package tests.credits.service.executor;


import com.credits.client.executor.thrift.generated.apiexec.GetSeedResult;
import com.credits.client.node.thrift.generated.WalletBalanceGetResult;
import com.credits.general.thrift.generated.Variant;
import com.credits.general.util.GeneralConverter;
import com.credits.general.util.compiler.CompilationException;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import pojo.EmitTransactionData;
import pojo.ReturnValue;
import tests.credits.UseContract;
import tests.credits.service.ContractExecutorTestContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;

import static com.credits.general.pojo.ApiResponseCode.FAILURE;
import static com.credits.general.pojo.ApiResponseCode.SUCCESS;
import static com.credits.general.thrift.generated.Variant._Fields.V_INT;
import static com.credits.general.thrift.generated.Variant._Fields.V_VOID;
import static com.credits.general.util.variant.VariantConverter.VOID_TYPE_VALUE;
import static com.credits.general.util.variant.VariantConverter.toObject;
import static com.credits.utils.ContractExecutorServiceUtils.SUCCESS_API_RESPONSE;
import static java.nio.ByteBuffer.wrap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;
import static tests.credits.TestContract.*;

public class ContractExecutorTest extends ContractExecutorTestContext {

    @BeforeEach
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("void return value must be return V_VOID variant type")
    void returnVoidType() {
        ReturnValue returnValue = executeSmartContract(smartContract, deployContractState, "initialize");
        assertThat(returnValue.executeResults.get(0).result, is(new Variant(V_VOID, VOID_TYPE_VALUE)));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("getter method cannot change contract state")
    void getterMethodCanNotChangeContractState() {
        ReturnValue rv = executeSmartContract(smartContract, deployContractState, "getTotal");
        assertThat(deployContractState, equalTo(rv.newContractState));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("setter method should be change executor state")
    void saveStateSmartContract() {
        var newContractState = executeSmartContract(smartContract, deployContractState, "addTokens", 10).newContractState;
        assertThat(deployContractState, not(equalTo(newContractState)));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("different contract state must be return different field value")
    public void differentContractStateReturnDifferentResult() {
        var total = getFirstReturnValue(executeSmartContract(smartContract, deployContractState, "getTotal")).getV_int();
        assertThat(total, is(0));

        final var contractState = executeSmartContract(smartContract, deployContractState, "addTokens", 10).newContractState;

        total = getFirstReturnValue(executeSmartContract(smartContract, contractState, "getTotal")).getV_int();
        assertThat(total, is(10));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("initiator must be initialized")
    void initiatorInit() {
        String initiator = getFirstReturnValue(executeSmartContract(smartContract, deployContractState, "getInitiatorAddress")).getV_string();
        assertThat(initiator, is(initiatorAddressBase58));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("sendTransaction into smartContract must be call NodeApiExecService")
    void sendTransactionIntoContract() {
        executeSmartContract(smartContract, deployContractState, "createTransactionIntoContract", "10");
        verify(spyNodeApiExecService)
                .sendTransaction(accessId, initiatorAddressBase58, smartContract.getContractAddressBase58(), 10, new byte[0]);
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("getContractVariables must be return public variables of contract")
    void getContractVariablesTest() {
        Map<String, Variant> contractVariables = ceService.getContractVariables(smartContract.getByteCodeObjectDataList(), deployContractState);
        assertThat(contractVariables, IsMapContaining.hasEntry("total", new Variant(V_INT, 0)));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("returned value should be BigDecimal type")
    void getBalanceReturnBigDecimal() {
        final var expectedBigDecimalValue = new BigDecimal("19.5").setScale(18, RoundingMode.DOWN);

        when(nodeThriftApiExec.getBalance(any())).thenReturn(new WalletBalanceGetResult(SUCCESS_API_RESPONSE,
                                                                                        GeneralConverter.bigDecimalToAmount(expectedBigDecimalValue)));

        final var balance = toObject(getFirstReturnValue(executeSmartContract(smartContract, deployContractState, "getBalanceTest")));
        assertThat(balance, is(expectedBigDecimalValue));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("parallel multiple call changes the contract state only once")
    void multipleMethodCall() {
        final var newContractState = executeSmartContractMultiple(smartContract,
                                                                  deployContractState,
                                                                  "addTokens",
                                                                  new Object[][]{{10}, {10}, {10}, {10}}).newContractState;
        assertThat(newContractState, not(equalTo(deployContractState)));

        final var total = ceService.getContractVariables(smartContract.getByteCodeObjectDataList(), newContractState).get("total").getV_int();
        assertThat(total, is(10));
    }


    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("compile source code must return byteCodeDataObjects")
    void compileClassCall() throws CompilationException {
        final var byteCodeObjectData = ceService.compileContractClass(smartContract.getSourceCode());

        assertThat(byteCodeObjectData, is(smartContract.getByteCodeObjectDataList()));
    }


    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("compileContractClass must be return byteCodes list of root and internal classes")
    void compileContractTest() {
        final var result = ceService.compileContractClass(smartContract.getSourceCode());

        assertThat(result.size(), greaterThan(0));
        assertThat(result.get(0).getName(), is("SmartContractV0TestImpl$Geo"));
        assertThat(result.get(1).getName(), is("SmartContractV0TestImpl"));
    }

    @Test
    @DisplayName("compileContractClass must be throw compilation exception with explanations")
    void compileContractTest1() {
        Throwable exception = assertThrows(CompilationException.class, () -> ceService.compileContractClass("class MyContract {\n MyContract()\n}"));
        assertThat(exception.getMessage(), containsString("compilation errors in class MyContract :\n2:';' expected"));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("getSeed must be call NodeApiExecService")
    void getSeedCallIntoSmartContract() {
        final var expectedSeed = new byte[]{0xB, 0xA, 0xB, 0xE};

        when(nodeThriftApiExec.getSeed(anyLong())).thenReturn(new GetSeedResult(SUCCESS_API_RESPONSE, wrap(expectedSeed)));
        final var seed = getFirstReturnValue(executeSmartContract(smartContract, deployContractState, "testGetSeed")).getV_byte_array();

        assertThat(seed, is(expectedSeed));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("execution of smart-executor must be stop when execution time expired")
    void executionTimeTest() {
        final var executionStatus = executeSmartContract(smartContract, deployContractState, 10, "infiniteLoop").executeResults.get(0).status;

        assertThat(executionStatus.code, is(FAILURE.code));
        assertThat(executionStatus.message, containsString("TimeoutException"));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("correct interrupt smart executor if time expired")
    void correctInterruptContractIfTimeExpired() {
        final var executionResult = executeSmartContract(smartContract, deployContractState, 10, "interruptedInfiniteLoop").executeResults.get(0);

        assertThat(executionResult.status, is(SUCCESS_API_RESPONSE));
        assertThat(executionResult.result.getV_string(), is("infinite loop interrupted correctly"));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("wait a bit delay for correct complete smart executor method")
    void waitCorrectCompleteOfSmartContract() {
        final var executionResult =
                executeSmartContract(smartContract, deployContractState, 10, "interruptInfiniteLoopWithDelay").executeResults.get(0);

        assertThat(executionResult.status, is(SUCCESS_API_RESPONSE));
        assertThat(executionResult.result.getV_string(), is("infinite loop interrupted correctly"));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("executeByteCode must be return spent cpu time by execution method thread")
    void executeByteCodeMeasureCpuTimeByThread0() {
        var spentCpuTime = executeSmartContract(smartContract, deployContractState, 11, "nothingWorkOnlySleep").executeResults.get(0).spentCpuTime;
        assertThat(spentCpuTime, lessThan(1000_000L));

        spentCpuTime = executeSmartContract(smartContract, deployContractState, 11, "bitWorkingThenSleep").executeResults.get(0).spentCpuTime;
        assertThat(spentCpuTime, greaterThan(7_000_000L));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("exception into executeByteCode must be return fail status with exception message")
    void exceptionDuringExecution() {
        final var result = executeSmartContract(smartContract, deployContractState, 1, "thisMethodThrowsExcetion").executeResults.get(0);

        assertThat(result.status.code, is(FAILURE.code));
        assertThat(result.status.message, containsString("oops some problem"));
    }

    @Test
    @UseContract(value = TroubleConstructor, deploy = false)
    @DisplayName("exception into constructor must be return fail status with exception method")
    void constructorWithException() {
        final var result = deploySmartContract(smartContract).executeResults.get(0);

        assertThat(result.status.code, is(FAILURE.code));
        assertThat(result.status.message, containsString("some problem found here"));
    }

    @Test
    @UseContract(SmartContractV2TestImpl)
    @DisplayName("v2.SmartContract must be compiled and executable")
    void executePayableSmartContractV2() {
        final var result = executeSmartContract(smartContract, deployContractState, "payable", BigDecimal.ONE, new byte[0]).executeResults.get(0);

        assertThat(result.status.code, is(SUCCESS.code));
        assertThat(result.result.getV_string(), is("payable call successfully"));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("buildContractClass must be return list of classes")
    void buildContractClass() {
        final var result = ceService.buildContractClass(smartContract.getByteCodeObjectDataList());

        assertThat(result.size(), is(2));
        assertThat(result.get(0).getName(), is("SmartContractV0TestImpl$Geo"));
        assertThat(result.get(1).getName(), is("SmartContractV0TestImpl"));
    }

    @Test
    @UseContract(SmartContractV2TestImpl)
    @DisplayName("emitted transactions list must be returned into each execution result")
    void returnEmittedTransactions() {
        final var result = executeSmartContract(smartContract, deployContractState, "createTwoTransactions");
        final var emittedTransactions = result.executeResults.get(0).emittedTransactions;

        final var firstTransaction = new EmitTransactionData(initiatorAddressBase58, smartContract.getContractAddressBase58(), 10);
        final var secondTransaction = new EmitTransactionData(initiatorAddressBase58, smartContract.getContractAddressBase58(), 0.01,
                                                              "hello".getBytes());
        assertThat(emittedTransactions.size(), is(2));
        assertThat(emittedTransactions.get(0), is(firstTransaction));
        assertThat(emittedTransactions.get(1), is(secondTransaction));
    }

    @Test
    @UseContract(SmartContractV2TestImpl)
    @DisplayName("takeAwayEmittedTransactions must be call even exception occurred")
    void takeAwayTransactionsMustBeCalledAlways() {
        executeSmartContract(smartContract, deployContractState, "createTwoTransactionThenThrowException");

        verify(spyNodeApiExecService, times(2)).takeAwayEmittedTransactions(anyLong());
    }

    @Test
    @UseContract(SmartContractV2TestImpl)
    @DisplayName("emitted transaction from external contracts must returned into execution result")
    void returnEmittedTransactionsFromExternalContracts() {
        final var contractAddress = smartContract.getContractAddressBase58();

        final var result = executeSmartContract(smartContract,
                                                deployContractState,
                                                "externalCall",
                                                contractAddress,
                                                "createTwoTransactions");

        final var emittedTransactions = result.executeResults.get(0).emittedTransactions;

        final var firstTransaction = new EmitTransactionData(initiatorAddressBase58, smartContract.getContractAddressBase58(), 10);
        final var secondTransaction = new EmitTransactionData(initiatorAddressBase58, smartContract.getContractAddressBase58(), 0.01,
                                                              "hello".getBytes());
        assertThat(emittedTransactions.size(), is(2));
        assertThat(emittedTransactions.get(0), is(firstTransaction));
        assertThat(emittedTransactions.get(1), is(secondTransaction));
    }

    @Test
    @UseContract(SmartContractV2TestImpl)
    @DisplayName("invocation external contracts should change contract state")
    void changeExternalSmartContractStates() {
        final var externalContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var externalContractAddress = externalContract.getContractAddressBase58();
        final var deployExternalContractState = deploySmartContract(externalContract).newContractState;

        setNodeResponseGetSmartContractByteCode(externalContract, deployExternalContractState, true);

        final var result = executeSmartContract(smartContract,
                                                deployContractState,
                                                "externalCall",
                                                externalContractAddress,
                                                "add10Tokens");

        final var executionResult = result.executeResults.get(0);
        final var changedExternalContractState = result.externalSmartContracts.get(externalContractAddress).getContractData().getContractState();
        final var add10TokensReturnValue = getFirstReturnValue(result).getV_int_box();//fixme int_box instead int

        assertThat(executionResult.status, is(SUCCESS_API_RESPONSE));
        assertThat(add10TokensReturnValue, is(10));
        assertThat(changedExternalContractState, not(equalTo(deployExternalContractState)));
        assertThat(result.newContractState, equalTo(deployContractState));
    }


    @Test
    @UseContract(SmartContractV2TestImpl)
    @DisplayName("external contract state can't be change if exception occurred into the external contract ")
    void contractStateNotChangeIfExceptionOccurred() {
        final var externalContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var externalContractAddress = externalContract.getContractAddressBase58();
        final var deployExternalContractState = deploySmartContract(externalContract).newContractState;

        setNodeResponseGetSmartContractByteCode(externalContract, deployExternalContractState, true);

        final var result = executeSmartContract(smartContract,
                                                deployContractState,
                                                "externalCall",
                                                externalContractAddress,
                                                "unknownMethod");

        final var executionResult = result.executeResults.get(0);
        final var changedExternalContractState = result.externalSmartContracts.get(externalContractAddress).getContractData().getContractState();
        final var errorDescription = getFirstReturnValue(result).getV_string();

        assertThat(executionResult.status.code, is(FAILURE.code));
        assertThat(errorDescription, containsString("Cannot find a method by name and parameters specified."));
        assertThat(changedExternalContractState, equalTo(deployExternalContractState));
        assertThat(result.newContractState, equalTo(deployContractState));
    }

    @Test
    @UseContract(SmartContractV2TestImpl)
    @DisplayName("setter method into external contract can change external contract state")
    public void setterMethodMustReturnNewStates() {
        final var externalContractData = smartContractsRepository.get(SmartContractV0TestImpl);
        final var externalContractAddress = externalContractData.getContractAddressBase58();
        final var externalContractDeployState = deploySmartContract(SmartContractV0TestImpl).newContractState;

        setNodeResponseGetSmartContractByteCode(externalContractData, externalContractDeployState, true);

        final var result = executeSmartContract(
                smartContract,
                deployContractState,
                "externalCall",
                externalContractAddress,
                "addTokens",
                10);

        final var methodResult = result.executeResults.get(0);

        assertThat(methodResult.status.message, is("success"));
        assertThat(result.newContractState, equalTo(deployContractState));
        assertThat(result.externalSmartContracts.get(externalContractAddress), not(equalTo(externalContractDeployState)));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    @DisplayName("getter method into external smart contract can't change state")
    void getterMethodIntoExternalContractCanNotChangeState() {
        setNodeResponseGetSmartContractByteCode(smartContract, deployContractState, false);

        int total = (int) executeExternalSmartContract(smartContract,
                                                       deployContractState,
                                                       "externalCall",
                                                       smartContract.getContractAddressBase58(),
                                                       "getTotal");

        assertThat(total, is(0));

        final var contractState = executeSmartContract(smartContract, deployContractState, "addTokens", 10).newContractState;
        total = (int) executeExternalSmartContract(smartContract,
                                                   contractState,
                                                   "externalCall",
                                                   smartContract.getContractAddressBase58(),
                                                   "getTotal");

        assertThat(total, is(10));
    }

    @Test
    @DisplayName("transactions emitted into external contract must returned into result")
    @UseContract(SmartContractV2TestImpl)
    void sendTransactionsIntoExternalSmartContract() {
        setNodeResponseGetSmartContractByteCode(smartContract, deployContractState, true);

        final var result = executeSmartContract(
                smartContract,
                deployContractState,
                "externalCall",
                smartContract.getContractAddressBase58(),
                "createTwoTransactions");

        final var emittedTransactions = result.executeResults.get(0).emittedTransactions;

        verify(spyNodeApiExecService, times(2)).sendTransaction(anyLong(), anyString(), anyString(), anyDouble(), any());
        verify(spyNodeApiExecService, times(2)).takeAwayEmittedTransactions(anyLong());
        assertThat(emittedTransactions.size(), is(2));
    }

    @Test
    @DisplayName("payable not allowed to call into external contract")
    @UseContract(SmartContractV0TestImpl)
    public void invokePayableNotAllowedForCall() {

        setNodeResponseGetSmartContractByteCode(smartContract, deployContractState, false);

        final var calledSmartContractAddress = smartContract.getContractAddressBase58();
        final var result = executeSmartContract(
                smartContract,
                deployContractState,
                "externalCall",
                calledSmartContractAddress,
                "payable");
        final var methodResult = result.executeResults.get(0);

        assertThat(methodResult.status.code, is(FAILURE.code));
        assertThat(methodResult.status.message, containsString("payable method cannot be called"));
        assertThat(result.newContractState, equalTo(deployContractState));
    }

    @Test
    @DisplayName("recursion contract call must be use one contract state")
    @UseContract(SmartContractV0TestImpl)
    public void recursionContractCall() {
        setNodeResponseGetSmartContractByteCode(smartContract, deployContractState, true);

        final var returnValue = executeSmartContract(
                smartContract,
                deployContractState,
                "recursionExternalContractSetterCall",
                10);

        final var methodResult = returnValue.executeResults.get(0);

        assertThat(methodResult.status.message, is("success"));
        assertThat(methodResult.result.getV_int_box(), is(45));
        assertThat(returnValue.newContractState, not(equalTo(deployContractState)));
        assertThat(returnValue.externalSmartContracts.size(), is(1));
    }


    @Test
    @DisplayName("token name can't be called Credits, CS, etc")
    void cantBeUsedReservedTokenName() {
        // TODO: 2019-07-05 add implementation
    }
}

