package com.am1goo.bloodseeker.android;

import java.util.List;

public interface ITrail {

	void seek(AppContext ctx, List<IResult> result, List<Exception> exceptions);
	 
}
