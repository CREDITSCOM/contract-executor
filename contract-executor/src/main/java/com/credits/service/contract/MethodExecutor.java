package com.credits.service.contract;

import com.credits.general.thrift.generated.Variant;
import com.credits.pojo.MethodData;
import exception.ContractExecutorException;
import pojo.session.InvokeMethodSession;

import java.util.List;
import java.util.stream.Collectors;

import static com.credits.general.serialize.Serializer.deserialize;
import static com.credits.general.util.variant.VariantConverter.toVariant;
import static com.credits.utils.ContractExecutorServiceUtils.getMethodArgumentsValuesByNameAndParams;
import static java.util.Arrays.stream;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

class MethodExecutor extends LimitedExecutionMethod<Variant> {
    private final InvokeMethodSession session;
    private Object instance;
    private final ClassLoader classLoader;

    public MethodExecutor(InvokeMethodSession session, Object contractInstance) {
        super(session);
        this.session = session;
        this.instance = contractInstance;
        this.classLoader = instance.getClass().getClassLoader();
    }

    public List<MethodResult> executeIntoLimitTimeThread() {
        return session.paramsTable.length < 2
                ? invokeSingleMethod()
                : invokeMultipleMethod();
    }

    public List<MethodResult> executeIntoCurrentThread() {
        return List.of(prepareResult(invokeIntoCurrentThread(session.paramsTable[0])));
    }

    public Object getSmartContractObject() {
        return instance;
    }

    private List<MethodResult> invokeSingleMethod() {
        return List.of(prepareResult(invokeIntoLimitTimeThread(session.paramsTable[0])));
    }

    private List<MethodResult> invokeMultipleMethod() {
        final var results = stream(session.paramsTable).map(params -> prepareResult(invokeUsingPrimaryContractState(params))).collect(Collectors.toList());
        session.usedContracts.get(session.contractAddress).setInstance(instance);
        return results;
    }

    private Variant invokeUsingPrimaryContractState(Variant... params) {
        instance = deserialize(session.contractState, instance.getClass().getClassLoader());
        return invokeIntoLimitTimeThread(params);
    }

    private Variant invokeIntoCurrentThread(Variant... params) {
        final var methodData = findInvokedMethodIntoContract(params);
        final var method = methodData.method;
        final var returnTypeName = method.getReturnType().getTypeName();
        return runIntoCurrentThread(() -> toVariant(returnTypeName, method.invoke(instance, methodData.argValues)));
    }

    private Variant invokeIntoLimitTimeThread(Variant... params) {
        final var methodData = findInvokedMethodIntoContract(params);
        final var method = methodData.method;
        final var returnTypeName = method.getReturnType().getTypeName();
        return runForLimitTime(() -> toVariant(returnTypeName, method.invoke(instance, methodData.argValues)));
    }

    private MethodData findInvokedMethodIntoContract(Variant[] params) {
        try {
            return getMethodArgumentsValuesByNameAndParams(instance.getClass(), session.methodName, params, classLoader);
        } catch (ClassNotFoundException e) {
            throw new ContractExecutorException(getRootCauseMessage(e));
        }
    }
}
