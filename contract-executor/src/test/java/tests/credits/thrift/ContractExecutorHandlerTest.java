package tests.credits.thrift;

import com.credits.client.executor.thrift.generated.*;
import com.credits.general.pojo.ApiResponseData;
import com.credits.general.pojo.MethodDescriptionData;
import com.credits.general.thrift.generated.APIResponse;
import com.credits.general.thrift.generated.ByteCodeObject;
import com.credits.general.thrift.generated.ClassObject;
import com.credits.general.thrift.generated.Variant;
import com.credits.scapi.v2.SmartContract;
import com.credits.thrift.ContractExecutorHandler;
import exception.ContractExecutorException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pojo.EmitTransactionData;
import pojo.ExternalSmartContract;
import pojo.ReturnValue;
import pojo.SmartContractMethodResult;
import pojo.apiexec.SmartContractGetResultData;
import pojo.session.InvokeMethodSession;
import service.executor.ContractExecutorService;
import tests.credits.SmartContactTestData;
import tests.credits.TestContract;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import static com.credits.ApplicationProperties.API_VERSION;
import static com.credits.general.pojo.ApiResponseCode.FAILURE;
import static com.credits.general.pojo.ApiResponseCode.SUCCESS;
import static com.credits.general.thrift.generated.Variant._Fields.*;
import static com.credits.general.util.variant.VariantConverter.VOID_TYPE_VALUE;
import static com.credits.scapi.misc.TokenStandardId.*;
import static com.credits.utils.ApiExecClientPojoConverter.convertEmittedTransactionDataToEmittedTransaction;
import static com.credits.utils.ContractExecutorServiceUtils.SUCCESS_API_RESPONSE;
import static com.credits.utils.ContractExecutorServiceUtils.failureApiResponse;
import static java.nio.ByteBuffer.allocate;
import static java.nio.ByteBuffer.wrap;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static tests.credits.TestContract.*;
import static tests.credits.TestUtils.*;

public class ContractExecutorHandlerTest {

    private final Variant voidVariantResult = new Variant(V_VOID, VOID_TYPE_VALUE);

    @Inject
    Map<TestContract, SmartContactTestData> contracts;
    @Inject
    ContractExecutorService mockCEService;
    @Inject
    ContractExecutorHandler contractExecutorHandler;

    @BeforeEach
    void setUp() {
        DaggerCEHandlerTestComponent.builder().build().inject(this);
        when(mockCEService.deploySmartContract(any())).thenReturn(
                new ReturnValue(new byte[]{0xC, 0xA, 0xF, 0xE},
                                List.of(new SmartContractMethodResult(SUCCESS_API_RESPONSE, voidVariantResult, 10, emptyList())),
                                emptyMap()));
    }

    @Test
    @DisplayName("MethodHeaders can be null when deploy")
    void executeByteCodeTest0() {
        var deployResult = executeSmartContract(allocate(0), null);
        verify(mockCEService).deploySmartContract(any());
        assertThat(deployResult.status, is(SUCCESS_API_RESPONSE));
        assertThat(getInvokedContractState(deployResult).length > 0, is(true));
    }


    @Test
    @DisplayName("MethodHeaders can be empty when deploy")
    void executeByteCodeTest1() {
        var deployResult = executeSmartContract(allocate(0), emptyList());
        verify(mockCEService).deploySmartContract(any());

        assertEquals(SUCCESS_API_RESPONSE, deployResult.status);
        assertTrue(getInvokedContractState(deployResult).length > 0);
    }

    @Test
    @DisplayName("MethodHeaders can't be null when execute")
    void executeByteCodeTest2() {
        var contractState = deploySmartContract();
        var executeByteCodeResult = executeSmartContract(contractState, null);
        verify(mockCEService, never()).executeSmartContract(any());


        assertThat(executeByteCodeResult.status.code, is(FAILURE.code));
        assertThat(executeByteCodeResult.results.size(), is(0));
        assertThat(executeByteCodeResult.status.getMessage(), containsString("IllegalArgumentException: method headers list can't be null or empty"));
    }

    @Test
    @DisplayName("MethodHeaders can't be empty when execute")
    void executeByteCodeTest3() {
        var contractState = deploySmartContract();
        var executeByteCodeResult = executeSmartContract(contractState, emptyList());
        verify(mockCEService, never()).executeSmartContract(any());

        assertThat(executeByteCodeResult.status.code, is(FAILURE.code));
        assertThat(executeByteCodeResult.results.size(), is(0));
        assertThat(executeByteCodeResult.status.getMessage(), containsString("IllegalArgumentException: method headers list can't be null or empty"));
    }

    @Test
    @DisplayName("Amount ExecuteByteCodeResult must be equals amount methodHeaders")
    void executeByteCodeTest5() {
        var contractState = deploySmartContract();

        Variant getTokensVariantResult = new Variant(V_INT, 5);

        doAnswer(invocationOnMock -> {
            var methodName = ((InvokeMethodSession) invocationOnMock.getArgument(0)).methodName;
            switch (methodName) {
                case "getTotal":
                    return createSuccessResponse(getTokensVariantResult);
                case "addTokens":
                    return createSuccessResponse(voidVariantResult);
                default:
                    throw new ContractExecutorException("unknown method");
            }
        }).when(mockCEService).executeSmartContract(any());

        var executeByteCodeResult = executeSmartContract(contractState, List.of(new MethodHeader("getTotal", emptyList()),
                                                                                new MethodHeader("addTokens", List.of(getTokensVariantResult)),
                                                                                new MethodHeader("unknown", emptyList()),
                                                                                new MethodHeader("getTotal", emptyList())));

        assertThat(executeByteCodeResult.status, is(SUCCESS_API_RESPONSE));
        assertThat(executeByteCodeResult.results.size(), is(4));

        List<SetterMethodResult> results = executeByteCodeResult.getResults();

        assertThat(results.get(0).status, is(SUCCESS_API_RESPONSE));
        assertThat(results.get(0).ret_val, is(getTokensVariantResult));

        assertThat(results.get(1).status, is(SUCCESS_API_RESPONSE));
        assertThat(results.get(1).ret_val, is(voidVariantResult));

        assertThat(results.get(2).status.code, is(FAILURE.code));
        assertThat(results.get(2).status.message, containsString("ContractExecutorException: unknown method"));
        assertThat(results.get(2).ret_val, is(new Variant(V_STRING, "unknown method")));

        assertThat(results.get(3).status, is(SUCCESS_API_RESPONSE));
        assertThat(results.get(3).ret_val, is(getTokensVariantResult));
    }

    private ReturnValue createSuccessResponse(Variant result) {
        return new ReturnValue(
                new byte[1],
                List.of(new SmartContractMethodResult(SUCCESS_API_RESPONSE, result, 10, emptyList())),
                emptyMap());
    }

    @Test
    @DisplayName("Throw exception if version is not valid")
    @SuppressWarnings("unchecked")
    void checkAppVersionTest() {
        short invalidVersion = (short) (API_VERSION - 1);
        var contractState = deploySmartContract();

        assertVersionIsInvalid(executeSmartContract(contractState, List.of(new MethodHeader("getTotal", emptyList())), invalidVersion).getStatus());
        assertVersionIsInvalid(executeByteCodeMultiple(contractState, "getTotal", invalidVersion, emptyList()).getStatus());
        assertVersionIsInvalid(contractExecutorHandler.compileSourceCode(contracts.get(SmartContractV0TestImpl).getSourceCode(),
                                                                         invalidVersion).getStatus());
        assertVersionIsInvalid(contractExecutorHandler.getContractMethods(List.of(mock(ByteCodeObject.class)), invalidVersion).getStatus());
        assertVersionIsInvalid(contractExecutorHandler.getContractVariables(List.of(mock(ByteCodeObject.class)),
                                                                            contractState,
                                                                            invalidVersion).getStatus());
    }

    @Test
    @DisplayName("results with exceptions must returned with failure status")
    void executeByteCodeThrowException() {
        var contractState = deploySmartContract();

        when(mockCEService.executeSmartContract(any())).thenReturn(
                new ReturnValue(new byte[1],
                                List.of(new SmartContractMethodResult(failureApiResponse(new RuntimeException("oops some problem")),
                                                                      new Variant(V_STRING, "oops some problem"), 0L, emptyList())),
                                null));

        var result = executeSmartContract(contractState, List.of(new MethodHeader("methodThrowsException", emptyList())));

        assertThat(result.getStatus(), is(SUCCESS_API_RESPONSE));
        assertThat(result.getResults().get(0).status.code, is(FAILURE.code));
        assertThat(result.getResults().get(0).status.message, containsString("oops some problem"));
    }

    @Test
    @DisplayName("getContactMethods must return methods descriptions and token version id")
    void getContactMethodsTest() {
        final var smartContract = contracts.get(SmartContractV0TestImpl);
        final var basicStandardContract = contracts.get(BasicStandardTestImpl);
        final var extensionStandardContract = contracts.get(ExtensionTokenStandardTestImpl);

        final var methodDescriptionData = List.of(new MethodDescriptionData("void", "foo", emptyList(), emptyList()));
        when(mockCEService.getContractMethods(anyList())).thenReturn(methodDescriptionData);

        when(mockCEService.buildContractClass(basicStandardContract.getByteCodeObjectDataList())).thenReturn(List.of(basicStandardContract.getContractClass()));
        var result = contractExecutorHandler.getContractMethods(basicStandardContract.getByteCodeObjectList(), API_VERSION);
        assertThat(result.getMethods().size(), is(1));
        assertThat(result.getTokenStandard(), is(BASIC_STANDARD.getId()));

        when(mockCEService.buildContractClass(extensionStandardContract.getByteCodeObjectDataList())).thenReturn(List.of(extensionStandardContract.getContractClass()));
        result = contractExecutorHandler.getContractMethods(extensionStandardContract.getByteCodeObjectList(), API_VERSION);
        assertThat(result.getMethods().size(), is(1));
        assertThat(result.getTokenStandard(), is(EXTENSION_TOKEN_STANDARD_V1.getId()));

        when(mockCEService.buildContractClass(anyList())).thenReturn(List.of(SmartContract.class));
        result = contractExecutorHandler.getContractMethods(smartContract.getByteCodeObjectList(), API_VERSION);
        assertThat(result.getMethods().size(), is(1));
        assertThat(result.getTokenStandard(), is(NOT_A_TOKEN.getId()));
    }

    @Test
    @DisplayName("execute bytecode method must return new state into contractStates map")
    void executeByteCodeReturnContractState() {
        var contractData = contracts.get(SmartContractV0TestImpl);
        var contractState = new byte[]{0xC, 0xA, 0xF, 0xE};
        var returnContractState = new byte[]{0xB, 0xA, 0xB, 0xE};
        ExecuteByteCodeResult result = prepareMockCEServiceThenExecute(contractData, contractState, returnContractState);

        assertThat(result.getStatus().code, is(SUCCESS.code));
        assertThat(result.getResults().size(), is(2));
        assertThat(result.getResults().get(0).contractsState.get(contractData.getContractAddressBinary()).array(), is(returnContractState));
        assertThat(result.getResults().get(1).contractsState.get(contractData.getContractAddressBinary()).array(), is(returnContractState));
    }

    private ExecuteByteCodeResult prepareMockCEServiceThenExecute(SmartContactTestData contractData,
                                                                  byte[] contractState,
                                                                  byte[] returnContractState) {
        return prepareMockCEServiceThenExecute(contractData, contractState, returnContractState, emptyList());
    }

    @Test
    @DisplayName("execute bytecode method must return list emitted transactions per each execution")
    void executeByteCodeReturnListEmittedTransactions() {
        final var contractData = contracts.get(SmartContractV0TestImpl);
        final var contractState = new byte[]{0xC, 0xA, 0xF, 0xE};
        final var returnContractState = new byte[]{0xB, 0xA, 0xB, 0xE};
        final var emittedTransactions = List.of(new EmitTransactionData("initiatorAddress", "contractAddress", 1.0));
        final var expectedEmittedTransactions = convertEmittedTransactionDataToEmittedTransaction(emittedTransactions);

        prepareMockCEServiceThenExecute(contractData, contractState, returnContractState, emittedTransactions)
                .getResults().forEach(result -> assertThat(result.getEmittedTransactions(), is(expectedEmittedTransactions)));

    }

    private ExecuteByteCodeResult prepareMockCEServiceThenExecute(SmartContactTestData contractData,
                                                                  byte[] contractState,
                                                                  byte[] returnContractState,
                                                                  List<EmitTransactionData> emittedTransactions) {
        when(mockCEService.executeSmartContract(any()))
                .thenReturn(new ReturnValue(contractState,
                                            List.of(new SmartContractMethodResult(SUCCESS_API_RESPONSE, new Variant(), 0L, emittedTransactions)),
                                            Map.of(contractData.getContractAddressBase58(),
                                                   new ExternalSmartContract(new SmartContractGetResultData(new ApiResponseData(SUCCESS, "success"),
                                                                                                            contractData.getByteCodeObjectDataList(),
                                                                                                            returnContractState,
                                                                                                            true)))));


        return executeSmartContract(wrap(contractState),
                                    List.of(methodHeaderOf("addTokens", 10),
                                            methodHeaderOf("getTotal")));
    }

    private void assertVersionIsInvalid(APIResponse status) {
        assertThat(status.code, is(FAILURE.code));
        assertThat(status.getMessage(), containsString("IllegalArgumentException: Invalid version"));
    }

    private ExecuteByteCodeResult executeSmartContract(ByteBuffer contractState, List<MethodHeader> methodHeaders) {
        return executeSmartContract(contractState, methodHeaders, API_VERSION);
    }

    private ExecuteByteCodeMultipleResult executeByteCodeMultiple(ByteBuffer contractState,
                                                                  String methodName,
                                                                  short version,
                                                                  List<Variant>... params) {
        return contractExecutorHandler.executeByteCodeMultiple(1,
                                                               initiatorAddress,
                                                               new SmartContractBinary(contractAddress,
                                                                                       new ClassObject(contracts.get(SmartContractV0TestImpl).getByteCodeObjectList(),
                                                                                                       contractState),
                                                                                       false),
                                                               methodName,
                                                               List.of(params),
                                                               Long.MAX_VALUE,
                                                               version);
    }

    private ExecuteByteCodeResult executeSmartContract(ByteBuffer contractState, List<MethodHeader> methodHeaders, int version) {
        SmartContractBinary smartContractBinary = new SmartContractBinary(contractAddress,
                                                                          new ClassObject(contracts.get(SmartContractV0TestImpl).getByteCodeObjectList(),
                                                                                          contractState),
                                                                          true);
        return contractExecutorHandler.executeByteCode(1, initiatorAddress, smartContractBinary, methodHeaders, 500, (short) version);
    }


    private ByteBuffer deploySmartContract() {
        return executeSmartContract(allocate(0), emptyList()).getResults().get(0).getContractsState().get(contractAddress);
    }
}
