package tests.credits.service.node;

import com.credits.service.node.apiexec.NodeApiExecInteractionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pojo.EmitTransactionData;
import tests.credits.DaggerTestComponent;

import javax.inject.Inject;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;

public class NodeApiExecInteractionServiceImplTest {

    @Inject
    public NodeApiExecInteractionServiceImpl nodeApiExecService;

    @BeforeEach
    void setUp() {
        DaggerTestComponent.builder().build().inject(this);
    }

    @Test
    @DisplayName("sendTransaction must be stored emitted transactions by threadId and remove if using takeAwayEmittedTransaction")
    void sendTransactionTest() {
        final var threadId = Thread.currentThread().getId();
        final var accessId = 0;
        final var sourceAddress = "sourceAddress";
        final var targetAddress = "targetAddress";
        final var amount = 10;

        nodeApiExecService.sendTransaction(accessId, sourceAddress, targetAddress, amount, null);
        nodeApiExecService.sendTransaction(accessId, sourceAddress, targetAddress, amount, null);

        var emittedTransactions = nodeApiExecService.takeAwayEmittedTransactions(threadId);
        assertThat(emittedTransactions.size(), is(2));

        final var expectedEmitTransaction = new EmitTransactionData(sourceAddress, targetAddress, amount, null);
        assertThat(emittedTransactions.get(0), is(expectedEmitTransaction));

        emittedTransactions = nodeApiExecService.takeAwayEmittedTransactions(threadId);
        assertThat(emittedTransactions, empty());
    }
}