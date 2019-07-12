package com.credits.utils;

import com.credits.client.executor.thrift.generated.EmittedTransaction;
import com.credits.client.executor.thrift.generated.apiexec.SmartContractGetResult;
import com.credits.general.pojo.ByteCodeObjectData;
import com.credits.general.thrift.generated.ByteCodeObject;
import pojo.EmitTransactionData;
import pojo.apiexec.GetSmartCodeResultData;
import pojo.apiexec.SmartContractGetResultData;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import static com.credits.general.util.GeneralConverter.bigDecimalToAmount;
import static com.credits.general.util.GeneralConverter.decodeFromBASE58;
import static com.credits.general.util.GeneralPojoConverter.createApiResponseData;
import static java.nio.ByteBuffer.wrap;


public class ApiExecClientPojoConverter {

    public static GetSmartCodeResultData createGetSmartCodeResultData(SmartContractGetResult thriftStruct) {

        return new GetSmartCodeResultData(
                createApiResponseData(thriftStruct.getStatus()),
                thriftStruct.getByteCodeObjects().stream().map(ApiExecClientPojoConverter::createByteCodeObjectData).collect(Collectors.toList()),
                thriftStruct.getContractState()
        );
    }

    public static SmartContractGetResultData createSmartContractGetResultData(SmartContractGetResult thriftStruct) {
        return new SmartContractGetResultData(
                createApiResponseData(thriftStruct.getStatus()),
                thriftStruct.getByteCodeObjects().stream().map(ApiExecClientPojoConverter::createByteCodeObjectData).collect(Collectors.toList()),
                thriftStruct.getContractState(),
                thriftStruct.stateCanModify
        );
    }

    public static ByteCodeObjectData createByteCodeObjectData(ByteCodeObject thriftStruct) {
        return new ByteCodeObjectData(thriftStruct.getName(), thriftStruct.getByteCode());
    }

    public static List<EmittedTransaction> convertEmittedTransactionDataToEmittedTransaction(List<EmitTransactionData> emittedTransactions) {
        return emittedTransactions.stream()
                .map(et -> {
                    final var source = wrap(decodeFromBASE58(et.getSource()));
                    final var target = wrap(decodeFromBASE58(et.getTarget()));
                    final var amount = bigDecimalToAmount(BigDecimal.valueOf(et.getAmount()));
                    final var emittedTransaction = new EmittedTransaction(source, target, amount);
                    emittedTransaction.setUserData(et.getUserData());
                    return emittedTransaction;
                }).collect(Collectors.toList());
    }
}
