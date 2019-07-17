package tests.credits.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

public class ExternalMethodCallTestContext extends ContractExecutorTestContext{

    protected final String calledSmartContractAddress = "5B3YXqDTcWQFGAqEJQJP3Bg1ZK8FFtHtgCiFLT5VAxpe";

    @BeforeEach
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp(testInfo);
    }
}
