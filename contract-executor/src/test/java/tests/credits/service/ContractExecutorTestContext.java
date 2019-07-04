package tests.credits.service;

import com.credits.general.classload.ByteCodeContractClassLoader;
import com.credits.general.pojo.ApiResponseData;
import com.credits.general.thrift.generated.Variant;
import com.credits.scapi.v0.SmartContract;
import pojo.ExternalSmartContract;
import pojo.ReturnValue;
import pojo.apiexec.SmartContractGetResultData;
import pojo.session.DeployContractSession;
import pojo.session.InvokeMethodSession;
import service.executor.ContractExecutorService;
import service.node.NodeApiExecInteractionService;
import tests.credits.DaggerTestComponent;
import tests.credits.SmartContactTestData;
import tests.credits.TestContract;

import javax.inject.Inject;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.credits.general.pojo.ApiResponseCode.SUCCESS;
import static com.credits.general.util.Utils.rethrowUnchecked;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static tests.credits.TestUtils.variantArrayOf;

public abstract class ContractExecutorTestContext {

    private final ByteCodeContractClassLoader byteCodeContractClassLoader = new ByteCodeContractClassLoader();
    protected final String initiatorAddressBase58 = "5B3YXqDTcWQFGAqEJQJP3Bg1ZK8FFtHtgCiFLT5VAxpe";
    protected final long accessId = 0;

    @Inject
    protected ContractExecutorService ceService;
    @Inject
    protected Map<TestContract, SmartContactTestData> smartContractsRepository;

    protected NodeApiExecInteractionService mockNodeApiExecService;

    protected void setUp() throws Exception {
        DaggerTestComponent.builder().build().inject(this);
        ceService = spy(ceService);
        mockNodeApiExecService = mock(NodeApiExecInteractionService.class);
        initSmartContractStaticField("nodeApiService", mockNodeApiExecService);
        initSmartContractStaticField("contractExecutorService", ceService);
        when(ceService.getSmartContractClassLoader()).thenReturn(byteCodeContractClassLoader);
    }

    private void initSmartContractStaticField(String fieldName, Object value) {
        Class<?> contract = SmartContract.class;
        rethrowUnchecked(() -> {
            Field interactionService = contract.getDeclaredField(fieldName);
            interactionService.setAccessible(true);
            interactionService.set(null, value);
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
        when(mockNodeApiExecService.getExternalSmartContractByteCode(anyLong(), anyString()))
                .thenReturn(new SmartContractGetResultData(
                        new ApiResponseData(SUCCESS, "success"),
                        contractTestData.getByteCodeObjectDataList(),
                        contractState,
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
