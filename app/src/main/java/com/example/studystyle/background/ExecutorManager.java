package com.example.studystyle.background;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorManager {

    private static ExecutorManager instance;
    private final ExecutorService executor;
    private final Handler mainHandler;

    private ExecutorManager() {
        executor = Executors.newFixedThreadPool(4);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    public static ExecutorManager getInstance() {
        if (instance == null) {
            instance = new ExecutorManager();
        }
        return instance;
    }


    public <T> void execute(BackgroundTask<T> task) {
        executor.execute(() -> {
            T result = task.doInBackground();
            mainHandler.post(() -> task.onResult(result));
        });
    }


    public void runInBackground(Runnable runnable) {
        executor.execute(runnable);
    }


    public void runOnMainThread(Runnable runnable) {
        mainHandler.post(runnable);
    }

    public void shutdown() {
        executor.shutdown();
    }
}
