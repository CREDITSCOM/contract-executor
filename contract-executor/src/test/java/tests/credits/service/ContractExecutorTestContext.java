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
import tests.credits.UseContract;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

import static com.credits.general.pojo.ApiResponseCode.SUCCESS;
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
    private UseContract useContract;

    protected void setUp() throws Exception {
        DaggerTestComponent.builder().build().inject(this);
        when(ceService.getSmartContractClassLoader()).thenReturn(byteCodeContractClassLoader);
    }

    protected void setUp(TestInfo testInfo) throws Exception {
        setUp();
        selectTestContractFromAnnotation(testInfo);
    }

    private void selectTestContractFromAnnotation(TestInfo testInfo) {
        if (testInfo.getTags().contains(UseContract.class.getSimpleName())) {
            testInfo.getTestMethod().ifPresent(m -> {
                useContract = m.getAnnotation(UseContract.class);
                smartContract = smartContractsRepository.get(useContract.value());
                if(useContract.deploy()){
                    deployContractState = deploySmartContract(smartContract).newContractState;
                }
            });
        }
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
