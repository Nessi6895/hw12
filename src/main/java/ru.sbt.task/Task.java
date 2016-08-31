package ru.sbt.task;

import java.util.concurrent.*;

public class Task<T> {
    private final Callable<T> callable;
    private T result = null;
    private volatile boolean isWorking = false;
    private volatile boolean isThrowsException = false;

    public Task(Callable<T> callable) {
        this.callable = callable;
    }


    public T get() throws InterruptedException {
        if (result != null) return result;
        if (isThrowsException) throw new RuntimeException("Exception during execution");

        synchronized (this) {
            startWork();
            try {
                result = callable.call();
                endWork();
                return result;
            } catch (Exception e) {
                isThrowsException = true;
                endWork();
                throw new RuntimeException("Exception during execution");
            }
        }
    }

    private void endWork() {
        isWorking = false;
        notify();
    }

    private void startWork() throws InterruptedException {
        while (isWorking)
            wait();
        isWorking = true;
    }
}
