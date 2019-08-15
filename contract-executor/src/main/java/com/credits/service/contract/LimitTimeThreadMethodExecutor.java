package com.credits.service.contract;

import com.credits.general.thrift.generated.Variant;
import com.credits.pojo.MethodData;
import com.credits.scapi.v2.BasicTokenStandard;
import exception.ContractExecutorException;
import pojo.session.InvokeMethodSession;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.credits.general.serialize.Serializer.deserialize;
import static com.credits.general.util.variant.VariantConverter.toVariant;
import static com.credits.thrift.utils.ContractExecutorUtils.contractIsHaveObservableBalances;
import static com.credits.utils.ContractExecutorServiceUtils.getMethodArgumentsValuesByNameAndParams;
import static java.util.Arrays.stream;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

class LimitTimeThreadMethodExecutor extends LimitedExecutionMethod<Variant> {
    private final InvokeMethodSession session;
    private Object instance;
    private final ClassLoader classLoader;
    private Map<String, Map<String, Number>> changedBalances =  new HashMap<>();

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
        final var balancesCollectorRef = new AtomicReference<List<DiffBalancesCollector>>();
        return runForLimitTime(() -> {
            try {
                if (contractIsHaveObservableBalances(instance.getClass())) {
                    final var balancesCollector = new DiffBalancesCollector(session.contractAddress, (BasicTokenStandard) instance);
                    ContractThreadLocalContext.addDiffBalanceCollector(balancesCollector);
                    balancesCollector.subscribe();
                }
                balancesCollectorRef.set(ContractThreadLocalContext.getDiffBalancesCollectorList());
                return toVariant(returnTypeName, method.invoke(instance, methodData.argValues));
            } finally {
                if (balancesCollectorRef.get() != null) {
                    balancesCollectorRef.get().forEach(bc -> {
                        bc.unsubscribe();
                        changedBalances.put(bc.getContractAddress(), bc.getBalances());
                    });
                }
            }
        });
    }

    @Override
    protected MethodResult prepareResult(Variant returnValue) {
        final var result = super.prepareResult(returnValue);
        result.setChangedBalances(changedBalances);
        return result;
    }

    private MethodData findInvokedMethodIntoContract(Variant[] params) {
        try {
            return getMethodArgumentsValuesByNameAndParams(instance.getClass(), session.methodName, params, classLoader);
        } catch (ClassNotFoundException e) {
            throw new ContractExecutorException(getRootCauseMessage(e));
        }
    }
}
