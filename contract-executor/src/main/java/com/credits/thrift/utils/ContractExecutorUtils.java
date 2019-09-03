package com.credits.thrift.utils;

import com.credits.exception.IncompatibleVersionException;
import com.credits.general.classload.ByteCodeContractClassLoader;
import com.credits.general.pojo.ByteCodeObjectData;
import exception.ContractExecutorException;

import java.util.ArrayList;
import java.util.List;

import static com.credits.ApplicationProperties.API_VERSION;

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

    public static List<Class<?>> compileSmartContractByteCode(List<ByteCodeObjectData> smartContractByteCodeData,
                                                              ByteCodeContractClassLoader byteCodeContractClassLoader) {

        List<Class<?>> compiledClasses = new ArrayList<>(smartContractByteCodeData.size());
        for (ByteCodeObjectData compilationUnit : smartContractByteCodeData) {
            compiledClasses.add(byteCodeContractClassLoader.loadClass(compilationUnit.getName(), compilationUnit.getByteCode()));
        }
        return compiledClasses;
    }

    public static void validateVersion(short version) {
        if (version != API_VERSION) {
            throw new IncompatibleVersionException(String.format("Invalid version %s, %s expected", version, API_VERSION));
        }
    }
}
