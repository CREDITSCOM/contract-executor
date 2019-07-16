package com.credits.service.contract;

import com.credits.general.thrift.generated.Variant;
import com.credits.utils.StopWatch;
import org.apache.thrift.annotation.Nullable;
import pojo.session.DeployContractSession;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeoutException;

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static pojo.SmartContractConstants.initSmartContractConstants;

class LimitedExecutionMethod<R> {
    protected DeployContractSession session;
    private StopWatch stopWatch;
    private Throwable exception;
    private long threadId;

    LimitedExecutionMethod(DeployContractSession session) {
        this.session = session;
    }

    protected R runForLimitTime(Callable<R> block) {
        final var task = new FutureTask<>(() -> {
            stopWatch.start();
            final R res;
            try {
                res = block.call();
            } finally {
                stopWatch.stop();
            }
            return res;
        });
        final var limitedTimeThread = new Thread(task);
        threadId = limitedTimeThread.getId();
        stopWatch = new StopWatch(limitedTimeThread);
        exception = null;
        R result = null;
        try {
            limitedTimeThread.setName(session.contractAddress);
            initSmartContractConstants(limitedTimeThread.getId(), session);
            limitedTimeThread.start();
            result = task.get(session.executionTime, MILLISECONDS);

        } catch (TimeoutException e) {
            limitedTimeThread.interrupt();

            if (limitedTimeThread.isAlive()) {
                try {
                    sleep(3);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
                if (limitedTimeThread.isAlive()) {
                    limitedTimeThread.stop();
                    stopWatch.stop();
                    exception = e;
                }
            }

            if (task.isDone()) {
                try {
                    result = task.get();
                } catch (Throwable ex) {
                    exception = ex;
                }
            }
        } catch (Throwable e) {
            exception = e;
        }
        return result;
    }

    protected R runIntoCurrentThread(Callable<R> block){
        R result = null;
        threadId = Thread.currentThread().getId();
        try {
            result = block.call();
        }catch (Throwable e){
            exception = e;
        }
        return result;
    }

    public long spentCpuTime() {
        return stopWatch != null ? stopWatch.getTime() : 0L;
    }

    @Nullable
    private Throwable getExceptionOrNull() {
        return exception;
    }

    protected MethodResult prepareResult(Variant returnValue) {
        return getExceptionOrNull() == null
                ? new MethodResult(returnValue, spentCpuTime(), threadId)
                : new MethodResult(getExceptionOrNull(), spentCpuTime(), threadId);
    }
}
