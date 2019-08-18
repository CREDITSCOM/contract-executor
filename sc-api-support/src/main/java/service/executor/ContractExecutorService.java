package service.executor;

import com.credits.general.classload.ByteCodeContractClassLoader;
import com.credits.general.pojo.ByteCodeObjectData;
import com.credits.general.pojo.MethodDescriptionData;
import com.credits.general.thrift.generated.Variant;
import com.credits.general.util.compiler.CompilationException;
import exception.ContractExecutorException;
import pojo.ReturnValue;
import pojo.session.DeployContractSession;
import pojo.session.InvokeMethodSession;

import java.util.List;
import java.util.Map;

public interface ContractExecutorService {

    ReturnValue deploySmartContract(DeployContractSession session) throws ContractExecutorException;

    ReturnValue executeSmartContract(InvokeMethodSession session) throws ContractExecutorException;

    List<MethodDescriptionData> getContractMethods(List<ByteCodeObjectData> byteCodeObjectDataList) throws ContractExecutorException;

    List<MethodDescriptionData> getContractMethods(Class<?> contractClass) throws ContractExecutorException;

    List<ByteCodeObjectData> compileContractClass(String sourceCode) throws ContractExecutorException, CompilationException;

    List<Class<?>> buildContractClass(List<ByteCodeObjectData> byteCodeObjectDataList);

    Map<String, Variant> getContractVariables(List<ByteCodeObjectData> contractBytecode, byte[] contractState) throws ContractExecutorException;

    Object executeExternalSmartContact(SmartContractContext contractContext, String invokingContractAddress, String method, Object[] params);

    Map<String, Number> getTokenBalances(List<ByteCodeObjectData> contractByteCode, byte[] contractState) throws ContractExecutorException;

    default ByteCodeContractClassLoader getSmartContractClassLoader() {
        return getClass().getClassLoader() instanceof ByteCodeContractClassLoader
            ? (ByteCodeContractClassLoader) getClass().getClassLoader()
            : new ByteCodeContractClassLoader();
    }
}
