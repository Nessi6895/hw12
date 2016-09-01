package ru.sbt.ExecutionManager;

import java.util.*;
import java.util.concurrent.Callable;

public class ContextImpl implements Context {
    private  final Runnable[] tasks;
    private final Set<ContextThread> threads = new HashSet<>();
    private final Runnable callback;
    private final List<Exception> log = new ArrayList<>();
    private int completedTasks = 0;
    private int failedTasks = 0;
    private int interruptedTasks = 0;

    public ContextImpl(Runnable callback, Runnable[] tasks) {
        this.callback = callback;
        this.tasks = tasks;
    }

    @Override
    public void work(){
        for(Runnable r : tasks){
            ContextThread thread = new ContextThread(r);
            threads.add(thread);
            thread.run();
        }
    }

    @Override
    public int getCompletedTaskCount() { return completedTasks; }

    @Override
    public int getFailedTaskCount() {
        return failedTasks;
    }

    @Override
    public int getInterruptedTaskCount() {
        return interruptedTasks;
    }

    @Override
    public void interrupt() {
        for(ContextThread thread: threads){
            thread.interrupt();
            synchronized (this){
                interruptedTasks++;
            }
        }
    }

    @Override
    public boolean isFinished() {
        return threads.isEmpty();
    }

    private class ContextThread extends Thread{
        private final Runnable target;

        public ContextThread(Runnable target) {
            this.target = target;
        }

        @Override
        public void run() {
            try{
                target.run();
                synchronized (this){
                    completedTasks++;
                    finishWork();
                }
            }catch (Exception e){
                synchronized (this){
                    failedTasks++;
                    log.add(e);
                    finishWork();
                }
            }
        }

        private void finishWork(){
            threads.remove(this);
            if(threads.isEmpty()){
                new Thread(callback).start();
            }
        }
    }
}
