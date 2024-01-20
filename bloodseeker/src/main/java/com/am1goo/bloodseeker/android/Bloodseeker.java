package com.am1goo.bloodseeker.android;

import java.util.List;
import java.util.ArrayList;

public class Bloodseeker {
	
	private List<ITrail> trails;

    public Bloodseeker() {
		super();
		this.trails = new ArrayList<ITrail>();
    }
    
    public boolean useDatabase(Database db) {
    	boolean result = false;
    	
    	List<ITrail> trails = db.getTrails();
    	for (int i = 0; i < trails.size(); ++i) {
    		ITrail trail = trails.get(i);
    		result |= addTrail(trail);
    	}
    	
    	return result;
    }
       
    public boolean addTrail(ITrail trail) {
    	if (trail == null)
    		return false;
    	
    	return trails.add(trail);
    }

    public Report seek()
    {
        List<IResult> results = new ArrayList<IResult>();
        List<Exception> exceptions = new ArrayList<Exception>();

        for (int i = 0; i < trails.size(); ++i) {
            try {
            	ITrail trail = trails.get(i);
                trail.seek(results, exceptions);
            }
            catch (Exception ex) {
                exceptions.add(ex);
            }
        }
        return new Report(results, exceptions);
    }
}
