package service.node;

import pojo.EmitTransactionData;

import java.util.List;

public interface NodeApiExecStoreTransactionService extends NodeApiExecInteractionService {

    List<EmitTransactionData> takeAwayEmittedTransactions(long threadId);
}
