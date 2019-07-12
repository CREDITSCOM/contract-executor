package tests.credits;

import dagger.Component;
import tests.credits.service.ContractExecutorTestContext;
import tests.credits.service.node.NodeApiExecInteractionServiceImplTest;
import tests.credits.thrift.ContractExecutorHandlerTest;

import javax.inject.Singleton;


@Singleton
@Component(modules = {TestModule.class, SmartContractsProviderTestModule.class})
public interface TestComponent{
    void inject(ContractExecutorTestContext contractExecutorTestContext);
    void inject(ContractExecutorHandlerTest contractExecutorHandlerTest);
    void inject(NodeApiExecInteractionServiceImplTest nodeApiExecInteractionServiceImplTest);
}
