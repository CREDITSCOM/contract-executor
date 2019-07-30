package com.credits.scapi.v2;

public interface BasicTokenStandard extends com.credits.scapi.v1.BasicTokenStandard {

    ObservableMap<? extends WalletAddress, ? extends Number> getTokenBalances();

}
