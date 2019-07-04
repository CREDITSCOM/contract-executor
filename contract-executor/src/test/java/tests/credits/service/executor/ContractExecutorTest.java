package tests.credits.service.executor;


import com.credits.general.thrift.generated.Variant;
import com.credits.general.util.compiler.CompilationException;
import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pojo.ReturnValue;
import tests.credits.service.ContractExecutorTestContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static com.credits.general.pojo.ApiResponseCode.FAILURE;
import static com.credits.general.pojo.ApiResponseCode.SUCCESS;
import static com.credits.general.thrift.generated.Variant._Fields.V_INT;
import static com.credits.general.thrift.generated.Variant._Fields.V_VOID;
import static com.credits.general.util.variant.VariantConverter.VOID_TYPE_VALUE;
import static com.credits.general.util.variant.VariantConverter.toObject;
import static com.credits.utils.ContractExecutorServiceUtils.SUCCESS_API_RESPONSE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static tests.credits.TestContract.*;

public class ContractExecutorTest extends ContractExecutorTestContext {

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
    }

    @Test
    @DisplayName("void return value must be return V_VOID variant type")
    void returnVoidType() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var contractState = deploySmartContract(smartContract).newContractState;

        ReturnValue returnValue = executeSmartContract(smartContract, contractState, "initialize");
        assertThat(returnValue.executeResults.get(0).result, is(new Variant(V_VOID, VOID_TYPE_VALUE)));
    }

    @Test
    @DisplayName("getter method cannot change contract state")
    void getterMethodCanNotChangeContractState() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var contractState = deploySmartContract(smartContract).newContractState;

        ReturnValue rv = executeSmartContract(smartContract, contractState, "getTotal");
        assertThat(contractState, equalTo(rv.newContractState));
    }

    @Test
    @DisplayName("setter method should be change executor state")
    void saveStateSmartContract() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var initialContractState = deploySmartContract(smartContract).newContractState;

        var newContractState = executeSmartContract(smartContract, initialContractState, "addTokens", 10).newContractState;
        assertThat(initialContractState, not(equalTo(newContractState)));
    }

    @Test
    @DisplayName("different contract state must be return different field value")
    public void differentContractStateReturnDifferentResult() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        var contractState = deploySmartContract(smartContract).newContractState;

        var total = getFirstReturnValue(executeSmartContract(smartContract, contractState, "getTotal")).getV_int();
        assertThat(total, is(0));

        contractState = executeSmartContract(smartContract, contractState, "addTokens", 10).newContractState;

        total = getFirstReturnValue(executeSmartContract(smartContract, contractState, "getTotal")).getV_int();
        assertThat(total, is(10));
    }

    @Test
    @DisplayName("initiator must be initialized")
    void initiatorInit() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var contractState = deploySmartContract(smartContract).newContractState;

        String initiator = getFirstReturnValue(executeSmartContract(smartContract, contractState, "getInitiatorAddress")).getV_string();
        assertThat(initiator, is(initiatorAddressBase58));
    }

    @Test
    @DisplayName("sendTransaction into smartContract must be call NodeApiExecService")
    void sendTransactionIntoContract() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var contractState = deploySmartContract(smartContract).newContractState;

        executeSmartContract(smartContract, contractState, "createTransactionIntoContract", "10");
        verify(mockNodeApiExecService)
                .sendTransaction(accessId, initiatorAddressBase58, smartContract.getContractAddressBase58(), 10, 1.0, new byte[0]);
    }

    @Test
    @DisplayName("getContractVariables must be return public variables of contract")
    void getContractVariablesTest() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var contractState = deploySmartContract(smartContract).newContractState;

        Map<String, Variant> contractVariables = ceService.getContractVariables(smartContract.getByteCodeObjectDataList(), contractState);
        assertThat(contractVariables, IsMapContaining.hasEntry("total", new Variant(V_INT, 0)));
    }

    @Test
    @DisplayName("returned value should be BigDecimal type")
    void getBalanceReturnBigDecimal() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var contractState = deploySmartContract(smartContract).newContractState;
        final var expectedBigDecimalValue = new BigDecimal("19.5");

        when(mockNodeApiExecService.getBalance(anyString())).thenReturn(expectedBigDecimalValue);

        final var balance = toObject(getFirstReturnValue(executeSmartContract(smartContract, contractState, "getBalanceTest")));
        assertThat(balance, is(expectedBigDecimalValue));
    }

    @Test
    @DisplayName("parallel multiple call changes the contract state only once")
    void multipleMethodCall() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var initialContractState = deploySmartContract(smartContract).newContractState;

        final var newContractState = executeSmartContractMultiple(smartContract,
                                                                  initialContractState,
                                                                  "addTokens",
                                                                  new Object[][]{{10}, {10}, {10}, {10}}).newContractState;
        assertThat(newContractState, not(equalTo(initialContractState)));

        final var total = ceService.getContractVariables(smartContract.getByteCodeObjectDataList(), newContractState).get("total").getV_int();
        assertThat(total, is(10));
    }


    @Test
    @DisplayName("compile source code must return byteCodeDataObjects")
    void compileClassCall() throws CompilationException {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);

        final var byteCodeObjectData = ceService.compileContractClass(smartContract.getSourceCode());

        assertThat(byteCodeObjectData, is(smartContract.getByteCodeObjectDataList()));
    }


    @Test
    @DisplayName("compileContractClass must be return byteCodes list of root and internal classes")
    void compileContractTest() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);

        final var result = ceService.compileContractClass(smartContract.getSourceCode());

        assertThat(result.size(), greaterThan(0));
        assertThat(result.get(0).getName(), is("SmartContractV0TestImpl$Geo"));
        assertThat(result.get(1).getName(), is("SmartContractV0TestImpl"));
    }

    @Test
    @DisplayName("compileContractClass must be throw compilation exception with explanations")
    void compileContractTest1() {
        assertThrows(CompilationException.class, () -> ceService.compileContractClass("class MyContract {\n MyContract()\n}"));
    }

    @Test
    @DisplayName("getSeed must be call NodeApiExecService")
    void getSeedCallIntoSmartContract() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var contractState = deploySmartContract(smartContract).newContractState;
        final var expectedSeed = new byte[]{0xB, 0xA, 0xB, 0xE};

        when(mockNodeApiExecService.getSeed(anyLong())).thenReturn(expectedSeed);
        final var seed = getFirstReturnValue(executeSmartContract(smartContract, contractState, "testGetSeed")).getV_byte_array();

        assertThat(seed, is(expectedSeed));
    }

    @Test
    @DisplayName("execution of smart-executor must be stop when execution time expired")
    void executionTimeTest() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var contractState = deploySmartContract(smartContract).newContractState;

        final var executionStatus = executeSmartContract(smartContract, contractState, 10, "infiniteLoop").executeResults.get(0).status;

        assertThat(executionStatus.code, is(FAILURE.code));
        assertThat(executionStatus.message, containsString("TimeoutException"));
    }

    @Test
    @DisplayName("correct interrupt smart executor if time expired")
    void correctInterruptContractIfTimeExpired() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var contractState = deploySmartContract(smartContract).newContractState;

        final var executionResult = executeSmartContract(smartContract, contractState, 10, "interruptedInfiniteLoop").executeResults.get(0);

        assertThat(executionResult.status, is(SUCCESS_API_RESPONSE));
        assertThat(executionResult.result.getV_string(), is("infinite loop interrupted correctly"));
    }

    @Test
    @DisplayName("wait a bit delay for correct complete smart executor method")
    void waitCorrectCompleteOfSmartContract() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var contractState = deploySmartContract(smartContract).newContractState;

        final var executionResult =
                executeSmartContract(smartContract, contractState, 10, "interruptInfiniteLoopWithDelay").executeResults.get(0);

        assertThat(executionResult.status, is(SUCCESS_API_RESPONSE));
        assertThat(executionResult.result.getV_string(), is("infinite loop interrupted correctly"));
    }

    @Test
    @DisplayName("executeByteCode must be return spent cpu time by execution method thread")
    void executeByteCodeMeasureCpuTimeByThread0() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var contractState = deploySmartContract(smartContract).newContractState;

        var spentCpuTime = executeSmartContract(smartContract, contractState, 11, "nothingWorkOnlySleep").executeResults.get(0).spentCpuTime;
        assertThat(spentCpuTime, lessThan(1000_000L));

        spentCpuTime = executeSmartContract(smartContract, contractState, 11, "bitWorkingThenSleep").executeResults.get(0).spentCpuTime;
        assertThat(spentCpuTime, greaterThan(10_000_000L));
    }

    @Test
    @DisplayName("exception into executeByteCode must be return fail status with exception message")
    void exceptionDuringExecution() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        final var contractState = deploySmartContract(smartContract).newContractState;

        final var result = executeSmartContract(smartContract, contractState, 1, "thisMethodThrowsExcetion").executeResults.get(0);

        assertThat(result.status.code, is(FAILURE.code));
        assertThat(result.status.message, containsString("oops some problem"));
    }

    @Test
    @DisplayName("exception into constructor must be return fail status with exception method")
    void constructorWithException() throws IOException {
        final var smartContract = smartContractsRepository.get(TroubleConstructor);

        final var result = deploySmartContract(smartContract).executeResults.get(0);

        assertThat(result.status.code, is(FAILURE.code));
        assertThat(result.status.message, containsString("some problem found here"));
    }

    @Test
    @DisplayName("v2.SmartContract must be compiled and executable")
    void executePayableSmartContractV2() throws IOException {
        final var smartContract = smartContractsRepository.get(SmartContractV2TestImpl);
        final var contractState = deploySmartContract(smartContract).newContractState;

        final var result = executeSmartContract(smartContract, contractState, "payable", BigDecimal.ONE, new byte[0]).executeResults.get(0);

        assertThat(result.status.code, is(SUCCESS.code));
        assertThat(result.result.getV_string(), is("payable call successfully"));
    }

    @Test
    @DisplayName("buildContractClass must be return list of classes")
    void buildContractClass() {
        final var smartContract = smartContractsRepository.get(SmartContractV0TestImpl);

        final var result = ceService.buildContractClass(smartContract.getByteCodeObjectDataList());

        assertThat(result.size(), greaterThan(0));
        assertThat(result.get(0).getName(), is("SmartContractV0TestImpl$Geo"));
        assertThat(result.get(1).getName(), is("SmartContractV0TestImpl"));
    }

    @Test
    @DisplayName("token name can't be called Credits, CS, etc")
    void cantBeUsedReservedTokenName() {
        // TODO: 2019-07-05 add implementation
    }
}

