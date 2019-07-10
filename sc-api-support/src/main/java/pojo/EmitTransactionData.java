package pojo;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class EmitTransactionData {
    final private String source;
    final private String target;
    final private double amount;
    final private byte[] userData;

    public EmitTransactionData(String source, String target, double amount, byte[] userData) {
        this.source = source;
        this.target = target;
        this.amount = amount;
        this.userData = Optional.ofNullable(userData).orElse(new byte[0]);
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public double getAmount() {
        return amount;
    }

    public byte[] getUserData() {
        return userData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmitTransactionData that = (EmitTransactionData) o;
        return Double.compare(that.amount, amount) == 0 &&
                Objects.equals(source, that.source) &&
                Objects.equals(target, that.target) &&
                Arrays.equals(userData, that.userData);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(source, target, amount);
        result = 31 * result + Arrays.hashCode(userData);
        return result;
    }

    @Override
    public String toString() {
        return "EmitTransactionData{" +
                "source='" + source + '\'' +
                ", target='" + target + '\'' +
                ", amount=" + amount +
                ", userData=" + Arrays.toString(userData) +
                '}';
    }
}
