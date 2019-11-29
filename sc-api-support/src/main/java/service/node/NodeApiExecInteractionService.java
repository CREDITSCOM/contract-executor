package service.node;


import pojo.apiexec.GetSmartCodeResultData;
import pojo.apiexec.SmartContractGetResultData;

import java.math.BigDecimal;

public interface NodeApiExecInteractionService {

    byte[] getSeed(long accessId);

    GetSmartCodeResultData getSmartCode(long accessId, String addressBase58);

    @Deprecated
    void sendTransaction(long accessId, String source, String target, double amount, double fee, byte[] userData);

    void sendTransaction(long accessId, String source, String target, double amount, byte[] userData);

    int getWalletId(long accessId, String addressBase58);

    SmartContractGetResultData getExternalSmartContractByteCode(long accessId, String addressBase58);

    BigDecimal getBalance(String addressBase58);

    long getBlockchainTimeMills(long accessId);
}
