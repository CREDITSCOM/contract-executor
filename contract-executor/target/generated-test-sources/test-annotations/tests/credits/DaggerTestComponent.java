package tests.credits;

import com.credits.secure.PermissionsManager;
import com.credits.thrift.ContractExecutorHandler;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import java.util.Map;
import javax.annotation.processing.Generated;
import javax.inject.Provider;
import service.executor.ContractExecutorService;
import service.node.NodeApiExecInteractionService;
import tests.credits.service.ContractExecutorTestContext;
import tests.credits.service.ContractExecutorTestContext_MembersInjector;
import tests.credits.thrift.ContractExecutorHandlerTest;
import tests.credits.thrift.ContractExecutorHandlerTest_MembersInjector;

@Generated(
  value = "dagger.internal.codegen.ComponentProcessor",
  comments = "https://google.github.io/dagger"
)
public final class DaggerTestComponent implements TestComponent {
  private Provider<NodeApiExecInteractionService> provideMockNodeApiInteractionServiceProvider;

  private Provider<PermissionsManager> providesPermissionsManagerProvider;

  private Provider<ContractExecutorService> provideContractExecutorServiceProvider;

  private Provider<Map<TestContract, SmartContactTestData>> provideSmartContractTestDataMapProvider;

  private DaggerTestComponent(
      TestModule testModuleParam,
      SmartContractsProviderTestModule smartContractsProviderTestModuleParam) {

    initialize(testModuleParam, smartContractsProviderTestModuleParam);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static TestComponent create() {
    return new Builder().build();
  }

  private ContractExecutorHandler getContractExecutorHandler() {
    return new ContractExecutorHandler(provideContractExecutorServiceProvider.get());
  }

  @SuppressWarnings("unchecked")
  private void initialize(
      final TestModule testModuleParam,
      final SmartContractsProviderTestModule smartContractsProviderTestModuleParam) {
    this.provideMockNodeApiInteractionServiceProvider =
        DoubleCheck.provider(
            TestModule_ProvideMockNodeApiInteractionServiceFactory.create(testModuleParam));
    this.providesPermissionsManagerProvider =
        DoubleCheck.provider(TestModule_ProvidesPermissionsManagerFactory.create(testModuleParam));
    this.provideContractExecutorServiceProvider =
        DoubleCheck.provider(
            TestModule_ProvideContractExecutorServiceFactory.create(
                testModuleParam,
                provideMockNodeApiInteractionServiceProvider,
                providesPermissionsManagerProvider));
    this.provideSmartContractTestDataMapProvider =
        DoubleCheck.provider(
            SmartContractsProviderTestModule_ProvideSmartContractTestDataMapFactory.create(
                smartContractsProviderTestModuleParam));
  }

  @Override
  public void inject(ContractExecutorTestContext contractExecutorTestContext) {
    injectContractExecutorTestContext(contractExecutorTestContext);
  }

  @Override
  public void inject(ContractExecutorHandlerTest contractExecutorHandlerTest) {
    injectContractExecutorHandlerTest(contractExecutorHandlerTest);
  }

  @CanIgnoreReturnValue
  private ContractExecutorTestContext injectContractExecutorTestContext(
      ContractExecutorTestContext instance) {
    ContractExecutorTestContext_MembersInjector.injectCeService(
        instance, provideContractExecutorServiceProvider.get());
    ContractExecutorTestContext_MembersInjector.injectSmartContractsRepository(
        instance, provideSmartContractTestDataMapProvider.get());
    return instance;
  }

  @CanIgnoreReturnValue
  private ContractExecutorHandlerTest injectContractExecutorHandlerTest(
      ContractExecutorHandlerTest instance) {
    ContractExecutorHandlerTest_MembersInjector.injectContracts(
        instance, provideSmartContractTestDataMapProvider.get());
    ContractExecutorHandlerTest_MembersInjector.injectMockCEService(
        instance, provideContractExecutorServiceProvider.get());
    ContractExecutorHandlerTest_MembersInjector.injectContractExecutorHandler(
        instance, getContractExecutorHandler());
    return instance;
  }

  public static final class Builder {
    private TestModule testModule;

    private SmartContractsProviderTestModule smartContractsProviderTestModule;

    private Builder() {}

    public Builder testModule(TestModule testModule) {
      this.testModule = Preconditions.checkNotNull(testModule);
      return this;
    }

    public Builder smartContractsProviderTestModule(
        SmartContractsProviderTestModule smartContractsProviderTestModule) {
      this.smartContractsProviderTestModule =
          Preconditions.checkNotNull(smartContractsProviderTestModule);
      return this;
    }

    public TestComponent build() {
      if (testModule == null) {
        this.testModule = new TestModule();
      }
      if (smartContractsProviderTestModule == null) {
        this.smartContractsProviderTestModule = new SmartContractsProviderTestModule();
      }
      return new DaggerTestComponent(testModule, smartContractsProviderTestModule);
    }
  }
}
