package tests.credits.service.executor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pojo.ReturnValue;
import pojo.SmartContractMethodResult;
import tests.credits.SmartContactTestData;
import tests.credits.service.ContractExecutorTestContext;

import static com.credits.general.pojo.ApiResponseCode.FAILURE;
import static com.credits.general.util.variant.VariantConverter.VOID_TYPE_VALUE;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static tests.credits.TestContract.SmartContractV0TestImpl;

public class ExternalMethodsCallTest extends ContractExecutorTestContext {

    private final String calledSmartContractAddress = "5B3YXqDTcWQFGAqEJQJP3Bg1ZK8FFtHtgCiFLT5VAxpe";
    private SmartContactTestData smartContract;
    private byte[] deployContractState = null;

    @BeforeEach
    protected void setUp() throws Exception {
        super.setUp();
        smartContract = smartContractsRepository.get(SmartContractV0TestImpl);
        deployContractState = deploySmartContract(smartContract).newContractState;
    }

    @Test
    public void getter_method_must_not_change_state() {

        setNodeResponseGetSmartContractByteCode(smartContract, deployContractState, false);

        final ReturnValue returnValue = executeExternalSmartContract(
                smartContract,
                deployContractState,
                "externalCall",
                calledSmartContractAddress,
                "getTotal");

        final SmartContractMethodResult methodResult = returnValue.executeResults.get(0);

        assertThat(methodResult.status.message, is("success"));
        assertThat(methodResult.result.getV_int(), is(0));
        assertThat(returnValue.newContractState, equalTo(deployContractState));
        assertThat(
                returnValue.newContractState,
                equalTo(returnValue.externalSmartContracts.get(calledSmartContractAddress).getContractData().getContractState()));
    }

    @Test
    public void setter_method_must_return_new_states() {
        setNodeResponseGetSmartContractByteCode(smartContract, deployContractState, true);

        final ReturnValue returnValue = executeExternalSmartContract(
                smartContract,
                deployContractState,
                "externalCallChangeState",
                calledSmartContractAddress,
                "addTokens",
                10);

        final SmartContractMethodResult methodResult = returnValue.executeResults.get(0);

        assertThat(methodResult.status.message, is("success"));
        assertThat(returnValue.newContractState, equalTo(deployContractState));
        assertThat(
                returnValue.newContractState,
                not(equalTo(returnValue.externalSmartContracts.get(calledSmartContractAddress).getContractData().getContractState())));
    }

    @Test
    public void recursion_contract_call() {
        setNodeResponseGetSmartContractByteCode(smartContract, deployContractState, true);

        final ReturnValue returnValue = executeExternalSmartContract(
                smartContract,
                deployContractState,
                "recursionExternalContractSetterCall",
                10);

        final SmartContractMethodResult methodResult = returnValue.executeResults.get(0);

        assertThat(methodResult.status.message, is("success"));
        assertThat(methodResult.result.getV_int_box(), is(45));
        assertThat(returnValue.newContractState, not(equalTo(deployContractState)));
        assertThat(returnValue.externalSmartContracts.size(), is(1));
    }


    @Test
    public void passObjectToExternalCall() {
        setNodeResponseGetSmartContractByteCode(smartContract, deployContractState, true);

        final ReturnValue returnValue = executeExternalSmartContract(
                smartContract,
                deployContractState,
                "useObjectIntoParams");

        final SmartContractMethodResult methodResult = returnValue.executeResults.get(0);

        assertThat(methodResult.status.message, is("success"));
        assertThat(methodResult.result.getFieldValue(), is(VOID_TYPE_VALUE));
        assertThat(returnValue.newContractState, equalTo(deployContractState));
    }

    @Test
    public void invokePayableNotAllowedForCall() {

        setNodeResponseGetSmartContractByteCode(smartContract, deployContractState, false);

        final ReturnValue returnValue = executeExternalSmartContract(
                smartContract,
                deployContractState,
                "externalCall",
                "payable");

        final SmartContractMethodResult methodResult = returnValue.executeResults.get(0);

        assertThat(methodResult.status.code, is(FAILURE.code));
        assertThat(methodResult.status.message, containsString("payable method cannot be called"));
        assertThat(returnValue.newContractState, equalTo(deployContractState));
        assertThat(returnValue.externalSmartContracts.get(calledSmartContractAddress), nullValue());
    }

    @Test
    public void call_external_contract_into_constructor() {
        //TODO need implementation
    }
}
