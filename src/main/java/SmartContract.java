import com.credits.common.utils.Converter;
import com.credits.common.utils.Utils;
import com.credits.crypto.Ed25519;
import com.credits.exception.ContractExecutorException;
import com.credits.leveldb.client.PoolData;
import com.credits.leveldb.client.TransactionData;
import com.credits.serialise.Serializer;
import com.credits.service.db.leveldb.LevelDbInteractionService;

import java.io.File;
import java.io.Serializable;
import java.security.PrivateKey;
import java.util.List;
import java.util.UUID;

public abstract class SmartContract implements Serializable {

    private static final String SYS_TRAN_PUBLIC_KEY_BASE64 = "accXpfvxnZa8txuxpjyPqzBaqYPHqYu2rwn34lL8rjI=";

    protected static LevelDbInteractionService service;

    protected double total = 0;

    private String specialProperty;

    protected SmartContract() {
        File propertySerFile = Serializer.getPropertySerFile();
        String property;
        try {
            property = (String) Serializer.deserialize(propertySerFile, ClassLoader.getSystemClassLoader());
        } catch (ContractExecutorException e) {
            throw new RuntimeException(e);
        }
        this.specialProperty = property;
        propertySerFile.delete();
    }

    protected Double getBalance(String address, String currency) {
        try {
            return service.getBalance(address, currency);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected TransactionData getTransaction(String transactionId) {
        try {
            return service.getTransaction(transactionId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected List<TransactionData> getTransactions(String address, long offset, long limit) {
        try {
            return service.getTransactions(address, offset, limit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected List<PoolData> getPoolList(long offset, long limit) {
        try {
            return service.getPoolList(offset, limit);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected PoolData getPool(String poolNumber) {
        try {
            return service.getPool(poolNumber);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void sendTransaction(String source, String target, Double amount, String currency) {
        String hash = Utils.randomAlphaNumeric(8);
        String innerId = UUID.randomUUID().toString();

        try {
            byte[] privateKeyByteArr = Converter.decodeFromBASE64(this.specialProperty);
            PrivateKey privateKey = Ed25519.bytesToPrivateKey(privateKeyByteArr);
            String signatureBASE64 =
                Ed25519.generateSignOfTransaction(hash, innerId, source, target, amount, currency, privateKey);
            service.transactionFlow(hash, innerId, source, target, amount, currency, signatureBASE64);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void sendTransactionSystem(Double amount, String currency) throws Exception {
        byte[] privateKeyByteArr = Converter.decodeFromBASE64(this.specialProperty);
        byte[] publicKeyByteArr = Utils.parseSubarray(privateKeyByteArr, 32, 32);
        String target = Converter.encodeToBASE64(publicKeyByteArr);

        sendTransaction(SmartContract.SYS_TRAN_PUBLIC_KEY_BASE64, target, amount, currency);
    }
}
