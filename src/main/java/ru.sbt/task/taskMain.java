package ru.sbt.task;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class taskMain {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Task<Integer> task = new Task<Integer>(() -> {
            Thread.sleep(1000);
            System.out.println("Was counting " + Thread.currentThread().getName());
            throw new RuntimeException();
            /*return count;*/
        });
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    System.out.println("Finished " + Thread.currentThread().getName() + " " + task.get());
                } catch (InterruptedException e) {
                }
            }).start();
        }
        Thread.sleep(8000);
        System.out.println("This threads shouldn't count(most of the time)");
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                try {
                    System.out.println("Finished " + Thread.currentThread().getName() + " " + task.get());
                } catch (InterruptedException e) {
                }
            }).start();
        }

    }
}
