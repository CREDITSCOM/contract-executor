package tests.credits;

import com.credits.general.classload.ByteCodeContractClassLoader;
import com.credits.general.pojo.ByteCodeObjectData;
import com.credits.general.thrift.generated.ByteCodeObject;
import org.apache.commons.lang3.RandomUtils;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Objects;

import static com.credits.general.util.GeneralConverter.*;
import static com.credits.general.util.compiler.InMemoryCompiler.compileSourceCode;
import static com.credits.thrift.utils.ContractExecutorUtils.compileSmartContractByteCode;
import static com.credits.thrift.utils.ContractExecutorUtils.findRootClass;
import static java.nio.ByteBuffer.wrap;

public class SmartContactTestData {
    private final String sourceCode;
    private final List<ByteCodeObjectData> byteCodeObjectDataList;
    private final List<ByteCodeObject> byteCodeObjectList;
    private final String contractAddressBase58;
    private final ByteBuffer contractAddressBinary;
    private final Class<?> contractClass;

    private SmartContactTestData(String sourceCode,
                                 List<ByteCodeObjectData> byteCodeObjectDataList,
                                 List<ByteCodeObject> byteCodeObjectList, String contractAddressBase58,
                                 ByteBuffer contractAddressBinary, Class<?> contractClass) {
        this.sourceCode = sourceCode;
        this.byteCodeObjectDataList = byteCodeObjectDataList;
        this.byteCodeObjectList = byteCodeObjectList;
        this.contractAddressBase58 = contractAddressBase58;
        this.contractAddressBinary = contractAddressBinary;
        this.contractClass = contractClass;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public List<ByteCodeObjectData> getByteCodeObjectDataList() {
        return byteCodeObjectDataList;
    }

    public List<ByteCodeObject> getByteCodeObjectList() {
        return byteCodeObjectList;
    }

    public String getContractAddressBase58() {
        return contractAddressBase58;
    }

    public ByteBuffer getContractAddressBinary() {
        return contractAddressBinary;
    }

    public Class<?> getContractClass() {
        return contractClass;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmartContactTestData that = (SmartContactTestData) o;
        return Objects.equals(sourceCode, that.sourceCode) &&
                Objects.equals(byteCodeObjectDataList, that.byteCodeObjectDataList) &&
                Objects.equals(contractAddressBase58, that.contractAddressBase58) &&
                Objects.equals(contractAddressBinary, that.contractAddressBinary);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceCode, byteCodeObjectDataList, contractAddressBase58, contractAddressBinary);
    }

    @Override
    public String toString() {
        return "SmartContactTestData{" +
                "sourceCode='" + sourceCode + '\'' +
                ", byteCodeObjectDataList=" + byteCodeObjectDataList +
                ", contractAddressBase58='" + contractAddressBase58 + '\'' +
                ", contractAddressBinary=" + contractAddressBinary +
                '}';
    }

    public static class Builder {
        private String sourceCode;
        private List<ByteCodeObjectData> byteCodeObjectDataList;
        private List<ByteCodeObject> byteCodeObjectList;
        private String contractAddressBase58;
        private ByteBuffer contractAddressBinary;
        private Class<?> contractClass;

        public Builder setContractAddressBase58(String contractAddress) {
            this.contractAddressBase58 = contractAddress;
            return this;
        }

        public Builder setContractAddressBinary(byte[] contractAddress) {
            this.contractAddressBase58 = encodeToBASE58(contractAddress);
            return this;
        }

        public Builder setSourceCode(String sourceCode) {
            this.sourceCode = sourceCode;
            return this;
        }

        public Builder setByteCodeObjectDataList(List<ByteCodeObjectData> byteCodeObjectDataList) {
            this.byteCodeObjectDataList = byteCodeObjectDataList;
            return this;
        }

        public SmartContactTestData build() {
            this.contractAddressBase58 = this.contractAddressBase58 == null
                    ? contractAddressBinary != null
                    ? encodeToBASE58(contractAddressBinary.array())
                    : encodeToBASE58(RandomUtils.nextBytes(32))
                    : this.contractAddressBase58;
            this.contractAddressBinary = this.contractAddressBinary == null
                    ? wrap(decodeFromBASE58(contractAddressBase58))
                    : this.contractAddressBinary;
            this.sourceCode = this.sourceCode == null
                    ? "public class Foo{ public void bar(){}; }"
                    : this.sourceCode;
            this.byteCodeObjectDataList = this.byteCodeObjectDataList == null
                    ? compilationPackageToByteCodeObjectsData(compileSourceCode(sourceCode))
                    : this.byteCodeObjectDataList;
            this.byteCodeObjectList = this.byteCodeObjectList == null
                    ? byteCodeObjectsDataToByteCodeObjects(this.byteCodeObjectDataList)
                    : this.byteCodeObjectList;
            this.contractClass = findRootClass(compileSmartContractByteCode(byteCodeObjectDataList, new ByteCodeContractClassLoader()));
            return new SmartContactTestData(sourceCode, byteCodeObjectDataList, byteCodeObjectList, contractAddressBase58, contractAddressBinary,
                                            contractClass);
        }
    }
}
