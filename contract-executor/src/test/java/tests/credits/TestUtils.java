package tests.credits;

import com.credits.client.executor.thrift.generated.ExecuteByteCodeResult;
import com.credits.client.executor.thrift.generated.MethodHeader;

import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.credits.general.util.GeneralConverter.decodeFromBASE58;
import static com.credits.general.util.Utils.rethrowUnchecked;
import static com.credits.utils.ContractExecutorServiceUtils.variantListOf;
import static java.nio.ByteBuffer.wrap;

public class TestUtils {
    public static ByteBuffer initiatorAddress = wrap(decodeFromBASE58("5B3YXqDTcWQFGAqEJQJP3Bg1ZK8FFtHtgCiFLT5VAxpe"));
    public static ByteBuffer contractAddress = wrap(decodeFromBASE58("5B3YXqDTcWQFGAqEJQJP3Bg1ZK8FFtHtgCiFLT5VAxpd"));

    public static String readSourceCode(String resourcePath) {
        return rethrowUnchecked(() -> {
            String sourceCodePath = String.format("%s/src/test/resources/%s", Paths.get("").toAbsolutePath(), resourcePath);
            return new String(Files.readAllBytes(Paths.get(sourceCodePath)));
        });
    }

    public static byte[] getInvokedContractState(ExecuteByteCodeResult deployResult) {
        return deployResult.results.get(0).getContractsState().get(contractAddress).array();
    }

    public static MethodHeader methodHeaderOf(String methodName, Object... params) {
        return new MethodHeader(methodName, variantListOf(params));
    }

    public static Object buildInstanceUseFirstConstructor(Class<?> clazz){
        return rethrowUnchecked(() -> clazz.getDeclaredConstructors()[0].newInstance());
    }
}

