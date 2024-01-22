package com.am1goo.bloodseeker.android;

public class AsyncReport extends Async<Report> {

    public AsyncReport() {
        super();
    }

    public Report getReport() {
        return getResult();
    }
}
