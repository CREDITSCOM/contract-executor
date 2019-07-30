package com.credits.service.node.apiexec;

import com.credits.client.executor.thrift.generated.apiexec.GetSeedResult;
import com.credits.client.executor.thrift.generated.apiexec.SmartContractGetResult;
import com.credits.client.node.thrift.generated.WalletBalanceGetResult;
import com.credits.client.node.thrift.generated.WalletIdGetResult;
import com.credits.general.thrift.generated.Amount;
import com.credits.general.util.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.EmitTransactionData;
import pojo.apiexec.GetSmartCodeResultData;
import pojo.apiexec.SmartContractGetResultData;
import service.node.NodeApiExecStoreTransactionService;

import javax.inject.Inject;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

import static com.credits.client.node.util.NodeClientUtils.processApiResponse;
import static com.credits.general.util.GeneralConverter.amountToBigDecimal;
import static com.credits.general.util.GeneralConverter.decodeFromBASE58;
import static com.credits.utils.ApiExecClientPojoConverter.createSmartContractGetResultData;
import static java.util.Collections.emptyList;

public class NodeApiExecInteractionServiceImpl implements NodeApiExecStoreTransactionService {
    private final static Logger logger = LoggerFactory.getLogger(NodeApiExecInteractionServiceImpl.class);

    private final NodeThriftApiExec nodeClient;
    private final Map<Long, List<EmitTransactionData>> emittedTransactionsByThreadId = new ConcurrentHashMap<>();
    private ExecutorService cachedPool;

    @Inject
    public NodeApiExecInteractionServiceImpl(NodeThriftApiExec nodeApiClient, ExecutorService cachedPool) {
        nodeClient = nodeApiClient;
        this.cachedPool = cachedPool;
    }

    @Override
    public byte[] getSeed(long accessId) {
        return callService(() -> {
            logger.debug("getSeed: ---> accessId = {}", accessId);
            GetSeedResult seed = nodeClient.getSeed(accessId);
            processApiResponse(seed.getStatus());
            logger.debug("getSeed: <--- seed = {}", seed.getSeed());
            return seed.getSeed();
        });
    }

    @Override
    @Deprecated
    public GetSmartCodeResultData getSmartCode(long accessId, String addressBase58) {
        return null;
    }

    @Override
    public SmartContractGetResultData getExternalSmartContractByteCode(long accessId, String addressBase58) {
        logger.debug(String.format("getExternalSmartContractByteCode: ---> accessId = %s; addressBase58 = %s", accessId, addressBase58));
        SmartContractGetResult result = nodeClient.getSmartContractBinary(accessId, decodeFromBASE58(addressBase58));
        processApiResponse(result.getStatus());
        SmartContractGetResultData data = createSmartContractGetResultData(result);
        logger.debug("getExternalSmartContractByteCode: <--- contractStateHashCode={}|stateCanModify={}",
                     Arrays.hashCode(data.getContractState()),
                     data.isStateCanModify());
        return data;
    }

    @Deprecated
    @Override
    public void sendTransaction(long accessId, String source, String target, double amount, double fee, byte[] userData) {
        sendTransaction(accessId, source, target, amount, userData);
    }

    @Override
    public void sendTransaction(long accessId, String source, String target, double amount, byte[] userData) {
        final var id = Thread.currentThread().getId();
        final var emittedTransactions = emittedTransactionsByThreadId.computeIfAbsent(id, k -> new ArrayList<>());
        emittedTransactions.add(new EmitTransactionData(source, target, amount, userData));
    }

    @Override
    public int getWalletId(long accessId, String addressBase58) {
        logger.debug(String.format("getWalletId: ---> addressBase58 = %s", addressBase58));
        WalletIdGetResult result = nodeClient.getWalletId(accessId, decodeFromBASE58(addressBase58));
        processApiResponse(result.getStatus());
        logger.debug(String.format("getWalletId: <--- walletId = %s", result.getWalletId()));
        return result.getWalletId();
    }

    @Override
    public BigDecimal getBalance(String addressBase58) {
        return callService(() -> {
            logger.info(String.format("getBalance: ---> address = %s", addressBase58));
            WalletBalanceGetResult result = nodeClient.getBalance(decodeFromBASE58(addressBase58));
            processApiResponse(result.getStatus());
            Amount amount = result.getBalance();
            BigDecimal balance = amountToBigDecimal(amount);
            logger.info(String.format("getBalance: <--- balance = %s", balance));
            return balance;
        });
    }

    @Override
    public List<EmitTransactionData> takeAwayEmittedTransactions(long threadId) {
        return Optional.ofNullable(emittedTransactionsByThreadId.remove(threadId)).orElse(emptyList());
    }


    private <R> R callService(Function<R> method) {
        try {
            return cachedPool.submit(method::apply).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
