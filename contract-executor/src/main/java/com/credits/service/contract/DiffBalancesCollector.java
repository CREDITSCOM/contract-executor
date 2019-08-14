package com.credits.service.contract;

import com.credits.scapi.v2.BasicTokenStandard;
import com.credits.scapi.v2.MapChangeListener;
import com.credits.scapi.v2.WalletAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

import static java.util.Objects.requireNonNull;

class DiffBalancesCollector implements MapChangeListener<WalletAddress, Number>, Closeable {
    private final static Logger logger = LoggerFactory.getLogger(DiffBalancesCollector.class);
    private final String contractAddress;
    private final BasicTokenStandard token;
    private boolean subscribed;
    private Map<String, Number> balances = new HashMap<>();

    public DiffBalancesCollector(String contractAddress, BasicTokenStandard token) {
        this.contractAddress = contractAddress;
        this.token = token;
    }

    public void subscribe() {
        try {
            requireNonNull(token.getTokenBalances(), "getTokenBalances return null").addListener(this);
            subscribed = true;
        } catch (Throwable e) {
            logger.debug("can't subscribe contract {}. Reason {}", contractAddress, e.getMessage());
        }
    }

    public void unsubscribe(){
        if (subscribed) {
            try {
                token.getTokenBalances().removeListener(this);
            } catch (Throwable e) {
                logger.debug("can't unsubscribe contract {}. Reason {}", contractAddress, e.getMessage());
            }
            subscribed = false;
        }
    }

    public Map<String, Number> getBalances() {
        return balances;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    @Override
    public void onChanged(EntryChange<? extends WalletAddress, ? extends Number> entryChange) {
        try {
            balances.put(entryChange.getKey().getBase58Address(), entryChange.getNewValue());
        } catch (Throwable e) {
            logger.debug("notify balance error. Address:{}. Reason:{}", contractAddress, e.getMessage());
        }

    }

    @Override
    public void close() {
        unsubscribe();
    }
}
