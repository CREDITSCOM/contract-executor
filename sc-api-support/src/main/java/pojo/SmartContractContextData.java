package pojo;

import service.executor.SmartContractContext;

import java.util.Map;

public class SmartContractContextData implements SmartContractContext {

    private final long accessId;
    private final String contractAddress;
    private final Map<String, ExternalSmartContract> usedContracts;
    private final ClassLoader classLoader;

    public SmartContractContextData(long accessId, String contractAddress, Map<String, ExternalSmartContract> usedContracts, ClassLoader classLoader) {
        this.accessId = accessId;
        this.contractAddress = contractAddress;
        this.usedContracts = usedContracts;
        this.classLoader = classLoader;
    }

    @Override
    public long getAccessId() {
        return accessId;
    }

    @Override
    public String getContractAddress() {
        return contractAddress;
    }

    @Override
    public Map<String, ExternalSmartContract> getUsedContracts() {
        return usedContracts;
    }

    @Override
    public ClassLoader getContractClassLoader() {
        return classLoader;
    }
}
