package com.am1goo.bloodseeker;

import java.util.ArrayList;
import java.util.List;

public class Database {
	
	private List<ITrail> trails;
	
	public Database() {
		trails = new ArrayList<ITrail>();
	}
	
	public List<ITrail> getTrails() {
		return trails;
	}
	
	public Database addTrail(ITrail trail) {
		trails.add(trail);
		return this;
	}
}
