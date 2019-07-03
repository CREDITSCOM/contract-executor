package tests.credits;

public enum TestContact {
    MySmartContract("com/credits/service/usercode/contractExecutorHandlerTest/MySmartContract.java"),
    MyExtensionTokenStandard("com/credits/service/usercode/contractExecutorHandlerTest/MyExtensionTokenStandard.java"),
    MyBasicStandard("com/credits/service/usercode/contractExecutorHandlerTest/MyBasicStandard.java");

    final String path;

    TestContact(String path) {
        this.path = path;
    }
}
