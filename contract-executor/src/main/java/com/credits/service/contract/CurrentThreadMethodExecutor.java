package com.credits.service.contract;

import com.credits.general.classload.ByteCodeContractClassLoader;
import com.credits.service.node.apiexec.NodeApiExecInteractionServiceImpl;
import exception.ContractExecutorException;
import exception.ExternalSmartContractException;
import org.apache.commons.lang3.exception.ExceptionUtils;
import pojo.ExternalSmartContract;
import service.executor.SmartContractContext;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static com.credits.general.serialize.Serializer.deserialize;
import static com.credits.general.serialize.Serializer.serialize;
import static com.credits.ioc.Injector.INJECTOR;
import static com.credits.thrift.utils.ContractExecutorUtils.loadClassesToClassloader;
import static com.credits.utils.ContractExecutorServiceUtils.initNonStaticContractFields;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static org.apache.commons.beanutils.MethodUtils.getMatchingAccessibleMethod;

public class CurrentThreadMethodExecutor {
    @Inject
    NodeApiExecInteractionServiceImpl nodeApiExecService;
    private final long accessId;
    private final Map<String, ExternalSmartContract> usedContracts;
    private final String currentContractAddress;
    private final String invokingContractAddress;
    private final ByteCodeContractClassLoader classLoader;
    private final String methodName;
    private final Object[] params;

    private CurrentThreadMethodExecutor(SmartContractContext contractContext, String invokingContractAddress, String methodName, Object[] params) {
        INJECTOR.component.inject(this);
        accessId = contractContext.getAccessId();
        usedContracts = requireNonNull(contractContext.getUsedContracts(), "usedContracts is null");
        currentContractAddress = contractContext.getContractAddress();
        classLoader = (ByteCodeContractClassLoader) contractContext.getContractClassLoader();
        this.invokingContractAddress = invokingContractAddress;
        this.methodName = methodName;
        this.params = params;
    }

    static CurrentThreadMethodExecutor createCurrentThreadMethodExecutor(SmartContractContext contractContext,
                                                                         String invokingContractAddress,
                                                                         String method,
                                                                         Object[] params) {
        return new CurrentThreadMethodExecutor(contractContext, invokingContractAddress, method, params);
    }

    public Object execute() {
        final var invokingContractData = getInvokingContractData(accessId, invokingContractAddress, usedContracts);
        final var instance = getInstance(invokingContractData);
        final var returnValue = findMethodThenInvoke(instance);
        final var newContractState = serialize(instance);
        verifyChangesContractState(currentContractAddress, invokingContractData, newContractState);
        invokingContractData.getContractData().setContractState(newContractState);
        return returnValue;
    }

    private Object findMethodThenInvoke(Object instance) {
        try {
            final var method = getMatchingAccessibleMethod(instance.getClass(),
                                                           methodName,
                                                           stream(params).map(Object::getClass).toArray(Class[]::new));
            return Optional.ofNullable(method)
                    .orElseThrow(() -> new NoSuchMethodException("Cannot find a method by name and parameters specified."))
                    .invoke(instance, params);
        } catch (Throwable e) {
            throw new ExternalSmartContractException(ExceptionUtils.getRootCauseMessage(e) + ". Contract address: " + invokingContractAddress + ". " +
                                                             "Method:" + methodName + ". Args: " + Arrays.toString(params));
        }
    }

    private Object getInstance(ExternalSmartContract invokingContract) {
        var instance = invokingContract.getInstance();
        return instance == null
               ? createInstance(invokingContract)
               : instance;
    }

    private Object createInstance(ExternalSmartContract invokingContract) {
        Object instance;
        requireNonNull(classLoader, "can't get contract classloader");
        loadClassesToClassloader(invokingContract.getContractData().getByteCodeObjects(), classLoader);
        final var contractState = invokingContract.getContractData().getContractState();
        instance = deserialize(contractState, classLoader);
        initNonStaticContractFields(accessId, currentContractAddress, usedContracts, instance);
        usedContracts.get(invokingContractAddress).setInstance(instance);
        return instance;
    }

    private void verifyChangesContractState(String currentContractAddress, ExternalSmartContract invokingContract, byte[] newContractState) {
        final var contractStateCanNotBeModify = !invokingContract.getContractData().isStateCanModify();
        final var oldContractState = invokingContract.getContractData().getContractState();
        if (contractStateCanNotBeModify && !Arrays.equals(oldContractState, newContractState)) {
            throw new ContractExecutorException("smart executor \"" + currentContractAddress + "\" can't be modify");
        }
    }

    private ExternalSmartContract getInvokingContractData(long accessId,
                                                          String invokingContractAddress,
                                                          Map<String, ExternalSmartContract> usedContracts) {
        final var usedContract = usedContracts.containsKey(invokingContractAddress)
                                 ? usedContracts.get(invokingContractAddress)
                                 : new ExternalSmartContract(nodeApiExecService.getExternalSmartContractByteCode(accessId, invokingContractAddress));
        if (usedContract == null) throw new InternalError("can't get invokingExternalContract \"" + invokingContractAddress + "\"");
        usedContracts.put(invokingContractAddress, usedContract);
        return usedContract;
    }
}
