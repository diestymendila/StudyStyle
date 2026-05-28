package com.example.studystyle.background;

public interface BackgroundTask<T> {
    T doInBackground();
    void onResult(T result);
}
