package ru.sbt.task;

import java.util.concurrent.*;

public class Task<T> {
    private final Callable<T> callable;
    private T result = null;
    private volatile boolean finished = false;
    private boolean isThrowsException = false;

    public Task(Callable<T> callable) {
        this.callable = callable;
    }


    public T get() throws InterruptedException {
        if (finished) return returnResult();

        synchronized (this) {
            if(!finished) {
                try {
                    result = callable.call();
                    finished = true;
                    return result;
                } catch (Exception e) {
                    isThrowsException = true;
                    finished = true;
                }
            }
            return returnResult();
        }
    }

    private T returnResult(){
        if(isThrowsException) throw new RuntimeException("Exception during execution");
        return result;
    }
}
