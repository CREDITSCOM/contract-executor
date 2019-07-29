package tests.credits;

import com.credits.ioc.AppComponent;
import com.credits.service.contract.CurrentThreadMethodExecutor;
import dagger.Component;
import tests.credits.service.ContractExecutorTestContext;
import tests.credits.service.node.NodeApiExecInteractionServiceImplTest;
import tests.credits.thrift.ContractExecutorHandlerTest;

import javax.inject.Singleton;


@Singleton
@Component(modules = {TestModule.class, SmartContractsProviderTestModule.class})
public interface TestComponent extends AppComponent {
    void inject(ContractExecutorTestContext contractExecutorTestContext);
    void inject(ContractExecutorHandlerTest contractExecutorHandlerTest);
    void inject(NodeApiExecInteractionServiceImplTest nodeApiExecInteractionServiceImplTest);
    void inject(CurrentThreadMethodExecutor methodExecutor);
}
