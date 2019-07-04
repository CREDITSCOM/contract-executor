import com.credits.scapi.v2.*;

import java.math.BigDecimal;
import java.util.Arrays;

public class SmartContractV2 extends SmartContract {

    @Override
    public String payable(BigDecimal amount, byte[] userData){
        return "payable call successfully";
    }
}