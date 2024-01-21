package com.am1goo.bloodseeker.android.trails;

import java.util.List;

import com.am1goo.bloodseeker.android.IResult;
import com.am1goo.bloodseeker.android.ITrail;
import com.am1goo.bloodseeker.android.Utilities;

public class ClassNameTrail implements ITrail {

	private final String[] classNames;
	
	public ClassNameTrail(String className) {
		this.classNames = new String[] { className };
	}
	
	public ClassNameTrail(String[] classNames) {
		this.classNames = classNames;
	}
	
	@Override
	public void seek(List<IResult> result, List<Exception> exceptions) {
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
