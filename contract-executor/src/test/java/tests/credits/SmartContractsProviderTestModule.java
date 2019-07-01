package tests.credits;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.List;

import static tests.credits.SmartContactTestData.builder;
import static tests.credits.TestUtils.readSourceCode;

@Module
public class SmartContractsProviderTestModule {

    @Singleton
    @Provides
    List<SmartContactTestData> provideSmartContractTestDataList() {
        try {
            return List.of(
                    builder().setSourceCode(readSourceCode("com/credits/service/usercode/contractExecutorHandlerTest/MySmartContract.java")).build(),
                    builder().setSourceCode(readSourceCode("com/credits/service/usercode/contractExecutorHandlerTest/MyBasicStandard.java")).build());
        } catch (Throwable e){
            throw new RuntimeException(e);
        }
    }

}
