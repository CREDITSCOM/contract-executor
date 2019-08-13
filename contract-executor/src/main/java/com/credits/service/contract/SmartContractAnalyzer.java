package com.credits.service.contract;

import com.credits.general.pojo.AnnotationData;
import com.credits.general.pojo.MethodArgumentData;
import com.credits.general.pojo.MethodDescriptionData;
import com.credits.general.thrift.generated.Variant;
import com.credits.scapi.annotations.ContractAddress;
import com.credits.scapi.annotations.ContractMethod;
import com.credits.scapi.annotations.UsingContract;
import com.credits.scapi.annotations.UsingContracts;
import com.credits.scapi.misc.TokenStandardId;
import com.credits.scapi.v2.WalletAddress;
import exception.ContractExecutorException;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.credits.general.util.variant.VariantConverter.toVariant;
import static com.credits.scapi.misc.TokenStandardId.NOT_A_TOKEN;
import static com.credits.service.BackwardCompatibilityService.allVersionsBasicStandardClass;
import static com.credits.utils.Constants.*;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;
import static org.apache.commons.lang3.exception.ExceptionUtils.rethrow;

public interface SmartContractAnalyzer {

    static long defineTokenStandard(Class<?> contractClass) {
        final var contractInterfaces = contractClass.getInterfaces();
        return stream(TokenStandardId.values())
                .filter(ts -> stream(contractInterfaces).anyMatch(ci -> ts.getTokenStandardClass().equals(ci)))
                .findFirst()
                .orElse(NOT_A_TOKEN).getId();
    }

    static void checkThatIsNotCreditsToken(Class<?> contractClass, Object instance) {
        stream(contractClass.getInterfaces())
                .filter(allVersionsBasicStandardClass::contains)
                .findAny()
                .ifPresent(ignore -> stream(contractClass.getMethods())
                        .filter(m -> m.getName().equals("getName") || m.getName().equals("getSymbol") && m.getParameters().length == 0)
                        .forEach(method -> {
                            try {
                                String methodName = method.getName();
                                if (methodName.equals("getName")) {
                                    if (((String) method.invoke(instance)).equalsIgnoreCase(CREDITS_TOKEN_NAME)) {
                                        throw new ContractExecutorException(TOKEN_NAME_RESERVED_ERROR);
                                    }
                                } else if (methodName.equals("getSymbol")) {
                                    if (((String) method.invoke(instance)).equalsIgnoreCase(CREDITS_TOKEN_SYMBOL)) {
                                        throw new ContractExecutorException(TOKEN_NAME_RESERVED_ERROR);
                                    }
                                }
                            } catch (ContractExecutorException e) {
                                rethrow(e);
                            } catch (Throwable ignored) {
                            }
                        }));
    }

    static List<MethodDescriptionData> getContractMethods(Class<?> contractClass) {
        return stream(contractClass.getMethods())
                .filter(m -> !OBJECT_METHODS.contains(m.getName()))
                .map(method -> {
                    var annotations = toAnnotationDataList(method.getAnnotations());
                    var parameters = stream(method.getParameters())
                            .map(p -> new MethodArgumentData(p.getType().getTypeName(), p.getName(), toAnnotationDataList(p.getAnnotations())))
                            .collect(toList());
                    return toMethodDescriptionData(method, annotations, parameters);
                })
                .collect(toList());
    }

    Set<String> OBJECT_METHODS = Set.of(
            "getClass",
            "hashCode",
            "equals",
            "toString",
            "notify",
            "notifyAll",
            "wait",
            "finalize");

    private static MethodDescriptionData toMethodDescriptionData(Method m, List<AnnotationData> annotations, List<MethodArgumentData> parameters) {
        return new MethodDescriptionData(m.getGenericReturnType().getTypeName(), m.getName(), parameters, annotations);
    }

    private static List<AnnotationData> toAnnotationDataList(Annotation[] annotations) {
        return stream(annotations).flatMap(a -> readAnnotation(a).stream()).collect(toList());
    }

    private static List<AnnotationData> readAnnotation(Annotation annotation) {
        if (annotation instanceof UsingContract) {
            var usingContract = ((UsingContract) annotation);
            return singletonList(new AnnotationData(
                    UsingContract.class.getName(),
                    Map.of("address", usingContract.address(), "method", usingContract.method())));

        } else if (annotation instanceof UsingContracts) {
            return stream(((UsingContracts) annotation).value())
                    .flatMap(a -> readAnnotation(a).stream())
                    .collect(toList());
        } else if (annotation instanceof ContractAddress) {
            return singletonList(new AnnotationData(
                    ContractAddress.class.getName(),
                    Map.of("id", Integer.toString(((ContractAddress) annotation).id()))));

        } else if (annotation instanceof ContractMethod) {
            return singletonList(new AnnotationData(
                    ContractMethod.class.getName(),
                    Map.of("id", Integer.toString(((ContractMethod) annotation).id()))));
        } else {
            return singletonList(new AnnotationData(annotation.annotationType().getName(), emptyMap()));
        }
    }

    static Map<String, Variant> getContractVariables(Object object) throws ContractExecutorException {
        Map<String, Variant> contractVariables = null;
        Field[] fields = object.getClass().getFields();
        if (fields.length != 0) {
            contractVariables = new HashMap<>();
            for (Field field : fields) {
                String name = field.getName();
                Variant variant;

                Object fieldValue;
                try {
                    fieldValue = field.get(object);
                } catch (IllegalAccessException e) {
                    throw new ContractExecutorException(
                            "Cannot getObject access to field: " + name + ". Reason: " + getRootCauseMessage(e), e);
                }

                variant = toVariant(field.getType().getTypeName(), fieldValue);
                contractVariables.put(name, variant);
            }
        }
        return contractVariables;
    }

    Map<String, Map<WalletAddress, Number>> getTokenBalances(Object contract);
}
