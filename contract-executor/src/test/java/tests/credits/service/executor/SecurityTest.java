package tests.credits.service.executor;

import com.credits.general.thrift.generated.Variant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tests.credits.service.ContractExecutorTestContext;

import java.util.Arrays;
import java.util.Collection;

import static com.credits.general.thrift.generated.Variant._Fields.V_INT;
import static com.credits.general.thrift.generated.Variant._Fields.V_STRING;
import static java.io.File.separator;
import static org.junit.Assert.fail;
import static tests.credits.TestContract.SandboxTestContract;

public class SecurityTest extends ContractExecutorTestContext {
    private static final Logger LOGGER = LoggerFactory.getLogger(SecurityTest.class);
    private static final String prjDir = "\"" + System.getProperty("user.dir") + separator + "credits" + separator + "file.test" + "\"";

    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"openSocket", new Variant(V_INT, 5555), false},
                {"setTotal", new Variant(V_INT, 1000), false},
                {"getTotal", null, false},
                {"createFile", null, false},
                {"createFileInProjectDir", new Variant(V_STRING, prjDir), false},
                {"killProcess", null, false},
                {"newThread", null, false},
        });
    }

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
    }

    @ParameterizedTest(name = "{index} {0}")
    @MethodSource("data")
    public void test(String methodName, Variant arg, boolean errorExpected) {
        final var smartContact = smartContractsRepository.get(SandboxTestContract);
        final var contractState = deploySmartContract(smartContact).newContractState;
        try {
            executeSmartContract(
                    smartContact,
                    contractState,
                    methodName,
                    arg != null ? new Variant[][]{{arg}} : new Variant[][]{{}},
                    contractState);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            return;
        }
        if (errorExpected) {
            fail("error expected");
        }
    }
}
