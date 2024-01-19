package com.am1goo.bloodseeker.trails;

import java.util.List;

import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.ITrail;

public class ClassNameTrail implements ITrail {
	
	private String[] classNames;
	
	public ClassNameTrail(String className) {
		this.classNames = new String[] { className };
	}
	
	public ClassNameTrail(String[] classNames) {
		this.classNames = classNames;
	}
	
	@Override
	public void seek(List<IResult> result, List<Exception> exceptions) {
		for (int i = 0; i < classNames.length; ++i) {
			String className = classNames[i];
			Class clazz = getClass(className, exceptions);
			if (clazz != null) {
				result.add(new Result(className));
			}
		}
	}
	
	private Class getClass(String className, List<Exception> exceptions) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException ex) {
        	//do nothing
        }
        catch (Exception ex) {
        	exceptions.add(ex);
        }
        return null;
    }
	
	public class Result implements IResult {
		private String className;
		
		public Result(String className) {
			this.className = className;
		}
		
		@Override
		public String toString() {
			return className;
		}
	}
}
