package tests.credits;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.Map;

import static com.credits.general.util.Utils.rethrowUnchecked;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;
import static tests.credits.SmartContactTestData.builder;
import static tests.credits.TestUtils.readSourceCode;

@Module
public class SmartContractsProviderTestModule {

    @Singleton
    @Provides
    Map<TestContact, SmartContactTestData> provideSmartContractTestDataList() {
        return stream(TestContact.values())
                .collect(toMap((k) -> k, v -> builder().setSourceCode(rethrowUnchecked(() -> readSourceCode(v.path))).build()));
    }
}
