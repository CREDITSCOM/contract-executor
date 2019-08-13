package tests.credits.thrift.utils;

import com.credits.general.classload.ByteCodeContractClassLoader;
import com.credits.general.thrift.generated.Variant;
import com.credits.general.util.compiler.InMemoryCompiler;
import com.credits.general.util.compiler.model.CompilationUnit;
import exception.ContractExecutorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pojo.SmartContractConstants;
import pojo.session.DeployContractSession;
import tests.credits.SmartContactTestData;
import tests.credits.TestContract;
import tests.credits.service.ContractExecutorTestContext;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.credits.service.contract.SmartContractAnalyzer.getContractVariables;
import static org.junit.Assert.*;


public class ContractExecutorUtilsTestContext extends ContractExecutorTestContext {

    private Object instanceWithVariables;
    private Object instanceWithoutVariables;
    private SmartContactTestData smartContract;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        smartContract = smartContractsRepository.get(TestContract.VariablesTestContract);
        instanceWithVariables = getInstance(smartContract.getSourceCode());
        String sourceCodeWithoutVariables =
                "public class SmartContractV0TestImpl extends com.credits.scapi.v0.SmartContract {\n" +
                        "    public SmartContractV0TestImpl() {\n" +
                        "    }\n" +
                        "}";
        instanceWithoutVariables = getInstance(sourceCodeWithoutVariables);
    }

    @Test
    public void getContractVariablesTest() throws ContractExecutorException {
        Map<String, Variant> map = getContractVariables(instanceWithVariables);
        assertNotNull(map);
        assertEquals("java.lang.String", map.get("nullField").getV_null());
        assertEquals(5, map.get("intField").getFieldValue());
        assertEquals(55, map.get("integerField").getFieldValue());
        assertEquals(5.55, map.get("doubleField").getFieldValue());
        assertEquals("some string value", map.get("stringField").getFieldValue());
        assertEquals(5, ((Variant) ((List) map.get("listIntegerField").getFieldValue()).get(0)).getFieldValue());
        assertTrue(((Set) map.get("setIntegerField").getFieldValue()).contains(new Variant(Variant._Fields.V_INT_BOX, 5)));
        assertEquals(
                new Variant(Variant._Fields.V_INT_BOX, 5),
                ((Map) map.get("mapStringIntegerField").getFieldValue()).get(new Variant(Variant._Fields.V_STRING, "string key")));

        //Checks returning null if no public variables exist in the executor
        assertNull(getContractVariables(instanceWithoutVariables));
    }

    private Object getInstance(String source) throws Exception {
        CompilationUnit compilationUnit = InMemoryCompiler.compileSourceCode(source).getUnits().get(0);
        SmartContractConstants.initSmartContractConstants(Thread.currentThread().getId(),
                                                          new DeployContractSession(0, "123", "123", smartContract.getByteCodeObjectDataList(), 0));
        Class<?> clazz = new ByteCodeContractClassLoader().loadClass(compilationUnit.getName(), compilationUnit.getByteCode());
        return clazz.getDeclaredConstructors()[0].newInstance();
    }
}
