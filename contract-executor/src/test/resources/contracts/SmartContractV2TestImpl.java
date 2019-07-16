import com.credits.scapi.v2.*;

import java.math.BigDecimal;
import java.util.Arrays;
import com.credits.scapi.annotations.*;

public class SmartContractV2 extends SmartContract {

    @Override
    public String payable(BigDecimal amount, byte[] userData){
        return "payable call successfully";
    }

    public void createTwoTransactions(){
        sendTransaction(initiator, contractAddress, 10);
        sendTransaction(initiator, contractAddress, 0.01, "hello".getBytes());
    }

    public void createTwoTransactionThenThrowException(){
        createTwoTransactions();
        throw new RuntimeException("some problem occured here");
    }

    public Object externalCall(@ContractAddress(id = 0) String address, @ContractMethod(id = 0) String method) {
        return invokeExternalContract(address, method);
    }
}