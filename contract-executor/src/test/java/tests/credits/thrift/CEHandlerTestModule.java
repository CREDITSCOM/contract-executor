package tests.credits.thrift;

import com.credits.ApplicationProperties;
import com.credits.thrift.ContractExecutorHandler;
import dagger.Component;
import dagger.Module;
import dagger.Provides;
import service.executor.ContractExecutorService;
import tests.credits.SmartContractsProviderTestModule;

import javax.inject.Singleton;

import static org.mockito.Mockito.mock;

@Module
public class CEHandlerTestModule {

    @Singleton
    @Provides
    public ContractExecutorService provideMockContractExecutorService() {
        return mock(ContractExecutorService.class);
    }

    @Singleton
    @Provides
    public ContractExecutorHandler provideContractExecutorHandler(ContractExecutorService contractExecutorService){
        return new ContractExecutorHandler(contractExecutorService, mock(ApplicationProperties.class));
    }

}


@Singleton
@Component(modules = {CEHandlerTestModule.class, SmartContractsProviderTestModule.class})
interface CEHandlerTestComponent {
    void inject(ContractExecutorHandlerTest contractExecutorHandlerTest);
}
