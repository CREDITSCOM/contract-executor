import com.credits.scapi.annotations.ContractAddress;
import com.credits.scapi.annotations.ContractMethod;
import com.credits.scapi.annotations.UsingContract;
import java.lang.Integer;
import java.math.BigDecimal;

import com.credits.scapi.v0.SmartContract;
import com.credits.scapi.v1.ExtensionTokenStandard;

public class MySmartContract implements ExtensionTokenStandard {

    @Override
    public void register() {

    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getSymbol() {
        return null;
    }

    @Override
    public int getDecimal() {
        return 0;
    }

    @Override
    public boolean setFrozen(boolean frozen) {
        return false;
    }

    @Override
    public BigDecimal totalSupply() {
        return null;
    }

    @Override
    public BigDecimal balanceOf(String owner) {
        return null;
    }

    @Override
    public BigDecimal allowance(String owner, String spender) {
        return null;
    }

    @Override
    public boolean transfer(String to, BigDecimal amount) {
        return false;
    }

    @Override
    public boolean transferFrom(String from, String to, BigDecimal amount) {
        return false;
    }

    @Override
    public void approve(String spender, BigDecimal amount) {

    }

    @Override
    public boolean burn(BigDecimal amount) {
        return false;
    }

    @Override
    public String payable(BigDecimal amount, byte[] userData) {
        return null;
    }
}
