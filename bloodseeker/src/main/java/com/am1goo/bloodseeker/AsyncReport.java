package com.am1goo.bloodseeker;

public class AsyncReport extends Async<Report> {

    public AsyncReport() {
        super();
    }

    public Report getReport() {
        return getResult();
    }
}
