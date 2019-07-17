package tests.credits.service;

import com.credits.client.executor.thrift.generated.apiexec.SmartContractGetResult;
import com.credits.general.classload.ByteCodeContractClassLoader;
import com.credits.general.pojo.ApiResponseData;
import com.credits.general.thrift.generated.Variant;
import com.credits.service.node.apiexec.NodeThriftApiExec;
import com.credits.utils.ContractExecutorServiceUtils;
import org.junit.jupiter.api.TestInfo;
import pojo.ExternalSmartContract;
import pojo.ReturnValue;
import pojo.apiexec.SmartContractGetResultData;
import pojo.session.DeployContractSession;
import pojo.session.InvokeMethodSession;
import service.executor.ContractExecutorService;
import service.node.NodeApiExecStoreTransactionService;
import tests.credits.DaggerTestComponent;
import tests.credits.SmartContactTestData;
import tests.credits.TestContract;
import tests.credits.UseTestContract;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.credits.general.pojo.ApiResponseCode.SUCCESS;
import static com.credits.general.util.Utils.rethrowUnchecked;
import static com.credits.service.BackwardCompatibilityService.allVersionsSmartContractClass;
import static java.nio.ByteBuffer.wrap;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static tests.credits.TestUtils.variantArrayOf;

public abstract class ContractExecutorTestContext {

    private final ByteCodeContractClassLoader byteCodeContractClassLoader = new ByteCodeContractClassLoader();
    protected final String initiatorAddressBase58 = "5B3YXqDTcWQFGAqEJQJP3Bg1ZK8FFtHtgCiFLT5VAxpe";
    protected final long accessId = 0;
    protected SmartContactTestData smartContract;
    protected byte[] deployContractState = null;

    @Inject
    protected ContractExecutorService ceService;
    @Inject
    protected Map<TestContract, SmartContactTestData> smartContractsRepository;
    @Inject
    protected NodeApiExecStoreTransactionService spyNodeApiExecService;
    @Inject
    protected NodeThriftApiExec nodeThriftApiExec;

    protected void setUp() throws Exception {
        DaggerTestComponent.builder().build().inject(this);
        when(ceService.getSmartContractClassLoader()).thenReturn(byteCodeContractClassLoader);
    }

    protected void setUp(TestInfo testInfo) throws Exception{
        setUp();
        selectTestContractFromAnnotation(testInfo);
    }

    private void selectTestContractFromAnnotation(TestInfo testInfo) {
        if (testInfo.getTags().contains(UseTestContract.class.getSimpleName())) {
            testInfo.getTestMethod().ifPresent(m -> {
                final var usingContract = m.getAnnotation(UseTestContract.class).value();
                selectSmartContractAndDeploy(usingContract);
            });
        }
    }

    private void selectSmartContractAndDeploy(TestContract testContract) {
        smartContract = smartContractsRepository.get(testContract);
        deployContractState = deploySmartContract(smartContract).newContractState;
    }

    private void initSmartContractStaticField(String fieldName, Object value) {
        allVersionsSmartContractClass.forEach(contract -> {
            rethrowUnchecked(() -> {
                Field interactionService = contract.getDeclaredField(fieldName);
                interactionService.setAccessible(true);
                interactionService.set(null, value);
            });
        });
    }

    protected ReturnValue deploySmartContract(TestContract smartContract) {
        return deploySmartContract(smartContractsRepository.get(smartContract));
    }

    protected ReturnValue deploySmartContract(SmartContactTestData smartContract) {
        return ceService.deploySmartContract(new DeployContractSession(
                accessId,
                initiatorAddressBase58,
                smartContract.getContractAddressBase58(),
                smartContract.getByteCodeObjectDataList(),
                Long.MAX_VALUE));
    }

    protected ReturnValue executeExternalSmartContract(SmartContactTestData smartContract,
                                                       byte[] contractState,
                                                       String methodName,
                                                       Object... params) {
        Map<String, ExternalSmartContract> usedContracts = new HashMap<>();
        usedContracts.putIfAbsent(smartContract.getContractAddressBase58(),
                                  new ExternalSmartContract(new SmartContractGetResultData(new ApiResponseData(SUCCESS, ""),
                                                                                           smartContract.getByteCodeObjectDataList(),
                                                                                           contractState,
                                                                                           true)));

        return ceService.executeExternalSmartContract(
                initInvokeMethodSession(smartContract, contractState, Long.MAX_VALUE, methodName, variantArrayOf(params)),
                usedContracts,
                byteCodeContractClassLoader);
    }

    protected ReturnValue executeSmartContract(SmartContactTestData smartContract, byte[] contractState, String methodName, Object... params) {
        return ceService.executeSmartContract(initInvokeMethodSession(smartContract,
                                                                      contractState,
                                                                      Long.MAX_VALUE,
                                                                      methodName,
                                                                      variantArrayOf(params)));
    }

    protected ReturnValue executeSmartContract(SmartContactTestData smartContract,
                                               byte[] contractState,
                                               long executionTime,
                                               String methodName,
                                               Object... params) {
        return ceService.executeSmartContract(initInvokeMethodSession(smartContract,
                                                                      contractState,
                                                                      executionTime,
                                                                      methodName,
                                                                      variantArrayOf(params)));
    }

    protected ReturnValue executeSmartContractMultiple(SmartContactTestData smartContract,
                                                       byte[] contractState,
                                                       String methodName,
                                                       Object[]... params) {
        return ceService.executeSmartContract(initInvokeMethodSession(smartContract,
                                                                      contractState,
                                                                      Long.MAX_VALUE,
                                                                      methodName,
                                                                      variantArrayOf(params)));
    }


    protected void setNodeResponseGetSmartContractByteCode(SmartContactTestData contractTestData, byte[] contractState, boolean isCanModify) {
        when(nodeThriftApiExec.getSmartContractBinary(anyLong(), any()))
                .thenReturn(new SmartContractGetResult(ContractExecutorServiceUtils.SUCCESS_API_RESPONSE,
                                                       contractTestData.getByteCodeObjectList(),
                                                       wrap(contractState),
                                                       isCanModify));
    }

    private InvokeMethodSession initInvokeMethodSession(SmartContactTestData smartContact,
                                                        byte[] contractState,
                                                        long executionTime,
                                                        String methodName,
                                                        Variant[][] variantParams) {
        return new InvokeMethodSession(
                0,
                initiatorAddressBase58,
                smartContact.getContractAddressBase58(),
                smartContact.getByteCodeObjectDataList(),
                contractState,
                methodName,
                variantParams,
                executionTime);
    }

    protected Variant getFirstReturnValue(ReturnValue executionResult) {
        return executionResult.executeResults.get(0).result;
    }
}
