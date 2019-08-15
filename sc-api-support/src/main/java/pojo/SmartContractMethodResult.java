package pojo;

import com.credits.general.thrift.generated.APIResponse;
import com.credits.general.thrift.generated.Variant;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.Collections.emptyMap;

public class SmartContractMethodResult {
    public final APIResponse status;
    public final Variant result;
    public final long spentCpuTime;
    public final List<EmitTransactionData> emittedTransactions;
    public final Map<String, Map<String, Number>> changedBalances;

    public SmartContractMethodResult(APIResponse status,
                                     Variant result,
                                     long spentCpuTime,
                                     List<EmitTransactionData> emittedTransactions,
                                     Map<String, Map<String, Number>> changedBalances) {
        this.status = status;
        this.result = result;
        this.spentCpuTime = spentCpuTime;
        this.emittedTransactions = emittedTransactions;
        this.changedBalances = changedBalances;
    }

    public SmartContractMethodResult(APIResponse status, Variant result, long spentCpuTime, List<EmitTransactionData> emittedTransactions) {
        this(status, result, spentCpuTime, emittedTransactions, emptyMap());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SmartContractMethodResult that = (SmartContractMethodResult) o;
        return spentCpuTime == that.spentCpuTime &&
                Objects.equals(status, that.status) &&
                Objects.equals(result, that.result) &&
                Objects.equals(emittedTransactions, that.emittedTransactions) &&
                Objects.equals(changedBalances, that.changedBalances);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, result, spentCpuTime, emittedTransactions, changedBalances);
    }

    @Override
    public String toString() {
        return "SmartContractMethodResult{" +
                "status=" + status +
                ", result=" + result +
                ", spentCpuTime=" + spentCpuTime +
                ", emittedTransactions=" + emittedTransactions +
                ", changedBalances=" + changedBalances +
                '}';
    }
}
