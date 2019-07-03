package tests.credits;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.Map;

import static tests.credits.SmartContactTestData.builder;
import static tests.credits.TestUtils.readSourceCode;

@Module
public class SmartContractsProviderTestModule {

    @Singleton
    @Provides
    Map<String, SmartContactTestData> provideSmartContractTestDataList() {
        try {
            return Map.of(
                    "MySmartContract",
                    builder().setSourceCode(readSourceCode("com/credits/service/usercode/contractExecutorHandlerTest/MySmartContract.java")).build(),
                    "MyBasicStandard",
                    builder().setSourceCode(readSourceCode("com/credits/service/usercode/contractExecutorHandlerTest/MyBasicStandard.java")).build(),
                    "MyExtensionTokenStandard",
                    builder().setSourceCode(readSourceCode("com/credits/service/usercode/contractExecutorHandlerTest/MyExtensionTokenStandard.java")).build());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

}
