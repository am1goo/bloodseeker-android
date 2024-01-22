package com.am1goo.bloodseeker.android;

public class Async<T> {

    private final Object lock = new Object();

    private boolean isDone;
    private T result;
    private Exception exception;

    public boolean isDone() {
        return isDone;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) throws Exception {
        synchronized (lock) {
            if (this.isDone)
                throw new Exception("result already done");

            this.isDone = true;
            this.result = result;
        }
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        synchronized (lock) {
            if (this.isDone)
                return;

            this.isDone = true;
            this.exception = exception;
        }
    }
}
