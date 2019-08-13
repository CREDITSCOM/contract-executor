package com.credits.service.contract;

import com.credits.general.classload.ByteCodeContractClassLoader;
import com.credits.general.pojo.ApiResponseCode;
import com.credits.general.pojo.ApiResponseData;
import com.credits.general.pojo.ByteCodeObjectData;
import com.credits.general.pojo.MethodDescriptionData;
import com.credits.general.thrift.generated.Variant;
import com.credits.general.util.GeneralConverter;
import com.credits.general.util.compiler.CompilationException;
import com.credits.general.util.compiler.InMemoryCompiler;
import com.credits.secure.PermissionsManager;
import com.credits.service.node.apiexec.NodeApiExecInteractionServiceImpl;
import exception.ContractExecutorException;
import pojo.ExternalSmartContract;
import pojo.ReturnValue;
import pojo.apiexec.SmartContractGetResultData;
import pojo.session.DeployContractSession;
import pojo.session.InvokeMethodSession;
import service.executor.ContractExecutorService;
import service.executor.SmartContractContext;
import service.node.NodeApiExecStoreTransactionService;

import javax.inject.Inject;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

import static com.credits.general.serialize.Serializer.deserialize;
import static com.credits.general.serialize.Serializer.serialize;
import static com.credits.service.BackwardCompatibilityService.allVersionsSmartContractClass;
import static com.credits.service.contract.CurrentThreadMethodExecutor.createCurrentThreadMethodExecutor;
import static com.credits.thrift.utils.ContractExecutorUtils.*;
import static com.credits.utils.ContractExecutorServiceUtils.*;
import static java.util.Collections.singletonList;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;


public class ContractExecutorServiceImpl implements ContractExecutorService {

    private final PermissionsManager permissionManager;
    private final NodeApiExecStoreTransactionService nodeApiExecService;

    @Inject
    public ContractExecutorServiceImpl(NodeApiExecStoreTransactionService nodeApiExecService,
                                       PermissionsManager permissionManager,
                                       ExecutorService threadPoolExecutor) {
        this.permissionManager = permissionManager;
        this.nodeApiExecService = nodeApiExecService;
        allVersionsSmartContractClass.forEach(contract -> initStaticContractFields(nodeApiExecService, this, threadPoolExecutor, contract));
        permissionManager.grantAllPermissions(NodeApiExecInteractionServiceImpl.class);
    }

    @Override
    public ReturnValue deploySmartContract(DeployContractSession session) throws ContractExecutorException {
        final var contractClass = findRootClass(compileClassesAndDropPermissions(session.byteCodeObjectDataList, getSmartContractClassLoader()));
        final var methodResult = new Deployer(session, contractClass).deploy();
        final var newContractState = methodResult.getInvokedObject() != null
                                     ? serialize(methodResult.getInvokedObject())
                                     : new byte[0];
        return new ReturnValue(newContractState,
                               singletonList(methodResult.getException() == null
                                             ? createSuccessMethodResult(methodResult, nodeApiExecService)
                                             : createFailureMethodResult(methodResult, nodeApiExecService)),
                               session.usedContracts);
    }

    @Override
    public ReturnValue executeSmartContract(InvokeMethodSession session) throws ContractExecutorException {
        final var contractClassLoader = getSmartContractClassLoader();
        loadClassesToClassloader(session.byteCodeObjectDataList,contractClassLoader);
        final var instance = deserialize(session.contractState, contractClassLoader);

        initNonStaticContractFields(session, instance);
        addThisContractToUsedContracts(session, instance);
        return executeContractMethod(session, instance);
    }

    private ReturnValue executeContractMethod(InvokeMethodSession session, Object contractInstance) {
        final var executor = new LimitTimeThreadMethodExecutor(session, contractInstance);
        final var methodResults = executor.executeIntoLimitTimeThread();
        session.usedContracts.values().forEach(contract -> contract.getContractData().setContractState(serialize(contract.getInstance())));

        return new ReturnValue(session.usedContracts.get(session.contractAddress).getContractData().getContractState(),
                               methodResults.stream()
                                       .map(mr -> mr.getException() == null
                                                  ? createSuccessMethodResult(mr, nodeApiExecService)
                                                  : createFailureMethodResult(mr, nodeApiExecService))
                                       .collect(toList()),
                               session.usedContracts);
    }

    @Override
    public List<MethodDescriptionData> getContractMethods(List<ByteCodeObjectData> byteCodeObjectDataList) {
        requireNonNull(byteCodeObjectDataList, "bytecode of executor class is null");

        final var contractClass = findRootClass(compileSmartContractByteCode(byteCodeObjectDataList, getSmartContractClassLoader()));
        return SmartContractAnalyzer.getContractMethods(contractClass);
    }

    @Override
    public List<MethodDescriptionData> getContractMethods(Class<?> contractClass) throws ContractExecutorException {
        requireNonNull(contractClass, "executor class is null");

        return SmartContractAnalyzer.getContractMethods(contractClass);
    }

    @Override
    public Map<String, Variant> getContractVariables(List<ByteCodeObjectData> byteCodeObjectDataList, byte[] contractState)
    throws ContractExecutorException {
        requireNonNull(byteCodeObjectDataList, "bytecode of contract class is null");
        requireNonNull(contractState, "contract state is null");
        if (contractState.length == 0) throw new ContractExecutorException("contract state is empty");

        var contractClassLoader = new ByteCodeContractClassLoader();
        compileSmartContractByteCode(byteCodeObjectDataList, contractClassLoader);
        return SmartContractAnalyzer.getContractVariables(deserialize(contractState, contractClassLoader));
    }

    @Override
    public List<ByteCodeObjectData> compileContractClass(String sourceCode) throws ContractExecutorException, CompilationException {
        requireNonNull(sourceCode, "sourceCode of executor class is null");
        if (sourceCode.isEmpty()) throw new ContractExecutorException("sourceCode of executor class is empty");

        final var compilationPackage = InMemoryCompiler.compileSourceCode(sourceCode);
        return GeneralConverter.compilationPackageToByteCodeObjectsData(compilationPackage);
    }

    @Override
    public List<Class<?>> buildContractClass(List<ByteCodeObjectData> byteCodeObjectDataList) {
        requireNonNull(byteCodeObjectDataList, "bytecode of executor class is null");

        return compileClassesAndDropPermissions(byteCodeObjectDataList, getSmartContractClassLoader());
    }

    @Override
    public Object executeExternalSmartContact(SmartContractContext contractContext, String invokingContractAddress, String method, Object[] params) {
        verifyPayableCall(method);
        return createCurrentThreadMethodExecutor(contractContext, invokingContractAddress, method, params).execute();
    }

    private void verifyPayableCall(String method) {
        if (method.equals("payable")) throw new ContractExecutorException("payable method cannot be called");
    }

    private void addThisContractToUsedContracts(InvokeMethodSession session, Object instance) {
        ExternalSmartContract usedContract = new ExternalSmartContract(
                new SmartContractGetResultData(
                        new ApiResponseData(ApiResponseCode.SUCCESS, ""),
                        session.byteCodeObjectDataList,
                        session.contractState,
                        true));
        usedContract.setInstance(instance);
        session.usedContracts.put(session.contractAddress, usedContract);
    }

    private List<Class<?>> compileClassesAndDropPermissions(List<ByteCodeObjectData> byteCodeObjectList, ByteCodeContractClassLoader classLoader)
    throws ContractExecutorException {
        return compileSmartContractByteCode(byteCodeObjectList, classLoader).stream()
                .peek(permissionManager::dropSmartContractRights)
                .collect(toList());
    }


}
