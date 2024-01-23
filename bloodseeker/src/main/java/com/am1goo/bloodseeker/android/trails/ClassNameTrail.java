package com.am1goo.bloodseeker.android.trails;

import java.util.List;

import com.am1goo.bloodseeker.android.AppContext;
import com.am1goo.bloodseeker.android.IResult;
import com.am1goo.bloodseeker.android.ITrail;
import com.am1goo.bloodseeker.android.Utilities;

public class ClassNameTrail extends BaseTrail {

	private final String[] classNames;
	
	public ClassNameTrail(String className) {
		this(new String[] { className } );
	}
	
	public ClassNameTrail(String[] classNames) {
		this.classNames = classNames;
	}
	
	@Override
	public void seek(AppContext context, List<IResult> result, List<Exception> exceptions) {
		if (classNames == null)
			return;

		for (String className : classNames) {
			if (className == null)
				continue;

			Class<?> clazz = Utilities.getClass(className, exceptions);
			if (clazz != null) {
				result.add(new Result(className));
			}
		}
	}

	public class Result implements IResult {
		private final String className;
		
		public Result(String className) {
			this.className = className;
		}
		
		@Override
		public String toString() {
			return "Class '" + className + "' found";
		}
	}
}
