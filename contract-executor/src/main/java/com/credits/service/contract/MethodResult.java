package com.credits.service.contract;

import com.credits.general.thrift.generated.Variant;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.credits.general.thrift.generated.Variant._Fields.V_STRING;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

public class MethodResult {
    private final long spentCpuTime;
    private final Variant returnValue;
    private final Throwable exception;
    private final long threadId;
    private Object invokedObject;
    private Map<String, Map<String, Number>> changedBalances = new HashMap<>();

    public MethodResult(Variant returnValue, long spentCpuTime, long threadId) {
        this.spentCpuTime = spentCpuTime;
        this.returnValue = returnValue;
        this.threadId = threadId;
        exception = null;
    }

    public MethodResult(Throwable exception, long spentCpuTime, long threadId) {
        this.spentCpuTime = spentCpuTime;
        this.exception = exception;

        returnValue = new Variant(V_STRING, getRootCauseMessage(exception));
        this.threadId = threadId;
    }

    public long getSpentCpuTime() {
        return spentCpuTime;
    }

    public Variant getReturnValue() {
        return returnValue;
    }

    public Throwable getException() {
        return exception;
    }

    public Object getInvokedObject() {
        return invokedObject;
    }

    public void setInvokedObject(Object invokedObject) {
        this.invokedObject = invokedObject;
    }

    public long getThreadId() {
        return threadId;
    }

    public Map<String, Map<String, Number>> getChangedBalances() {
        return changedBalances;
    }

    public void setChangedBalances(Map<String, Map<String, Number>> changedBalances) {
        this.changedBalances = changedBalances;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodResult result = (MethodResult) o;
        return spentCpuTime == result.spentCpuTime &&
                threadId == result.threadId &&
                Objects.equals(returnValue, result.returnValue) &&
                Objects.equals(exception, result.exception) &&
                Objects.equals(invokedObject, result.invokedObject) &&
                Objects.equals(changedBalances, result.changedBalances);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spentCpuTime, returnValue, exception, threadId, invokedObject, changedBalances);
    }

    @Override
    public String toString() {
        return "MethodResult{" +
                "spentCpuTime=" + spentCpuTime +
                ", returnValue=" + returnValue +
                ", exception=" + exception +
                ", threadId=" + threadId +
                ", invokedObject=" + invokedObject +
                ", changedBalances=" + changedBalances +
                '}';
    }
}
