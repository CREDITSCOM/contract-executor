package com.credits.utils;

import com.credits.client.executor.thrift.generated.ChangedTokenBalance;
import com.credits.general.thrift.generated.APIResponse;
import com.credits.general.thrift.generated.Variant;
import com.credits.general.util.variant.VariantConverter;
import com.credits.pojo.MethodData;
import com.credits.service.contract.MethodResult;
import exception.ContractExecutorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.ExternalSmartContract;
import pojo.SmartContractMethodResult;
import pojo.session.InvokeMethodSession;
import service.executor.ContractExecutorService;
import service.node.NodeApiExecInteractionService;
import service.node.NodeApiExecStoreTransactionService;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import static com.credits.general.pojo.ApiResponseCode.FAILURE;
import static com.credits.general.pojo.ApiResponseCode.SUCCESS;
import static com.credits.general.util.GeneralConverter.decodeFromBASE58;
import static com.credits.general.util.Utils.getClassType;
import static com.credits.general.util.Utils.rethrowUnchecked;
import static com.credits.general.util.variant.VariantConverter.toVariant;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.beanutils.MethodUtils.getMatchingAccessibleMethod;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseStackTrace;
import static org.apache.commons.lang3.reflect.FieldUtils.getAllFieldsList;


public class ContractExecutorServiceUtils {

    private final static Logger logger = LoggerFactory.getLogger(ContractExecutorServiceUtils.class);

    public final static APIResponse SUCCESS_API_RESPONSE = new APIResponse(SUCCESS.code, "success");

    public static MethodData getMethodArgumentsValuesByNameAndParams(
            Class<?> contractClass,
            String methodName,
            Variant[] params,
            ClassLoader classLoader) throws ClassNotFoundException {

        if (params == null) {
            throw new ContractExecutorException("Cannot find method params == null");
        }

        Class<?>[] argTypes = getArgTypes(params, classLoader);
        Method method = getMatchingAccessibleMethod(contractClass, methodName, argTypes);
        if (method != null) {
            Object[] argValues = castValues(argTypes, params, classLoader);
            return new MethodData(method, argTypes, argValues);
        } else {
            throw new ContractExecutorException("Cannot find a method by name and parameters specified. Signature: " + methodName + "("
                                                        + stream(argTypes).map(Class::getName).collect(Collectors.joining(", ")) + ")");
        }
    }

    public static APIResponse failureApiResponse(Throwable e) {
        return new APIResponse(FAILURE.code, getRootCauseMessage(e));
    }


    public static SmartContractMethodResult createSuccessMethodResult(MethodResult mr, NodeApiExecStoreTransactionService nodeApiExecService) {
        return new SmartContractMethodResult(SUCCESS_API_RESPONSE,
                                             mr.getReturnValue(),
                                             mr.getSpentCpuTime(),
                                             nodeApiExecService.takeAwayEmittedTransactions(mr.getThreadId()),
                                             mr.getChangedBalances());
    }

    public static SmartContractMethodResult createFailureMethodResult(MethodResult mr, NodeApiExecStoreTransactionService nodeApiExecService) {
        return new SmartContractMethodResult(failureApiResponse(mr.getException()),
                                             toVariant(String.class.getTypeName(), String.join("\n", getRootCauseStackTrace(mr.getException()))),
                                             mr.getSpentCpuTime(),
                                             nodeApiExecService.takeAwayEmittedTransactions(mr.getThreadId()));
    }


    public static Object[] castValues(Class<?>[] types, Variant[] params, ClassLoader classLoader) throws ContractExecutorException {
        if (params == null || params.length != types.length) {
            throw new ContractExecutorException("not enough arguments passed");
        }
        Object[] retVal = new Object[types.length];
        Variant param;
        for (int i = 0, typesLength = types.length; i < typesLength; i++) {
            param = params[i];
            retVal[i] = VariantConverter.toObject(param, classLoader);
        }
        return retVal;
    }

    private static Class<?>[] getArgTypes(Variant[] params, ClassLoader classLoader) throws ClassNotFoundException {
        Class<?>[] classes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            Variant variant = params[i];
            switch (variant.getSetField()) {
                case V_OBJECT:
                    classes[i] = Class.forName(variant.getV_object().nameClass, false, classLoader);
                    break;
                case V_VOID:
                    classes[i] = Void.TYPE;
                    break;
                case V_NULL:
                    classes[i] = Class.forName(variant.getV_null(), false, classLoader);
                    break;
                case V_BYTE_ARRAY:
                    classes[i] = byte[].class;
                    break;
                case V_BIG_DECIMAL:
                    classes[i] = BigDecimal.class;
                    break;
                default:
                    classes[i] = variant.getFieldValue().getClass();
                    break;
            }
        }
        return classes;
    }

    public static void initializeSmartContractField(String fieldName, Object value, Class<?> clazz, Object instance) {
        getAllFieldsList(clazz).stream()
                .filter(field -> field.getName().equals(fieldName))
                .findAny()
                .ifPresent(field -> rethrowUnchecked(() -> {
                    field.setAccessible(true);
                    field.set(instance, value);
                }));
    }

    public static void initStaticContractFields(NodeApiExecInteractionService nodeApiExecService,
                                                ContractExecutorService executorService,
                                                ExecutorService threadPoolExecutor,
                                                Class<?> contractClass) {
        initializeSmartContractField("nodeApiService", nodeApiExecService, contractClass, null);
        initializeSmartContractField("contractExecutorService", executorService, contractClass, null);
        initializeSmartContractField("cachedPool", threadPoolExecutor, contractClass, null);
    }

    public static void initNonStaticContractFields(long accessId,
                                                   String initiatorAddress,
                                                   Map<String, ExternalSmartContract> usedContracts,
                                                   Object instance) {
        requireNonNull(instance, "instance can't be null for not static fields");
        final var contractClass = instance.getClass();
        initializeSmartContractField("initiator", initiatorAddress, contractClass, instance);
        initializeSmartContractField("accessId", accessId, contractClass, instance);
        initializeSmartContractField("usedContracts", usedContracts, contractClass, instance);
    }

    public static void initNonStaticContractFields(InvokeMethodSession session, Object instance) {
        initNonStaticContractFields(session.accessId, session.initiatorAddress, session.usedContracts, instance);
    }
    public static Variant[][] variantArrayOf(Object[]... params) {
        return stream(params)
                .map(p -> stream(p)
                        .map(pp -> toVariant(getClassType(pp), pp))
                        .collect(toList()).toArray(Variant[]::new))
                .collect(toList()).toArray(Variant[][]::new);
    }

    public static Variant[][] variantArrayOf(Object... params) {
        return new Variant[][]{stream(params).map(p -> toVariant(getClassType(p), p)).collect(toList()).toArray(Variant[]::new)};
    }

    public static List<Variant> variantListOf(Object... params) {
        return stream(params).map(p -> toVariant(getClassType(p), p)).collect(toList());
    }

    public static Map<ByteBuffer, List<ChangedTokenBalance>> convertToChangedTokenBalancesMap(Map<String, Map<String, Number>> changedBalances) {
        final var result = new HashMap<ByteBuffer, List<ChangedTokenBalance>>();
        for (var token : changedBalances.entrySet()) {
            final var contractAddress = ByteBuffer.wrap(decodeFromBASE58(token.getKey()));
            final var balances = token.getValue();

            final var changedTokenBalances = convertToChangedTokenBalancesList(balances);

            result.put(contractAddress, changedTokenBalances);
        }
        return result;
    }

    public static List<ChangedTokenBalance> convertToChangedTokenBalancesList(Map<String, Number> balances) {
        return balances.entrySet().stream().map(addressAndBalance -> {
            final var address = ByteBuffer.wrap(decodeFromBASE58(addressAndBalance.getKey()));
            Variant newValue;
            try {
                final var numberValue = addressAndBalance.getValue().doubleValue();
                newValue = toVariant(getClassType(numberValue), numberValue);
            } catch (Throwable e) {
                logger.error("incorrect Number implementation of balance value. " + e.getMessage());
                newValue = toVariant(getClassType(null), null);
            }
            return new ChangedTokenBalance(address, newValue);
        }).collect(Collectors.toList());
    }
}
