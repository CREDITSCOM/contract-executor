package com.credits.thrift;

import com.credits.client.executor.thrift.generated.*;
import com.credits.general.thrift.generated.ByteCodeObject;
import com.credits.general.thrift.generated.ClassObject;
import com.credits.general.thrift.generated.Variant;
import com.credits.general.util.GeneralConverter;
import com.credits.general.util.compiler.CompilationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pojo.ReturnValue;
import pojo.session.DeployContractSession;
import pojo.session.InvokeMethodSession;
import service.executor.ContractExecutorService;

import javax.inject.Inject;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.credits.general.util.GeneralConverter.*;
import static com.credits.ioc.Injector.INJECTOR;
import static com.credits.scapi.misc.TokenStandardId.NOT_A_TOKEN;
import static com.credits.thrift.utils.ContractExecutorUtils.*;
import static com.credits.utils.ContractExecutorServiceUtils.SUCCESS_API_RESPONSE;
import static com.credits.utils.ContractExecutorServiceUtils.failureApiResponse;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

public class ContractExecutorHandler implements ContractExecutor.Iface {

    private final static Logger logger = LoggerFactory.getLogger(ContractExecutorHandler.class);

    private final ContractExecutorService ceService;

    @Inject
    public ContractExecutorHandler(ContractExecutorService contractExecutorService) {
        INJECTOR.component.inject(this);
        ceService = contractExecutorService;
    }

    @Override
    public ExecuteByteCodeResult executeByteCode(long accessId,
                                                 ByteBuffer initiatorAddress,
                                                 SmartContractBinary invokedContract,
                                                 List<MethodHeader> methodHeaders,
                                                 long executionTime,
                                                 short version) {
        ExecuteByteCodeResult executeByteCodeResult;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug(
                        "<-- executeByteCode: initiatorAddress={}|contractAddress={}|contractState={} bytes|methodHeaders={}|executionTime={}|version={}",
                        encodeToBASE58(initiatorAddress.array()),
                        encodeToBASE58(invokedContract.contractAddress.array()),
                        invokedContract.object.instance.array().length,
                        methodHeaders,
                        executionTime,
                        version);
            }

            validateVersion(version);

            final var session = new ExecuteByteCodeSession(ceService, accessId, initiatorAddress, invokedContract, methodHeaders, executionTime);
            executeByteCodeResult = session.perform();
        } catch (Throwable e) {
            executeByteCodeResult = new ExecuteByteCodeResult(failureApiResponse(e), emptyList());
        }

        logger.debug("executeByteCode --> {}", executeByteCodeResult);
        return executeByteCodeResult;
    }

    @Override
    public ExecuteByteCodeMultipleResult executeByteCodeMultiple(
            long accessId,
            ByteBuffer initiatorAddress,
            SmartContractBinary invokedContract,
            String method,
            List<List<Variant>> params,
            long executionTime,
            short version) {

        ClassObject classObject = invokedContract.object;
        ExecuteByteCodeMultipleResult byteCodeMultipleResult;

        try {
            if (logger.isDebugEnabled()) {
                logger.debug("<-- executeByteCodeMultiple: " +
                                     "initiatorAddress={}|contractAddress={}|contractState={} bytes|method={}|params={}|executionTime={}|version={}",
                             encodeToBASE58(initiatorAddress.array()),
                             encodeToBASE58(invokedContract.contractAddress.array()),
                             invokedContract.object.instance.array().length,
                             method,
                             params,
                             executionTime,
                             version);
            }

            Objects.requireNonNull(classObject, "class object can't be null");

            Variant[][] paramsArray = null;
            if (params != null) {
                paramsArray = new Variant[params.size()][];
                for (int i = 0; i < params.size(); i++) {
                    List<Variant> list = params.get(i);
                    paramsArray[i] = list.toArray(new Variant[0]);
                }
            }

            byteCodeMultipleResult = new ExecuteByteCodeMultipleResult(SUCCESS_API_RESPONSE, null);
            validateVersion(version);

            ReturnValue returnValue = classObject.instance == null || classObject.instance.array().length == 0
                    ? ceService.deploySmartContract(new DeployContractSession(accessId,
                                                                              encodeToBASE58(initiatorAddress.array()),
                                                                              encodeToBASE58(invokedContract.contractAddress.array()),
                                                                              byteCodeObjectsToByteCodeObjectsData(classObject.byteCodeObjects),
                                                                              executionTime))
                    : ceService.executeSmartContract(new InvokeMethodSession(accessId,
                                                                             encodeToBASE58(initiatorAddress.array()),
                                                                             encodeToBASE58(invokedContract.contractAddress.array()),
                                                                             byteCodeObjectsToByteCodeObjectsData(classObject.byteCodeObjects),
                                                                             classObject.instance.array(),
                                                                             method,
                                                                             paramsArray,
                                                                             executionTime));

            byteCodeMultipleResult.results = returnValue.executeResults.stream().map(rv -> {
                final GetterMethodResult getterMethodResult = new GetterMethodResult(rv.status);
                getterMethodResult.ret_val = rv.result;
                return getterMethodResult;
            }).collect(Collectors.toList());

        } catch (Throwable e) {
            byteCodeMultipleResult = new ExecuteByteCodeMultipleResult(failureApiResponse(e), emptyList());
        }

        logger.debug("executeByteCodeMultiple --> {}", byteCodeMultipleResult);
        return byteCodeMultipleResult;
    }

    @Override
    public GetContractMethodsResult getContractMethods(List<ByteCodeObject> compilationUnits, short version) {
        GetContractMethodsResult result;
        try {
            logger.debug("<-- getContractMethods: compilationUnits={} bytes|version={}", compilationUnits.size(), version);
            validateVersion(version);
            final var byteCodeObjects = byteCodeObjectsToByteCodeObjectsData(compilationUnits);
            final var contractClass = findRootClass(ceService.buildContractClass(byteCodeObjects));
            final var tokenStandard = defineTokenStandard(contractClass);
            final var contractMethods = ceService.getContractMethods(byteCodeObjects).stream()
                    .map(GeneralConverter::convertMethodDataToMethodDescription)
                    .collect(toList());

            result = new GetContractMethodsResult(SUCCESS_API_RESPONSE, contractMethods, tokenStandard);
        } catch (Throwable e) {
            result = new GetContractMethodsResult(failureApiResponse(e), emptyList(), NOT_A_TOKEN.getId());
        }
        logger.debug("getContractMethods --> {}", result);
        return result;
    }

    @Override
    public GetContractVariablesResult getContractVariables(List<ByteCodeObject> compilationUnits, ByteBuffer contractState, short version) {
        GetContractVariablesResult result;
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("<-- getContractVariables: compilationUnits={} bytes|contractState={} bytes|version={}",
                             compilationUnits.size(), contractState.array().length, version);
            }
            validateVersion(version);
            result = new GetContractVariablesResult(SUCCESS_API_RESPONSE,
                                                    ceService.getContractVariables(
                                                            byteCodeObjectToByteCodeObjectData(compilationUnits),
                                                            contractState.array()));
        } catch (Throwable e) {
            result = new GetContractVariablesResult(failureApiResponse(e), emptyMap());
        }
        logger.debug("getContractVariables --> {}", result);
        return result;
    }


    @Override
    public CompileSourceCodeResult compileSourceCode(String sourceCode, short version) {
        CompileSourceCodeResult result;
        try {
            logger.debug("<-- compileBytecode: sourceCode={}|version={}", sourceCode, version);
            validateVersion(version);
            result = new CompileSourceCodeResult(SUCCESS_API_RESPONSE,
                                                 byteCodeObjectsDataToByteCodeObjects(ceService.compileContractClass(sourceCode)));
        } catch (CompilationException e) {
            result = new CompileSourceCodeResult();
            result.setStatus(failureApiResponse(e));
        } catch (Throwable e) {
            result = new CompileSourceCodeResult(failureApiResponse(e), emptyList());
        }
        logger.debug("compileBytecode --> {}", result);
        return result;
    }
}
