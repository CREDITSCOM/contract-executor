package com.credits.cst;

import com.credits.scapi.annotations.ContractAddress;
import com.credits.scapi.annotations.ContractMethod;
import com.credits.scapi.implemetation.TokenBalances;
import com.credits.scapi.v2.BasicTokenStandard;
import com.credits.scapi.v2.ObservableMap;
import com.credits.scapi.v2.SmartContract;
import com.credits.scapi.v2.WalletAddress;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static java.math.BigDecimal.ZERO;
import static java.math.RoundingMode.DOWN;
import static com.credits.scapi.implemetation.Address.addressOf;

public class BasicTokenStandardV2Impl extends SmartContract implements BasicTokenStandard {

    private final String owner;
    private final int decimal;
    private final TokenBalances<WalletAddress, BigDecimal> balances;
    private final String name;
    private final String symbol;
    private BigDecimal totalCoins;
    private BigDecimal freeCoins;
    private HashMap<String, Map<String, BigDecimal>> allowed;
    private boolean frozen;

    public BasicTokenStandardV2Impl() {
        super();
        name = "CreditsToken";
        symbol = "CST";
        decimal = 3;
        totalCoins = new BigDecimal(10_000_000).setScale(decimal, DOWN);
        freeCoins = new BigDecimal(9_000_000).setScale(decimal, DOWN);
        owner = initiator;
        allowed = new HashMap<>();
        balances = new TokenBalances<>();
        balances.put(addressOf(owner), new BigDecimal(1_000_000L).setScale(decimal, DOWN));
    }

    @Override
    public int getDecimal() {
        return decimal;
    }

    @Override
    public boolean setFrozen(boolean isFrozen) {
        if (!initiator.equals(owner)) {
            throw new RuntimeException("unable change frozen state! The wallet " + initiator + " is not owner");
        }
        this.frozen = isFrozen;
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getSymbol() {
        return symbol;
    }

    @Override
    public BigDecimal totalSupply() {
        return totalCoins;
    }

    @Override
    public BigDecimal balanceOf(String owner) {
        return getTokensBalance(owner);
    }

    @Override
    public BigDecimal allowance(String owner, String spender) {
        if (allowed.get(owner) == null) {
            return ZERO;
        }
        BigDecimal amount = allowed.get(owner).get(spender);
        return amount != null
               ? amount
               : ZERO;
    }

    @Override
    public boolean transfer(String to, BigDecimal amount) {
        contractIsNotFrozen();
        if (!to.equals(initiator)) {
            BigDecimal sourceBalance = getTokensBalance(initiator);
            BigDecimal targetTokensBalance = getTokensBalance(to);
            if (sourceBalance.compareTo(amount) < 0) {
                throw new RuntimeException("the wallet " + initiator + " doesn't have enough tokens to transfer");
            }
            balances.put(addressOf(initiator), sourceBalance.subtract(amount));
            balances.put(addressOf(to), targetTokensBalance.add(amount));
        }
        return true;
    }

    @Override
    public boolean transferFrom(String from, String to, BigDecimal amount) {
        contractIsNotFrozen();

        if (!from.equals(to)) {
            BigDecimal sourceBalance = getTokensBalance(from);
            BigDecimal targetTokensBalance = getTokensBalance(to);
            if (sourceBalance.compareTo(amount) < 0)
                throw new RuntimeException("unable transfer tokens! The balance of " + from + " less then " + amount);

            Map<String, BigDecimal> spender = allowed.get(from);
            if (spender == null || !spender.containsKey(initiator))
                throw new RuntimeException("unable transfer tokens! The wallet " + from + " not allow transfer tokens for " + to);

            BigDecimal allowTokens = spender.get(initiator);
            if (allowTokens.compareTo(amount) < 0) {
                throw new RuntimeException("unable transfer tokens! Not enough allowed tokens. For the wallet " + initiator + " allow only " + allowTokens + " tokens");
            }

            spender.put(initiator, allowTokens.subtract(amount));
            balances.put(addressOf(from), sourceBalance.subtract(amount));
            balances.put(addressOf(to), targetTokensBalance.add(amount));
        }
        return true;
    }

    @Override
    public void approve(String spender, BigDecimal amount) {
        Map<String, BigDecimal> initiatorSpenders = allowed.get(initiator);
        if (initiatorSpenders == null) {
            Map<String, BigDecimal> newSpender = new HashMap<>();
            newSpender.put(spender, amount);
            allowed.put(initiator, newSpender);
        } else {
            BigDecimal spenderAmount = initiatorSpenders.get(spender);
            initiatorSpenders.put(spender,
                                  spenderAmount == null
                                  ? amount
                                  : spenderAmount.add(amount));
        }
    }

    @Override
    public boolean burn(BigDecimal amount) {
        contractIsNotFrozen();
        BigDecimal sourceBalance = getTokensBalance(initiator);
        checkNegative(amount);
        if (sourceBalance.compareTo(amount) < 0) {
            throw new RuntimeException(String.format("the wallet %s doesn't have enough tokens to burn", initiator));
        }
        totalCoins = totalCoins.subtract(amount);
        balances.put(addressOf(initiator), sourceBalance.subtract(amount));
        return true;
    }

    public String payable(BigDecimal amount, byte[] userData) {
        contractIsNotFrozen();
        if (freeCoins.compareTo(amount) < 0) throw new RuntimeException("not enough tokes to buy");
        balances.put(addressOf(initiator), Optional.ofNullable(balances.get(addressOf(initiator))).orElse(ZERO).add(amount));
        freeCoins = freeCoins.subtract(amount);
        return "success.";
    }

    public void surprise(){
        balances.compute(addressOf(owner), (addr, balance) -> balance.subtract(BigDecimal.ONE));
        if(balances.containsKey(addressOf(initiator))) {
            balances.compute(addressOf(initiator), (addr, balance) -> balance.add(BigDecimal.ONE));
        }else{
            balances.computeIfAbsent(addressOf(initiator), (addr) -> BigDecimal.ONE);
        }
    }

    public Object burnOneThenExternalCall(@ContractAddress(id = 0) String address, @ContractMethod(id = 0) String method) {
        burnOneToken();
        return externalCall(address, method);
    }

    public void burnOneToken(){
        burn(BigDecimal.ONE);
    }

    public Object externalCall(@ContractAddress(id = 0) String address, @ContractMethod(id = 0) String method) {
        return invokeExternalContract(address, method);
    }


    private void contractIsNotFrozen() {
        if (frozen) throw new RuntimeException("unavailable action! The smart-contract is frozen");
    }

    private BigDecimal getTokensBalance(String address) {
        return Optional.ofNullable(balances.get(addressOf(address))).orElseGet(() -> {
            balances.put(addressOf(address), ZERO.setScale(decimal, DOWN));
            return ZERO.setScale(decimal, DOWN);
        });
    }

    private void checkNegative(BigDecimal value) {
        if (value.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException("the amount cannot be negative");
        }
    }

    @Override
    public ObservableMap<? extends WalletAddress, ? extends Number> getTokenBalances() {
        return balances;
    }
}
