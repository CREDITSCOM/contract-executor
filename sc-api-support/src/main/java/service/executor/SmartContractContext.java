package service.executor;


import pojo.ExternalSmartContract;

import java.util.Map;

public interface SmartContractContext {

    long getAccessId();

    String getContractAddress();

    Map<String, ExternalSmartContract> getUsedContracts();

    ClassLoader getContractClassLoader();
}
