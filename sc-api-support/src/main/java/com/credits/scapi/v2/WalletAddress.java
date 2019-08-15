package com.credits.scapi.v2;

import java.io.Serializable;

public interface WalletAddress extends Serializable {

    String getBase58Address();

}
