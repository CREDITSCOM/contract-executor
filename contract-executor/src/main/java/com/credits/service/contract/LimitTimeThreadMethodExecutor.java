package com.credits.service.contract;

import com.credits.general.thrift.generated.Variant;
import com.credits.pojo.MethodData;
import com.credits.scapi.v2.BasicTokenStandard;
import exception.ContractExecutorException;
import pojo.session.InvokeMethodSession;

import java.util.List;
import java.util.stream.Collectors;

import static com.credits.general.serialize.Serializer.deserialize;
import static com.credits.general.util.variant.VariantConverter.toVariant;
import static com.credits.scapi.misc.TokenStandardId.BASIC_TOKEN_STANDARD_V2;
import static com.credits.scapi.misc.TokenStandardId.EXTENSION_TOKEN_STANDARD_V2;
import static com.credits.service.contract.SmartContractAnalyzer.defineTokenStandard;
import static com.credits.utils.ContractExecutorServiceUtils.getMethodArgumentsValuesByNameAndParams;
import static java.util.Arrays.stream;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

class LimitTimeThreadMethodExecutor extends LimitedExecutionMethod<Variant> {
    private final InvokeMethodSession session;
    private Object instance;
    private final ClassLoader classLoader;

    public LimitTimeThreadMethodExecutor(InvokeMethodSession session, Object contractInstance) {
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

    private Variant invokeIntoLimitTimeThread(Variant... params) {
        final var methodData = findInvokedMethodIntoContract(params);
        final var method = methodData.method;
        final var returnTypeName = method.getReturnType().getTypeName();
        final var balancesCollector = new DiffBalancesCollector(session.contractAddress, (BasicTokenStandard) instance);
        return runForLimitTime(() -> {
            if (contractIsHaveObservableBalances()) {
                ContractThreadLocalContext.addDiffBalanceCollector(balancesCollector);
                try {
                    return toVariant(returnTypeName, method.invoke(instance, methodData.argValues));
                } finally {
                    balancesCollector.unsubscribe();
                }
            } else {
                return toVariant(returnTypeName, method.invoke(instance, methodData.argValues));
            }

        });
    }

    private boolean contractIsHaveObservableBalances() {
        final var standardId = defineTokenStandard(instance.getClass());
        return standardId == BASIC_TOKEN_STANDARD_V2.getId() ||
                standardId == EXTENSION_TOKEN_STANDARD_V2.getId();
    }

    private MethodData findInvokedMethodIntoContract(Variant[] params) {
        try {
            return getMethodArgumentsValuesByNameAndParams(instance.getClass(), session.methodName, params, classLoader);
        } catch (ClassNotFoundException e) {
            throw new ContractExecutorException(getRootCauseMessage(e));
        }
    }
}
