package com.credits.service.db.leveldb;

import com.credits.leveldb.client.PoolData;
import com.credits.leveldb.client.TransactionData;
import com.credits.leveldb.client.data.TransactionFlowData;

import java.math.BigDecimal;
import java.util.List;

public interface LevelDbInteractionService {

    BigDecimal getBalance(String address, String currency) throws Exception;

    TransactionData getTransaction(String transactionId) throws Exception;

    List<TransactionData> getTransactions(String address, long offset, long limit) throws Exception;

    List<PoolData> getPoolList(long offset, long limit) throws Exception;

    PoolData getPool(String poolNumber) throws Exception;

    void transactionFlow(String hash, String innerId, String source, String target, BigDecimal total, String address, String signatureBASE64) throws Exception;

    void transactionFlowWithFee(TransactionFlowData transactionFlowData, TransactionFlowData transactionFlowDataFee, boolean checkBalance) throws Exception;
}
