package tests.credits.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;
import tests.credits.SmartContactTestData;
import tests.credits.TestContract;
import tests.credits.UseTestContract;

public class ExternalMethodCallTestContext extends ContractExecutorTestContext{

    private final TestContract defaultTestContract = TestContract.SmartContractV0TestImpl;
    protected final String calledSmartContractAddress = "5B3YXqDTcWQFGAqEJQJP3Bg1ZK8FFtHtgCiFLT5VAxpe";
    protected SmartContactTestData smartContract;
    protected byte[] deployContractState = null;

    @BeforeEach
    protected void setUp(TestInfo testInfo) throws Exception {
        super.setUp();
        selectDefaultTestContractIfMethodNotAnnotated(testInfo);
    }

    private void selectDefaultTestContractIfMethodNotAnnotated(TestInfo testInfo) {
        if (testInfo.getTags().contains(UseTestContract.class.getSimpleName())) {
            testInfo.getTestMethod().ifPresent(m -> {
                final var usingContract = m.getAnnotation(UseTestContract.class).value();
                selectSmartContractAndDeploy(usingContract);
            });
        } else {
            selectSmartContractAndDeploy(defaultTestContract);
        }
    }

    private void selectSmartContractAndDeploy(TestContract testContract) {
        smartContract = smartContractsRepository.get(testContract);
        deployContractState = deploySmartContract(smartContract).newContractState;
    }
}
