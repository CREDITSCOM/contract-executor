package tests.credits;

import static java.io.File.separator;

public enum TestContract {
    SmartContractV0TestImpl("SmartContractV0TestImpl.java"),
    SmartContractV2TestImpl("SmartContractV2TestImpl.java"),
    ExtensionTokenStandardTestImpl("ExtensionTokenStandardTestImpl.java"),
    BasicStandardTestImpl("BasicStandardTestImpl.java"),
    SandboxTestContract("SandboxTestContract.java"),
    AnnotationTestContract("AnnotationTestContract.java"),
    TroubleConstructor("TroubleConstructor.java"),
    VariablesTestContract("VariablesTestContract.java"),
    MethodParameterTestContract("MethodParametersTestContract.java"),
    GetContractMethodsTestContract("GetContractMethodsTestContract.java"),
    BasicTokenStandardV2Impl("BasicTokenStandardV2Impl.java");

    public final String path;

    TestContract(String fileContractName) {
        this.path = "contracts" + separator + fileContractName;
    }
}
