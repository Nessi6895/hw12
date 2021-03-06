package ru.sbt.ExecutionManager;

public class ExecutionManagerImpl implements ExecutionManager {
    /*private final int nThreads;

    public ExecutionManagerImpl(int nThreads) {
        this.nThreads = nThreads;

    }*/

    @Override
    public Context execute(Runnable callback, Runnable... tasks) {
        Context context = new ContextImpl(callback, tasks);
        context.work();
        return context;
    }

}
