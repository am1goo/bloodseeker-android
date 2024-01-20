package com.am1goo.bloodseeker.android;

import java.util.List;

public class Report {
	
	private boolean success;
    private List<IResult> results;
    private List<Exception> exceptions;
    
    public Report(List<IResult> results, List<Exception> exceptions) {
    	this.success = results != null && !results.isEmpty();
    	this.results = results;
    	this.exceptions = exceptions;
    }
    
    public boolean isSuccess() {
    	return success;
    }
    
    public List<IResult> getResults() {
    	return results;
    }
    
    public String[] getEvidence() {
    	if (results == null) {
    		return new String[0];
    	}
    	
    	String[] array = new String[results.size()];
    	for (int i = 0; i < results.size(); ++i) {
    		array[i] = results.get(i).toString();
    	}
    	return array;
    }
    
    public List<Exception> getExceptions() {	
    	return exceptions;
    }
    
    public String[] getErrors() {
    	if (exceptions == null) {
    		return new String[0];
    	}
    	
    	String[] array = new String[exceptions.size()];
    	for (int i = 0; i < exceptions.size(); ++i) {
    		array[i] = exceptions.get(i).toString();
    	}
    	return array;
    }
}
