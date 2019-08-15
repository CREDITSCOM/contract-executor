package com.credits.thrift.utils;

import com.credits.general.classload.ByteCodeContractClassLoader;
import com.credits.general.pojo.ByteCodeObjectData;
import exception.ContractExecutorException;

import java.util.ArrayList;
import java.util.List;

import static com.credits.ApplicationProperties.APP_VERSION;
import static com.credits.scapi.misc.TokenStandardId.BASIC_TOKEN_STANDARD_V2;
import static com.credits.scapi.misc.TokenStandardId.EXTENSION_TOKEN_STANDARD_V2;
import static com.credits.service.contract.SmartContractAnalyzer.defineTokenStandard;

public class ContractExecutorUtils {

    public static Class<?> findRootClass(List<Class<?>> classes) {
        return classes.stream()
                .filter(clazz -> !clazz.getName().contains("$"))
                .findAny()
                .orElseThrow(() -> new ContractExecutorException("executor class not compiled"));
    }

    public static void loadClassesToClassloader(List<ByteCodeObjectData> objectDataList, ByteCodeContractClassLoader classLoader) {
        objectDataList.forEach(o -> classLoader.loadClass(o.getName(), o.getByteCode()));
    }

    public static boolean contractIsHaveObservableBalances(Class<?> contractClass) {
        final var standardId = defineTokenStandard(contractClass);
        return standardId == BASIC_TOKEN_STANDARD_V2.getId() ||
                standardId == EXTENSION_TOKEN_STANDARD_V2.getId();
    }

    public static List<Class<?>> compileSmartContractByteCode(List<ByteCodeObjectData> smartContractByteCodeData,
                                                              ByteCodeContractClassLoader byteCodeContractClassLoader) {

        List<Class<?>> compiledClasses = new ArrayList<>(smartContractByteCodeData.size());
        for (ByteCodeObjectData compilationUnit : smartContractByteCodeData) {
            compiledClasses.add(byteCodeContractClassLoader.loadClass(compilationUnit.getName(), compilationUnit.getByteCode()));
        }
        return compiledClasses;
    }

    public static void validateVersion(short version) {
        if (version != APP_VERSION) {
            throw new IllegalArgumentException(String.format("Invalid version %s, %s expected", version, APP_VERSION));
        }
    }
}
