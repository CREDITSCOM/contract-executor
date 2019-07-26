package tests.credits.service.executor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pojo.ReturnValue;
import pojo.SmartContractMethodResult;
import tests.credits.UseContract;
import tests.credits.service.ExternalMethodCallTestContext;

import static com.credits.general.pojo.ApiResponseCode.FAILURE;
import static com.credits.general.util.variant.VariantConverter.VOID_TYPE_VALUE;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static tests.credits.TestContract.SmartContractV0TestImpl;
import static tests.credits.TestContract.SmartContractV2TestImpl;

public class ExternalMethodsCallTest extends ExternalMethodCallTestContext {


    @Test
    @UseContract(SmartContractV0TestImpl)
    public void getter_method_must_not_change_state() {

        setNodeResponseGetSmartContractByteCode(smartContract, deployContractState, false);

        final int total = (int) executeExternalSmartContract(smartContract, deployContractState, "externalCall", calledSmartContractAddress, "getTotal");

        assertThat(total, is(10));
//        final SmartContractMethodResult methodResult = returnValue.executeResults.get(0);
//
//        assertThat(methodResult.status.message, is("success"));
//        assertThat(methodResult.result.getV_int(), is(0));
//        assertThat(returnValue.newContractState, equalTo(deployContractState));
//        assertThat(returnValue.newContractState,
//                equalTo(returnValue.externalSmartContracts.get(calledSmartContractAddress).getContractData().getContractState()));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
    public void setter_method_must_return_new_states() {
        setNodeResponseGetSmartContractByteCode(smartContract, deployContractState, true);

        executeExternalSmartContract(
                smartContract,
                deployContractState,
                "externalCallChangeState",
                calledSmartContractAddress,
                "addTokens",
                10);

        final SmartContractMethodResult methodResult = returnValue.executeResults.get(0);

        assertThat(methodResult.status.message, is("success"));
        assertThat(returnValue.newContractState, equalTo(deployContractState));
        assertThat(returnValue.newContractState,
                not(equalTo(returnValue.externalSmartContracts.get(calledSmartContractAddress).getContractData().getContractState())));
    }

    @Test
    @UseContract(SmartContractV0TestImpl)
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
    @UseContract(SmartContractV0TestImpl)
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
    @UseContract(SmartContractV0TestImpl)
    public void invokePayableNotAllowedForCall() {

        setNodeResponseGetSmartContractByteCode(smartContract, deployContractState, false);

        final ReturnValue returnValue = executeExternalSmartContract(
                smartContract,
                deployContractState,
                "externalCall",
                smartContract.getContractAddressBase58(),
                "payable");

        final SmartContractMethodResult methodResult = returnValue.executeResults.get(0);

        assertThat(methodResult.status.code, is(FAILURE.code));
        assertThat(methodResult.status.message, containsString("payable method cannot be called"));
        assertThat(returnValue.newContractState, equalTo(deployContractState));
        assertThat(returnValue.externalSmartContracts.get(calledSmartContractAddress), nullValue());
    }

    @Test
    @DisplayName("transactions emitted into external contract must returned into result")
    @UseContract(SmartContractV2TestImpl)
    void sendTransactionsIntoExternalSmartContract() {
        setNodeResponseGetSmartContractByteCode(smartContract, deployContractState, true);

        final var result = executeExternalSmartContract(
                smartContract,
                deployContractState,
                "externalCall",
                smartContract.getContractAddressBase58(),
                "createTwoTransactions");

        final var emittedTransactions = result.executeResults.get(0).emittedTransactions;

        assertThat(emittedTransactions.size(), is(0));
        verify(spyNodeApiExecService, times(2)).sendTransaction(anyLong(), anyString(), anyString(), anyDouble(), any());
    }

    @Test
    public void call_external_contract_into_constructor() {
        //TODO need implementation
    }
}
