package tests.credits;

import dagger.Module;
import dagger.Provides;

import java.util.Map;

import static com.credits.general.util.Utils.rethrowUnchecked;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;
import static tests.credits.SmartContactTestData.builder;
import static tests.credits.TestUtils.readSourceCode;

@Module
public class SmartContractsProviderTestModule {

    public static final Map<TestContract, SmartContactTestData> smartContractTestDataMap =
            stream(TestContract.values())
                    .collect(toMap((k) -> k, v -> builder().setSourceCode(rethrowUnchecked(() -> readSourceCode(v.path))).build()));

    @Provides
    Map<TestContract, SmartContactTestData> provideSmartContractTestDataMap() {
        return smartContractTestDataMap;
    }
}
