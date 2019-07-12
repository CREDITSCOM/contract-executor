package tests.credits;

import com.credits.client.executor.thrift.generated.ExecuteByteCodeResult;
import com.credits.client.executor.thrift.generated.MethodHeader;
import com.credits.general.thrift.generated.Variant;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.credits.general.util.GeneralConverter.decodeFromBASE58;
import static com.credits.general.util.Utils.getClassType;
import static com.credits.general.util.Utils.rethrowUnchecked;
import static com.credits.general.util.variant.VariantConverter.toVariant;
import static java.nio.ByteBuffer.wrap;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class TestUtils {
    public static ByteBuffer initiatorAddress = wrap(decodeFromBASE58("5B3YXqDTcWQFGAqEJQJP3Bg1ZK8FFtHtgCiFLT5VAxpe"));
    public static ByteBuffer contractAddress = wrap(decodeFromBASE58("5B3YXqDTcWQFGAqEJQJP3Bg1ZK8FFtHtgCiFLT5VAxpd"));

    public static String readSourceCode(String resourcePath) throws IOException {
        String sourceCodePath = String.format("%s/src/test/resources/%s", Paths.get("").toAbsolutePath(), resourcePath);
        return new String(Files.readAllBytes(Paths.get(sourceCodePath)));
    }

    public static byte[] getInvokedContractState(ExecuteByteCodeResult deployResult) {
        return deployResult.results.get(0).getContractsState().get(contractAddress).array();
    }

    public static Variant[][] variantArrayOf(Object[]... params) {
        return stream(params)
                .map(p -> stream(p)
                .map(pp -> toVariant(getClassType(pp), pp))
                .collect(toList()).toArray(Variant[]::new))
                .collect(toList()).toArray(Variant[][]::new);
    }

    public static Variant[][] variantArrayOf(Object... params) {
        return new Variant[][]{stream(params).map(p -> toVariant(getClassType(p), p)).collect(toList()).toArray(Variant[]::new)};
    }

    public static List<Variant> variantListOf(Object... params) {
        return stream(params).map(p -> toVariant(getClassType(p), p)).collect(toList());
    }

    public static MethodHeader methodHeaderOf(String methodName, Object... params) {
        return new MethodHeader(methodName, variantListOf(params));
    }

    public static Object buildInstanceUseFirstConstructor(Class<?> clazz){
        return rethrowUnchecked(() -> clazz.getDeclaredConstructors()[0].newInstance());
    }
}

