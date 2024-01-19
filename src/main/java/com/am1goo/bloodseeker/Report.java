package com.am1goo.bloodseeker;

import java.util.List;

public class Report {
	
	private boolean success;
    private List<IResult> results;
    private List<Exception> exceptions;
    
    public Report(List<IResult> results, List<Exception> exceptions) {
    	this.success = results != null && results.size() > 0;
    	this.results = results;
    	this.exceptions = exceptions;
    }
    
    public boolean isSuccess() {
    	return success;
    }
    
    public List<IResult> getResults() {
    	return results;
    }
    
    public List<Exception> getExceptions() {	
    	return exceptions;
    }
}
