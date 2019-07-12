package com.credits.service.contract;

import com.credits.general.thrift.generated.Variant;

import java.util.Objects;

import static com.credits.general.thrift.generated.Variant._Fields.V_STRING;
import static org.apache.commons.lang3.exception.ExceptionUtils.getRootCauseMessage;

class MethodResult {
    private final long spentCpuTime;
    private final Variant returnValue;
    private final Throwable exception;
    private final long threadId;
    private Object invokedObject;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodResult that = (MethodResult) o;
        return spentCpuTime == that.spentCpuTime &&
                threadId == that.threadId &&
                Objects.equals(returnValue, that.returnValue) &&
                Objects.equals(exception, that.exception) &&
                Objects.equals(invokedObject, that.invokedObject);
    }

    @Override
    public int hashCode() {
        return Objects.hash(spentCpuTime, returnValue, exception, threadId, invokedObject);
    }

    @Override
    public String toString() {
        return "MethodResult{" +
                "spentCpuTime=" + spentCpuTime +
                ", returnValue=" + returnValue +
                ", exception=" + exception +
                ", threadId=" + threadId +
                ", invokedObject=" + invokedObject +
                '}';
    }
}
