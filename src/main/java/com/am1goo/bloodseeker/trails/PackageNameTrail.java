package com.am1goo.bloodseeker.trails;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.ITrail;

public class PackageNameTrail implements ITrail {

	private String[] packageNames;
	
	public PackageNameTrail(String packageName) {
		this.packageNames = new String[] { packageName };
	}
	
	public PackageNameTrail(String[] packageNames) {
		this.packageNames = packageNames;
	}
	
	@Override
	@SuppressWarnings("rawtypes")
	public void seek(List<IResult> result, List<Exception> exceptions) {
		for (int i =0; i < packageNames.length; ++i) {
			String packageName = packageNames[i];
			Set<Class> foundClasses = findAllClassesUsingClassLoader(packageName, exceptions);
			int foundCount = foundClasses.size(); 
			if (foundCount > 0) {
				result.add(new Result(packageName));
			}
		}
	}

	@SuppressWarnings("rawtypes")
	private Set<Class> findAllClassesUsingClassLoader(String packageName, List<Exception> exceptions) {
        InputStream stream = ClassLoader.getSystemClassLoader()
        	.getResourceAsStream(packageName.replaceAll("[.]", "/"));
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        return reader.lines()
        	.filter(line -> line.endsWith(".class"))
        	.map(line -> getClass(line, packageName, exceptions))
        	.collect(Collectors.toSet());
	}
	
	@SuppressWarnings("rawtypes")
	private Class getClass(String className, String packageName, List<Exception> exceptions) {
        try {
            return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
        } catch (ClassNotFoundException ex) {
        	//do nothing
        }
        catch (Exception ex) {
        	exceptions.add(ex);
        }
        return null;
    }
	
	public class Result implements IResult {
		private String packageName;
		
		public Result(String packageName) {
			this.packageName = packageName;
		}
		
		@Override
		public String toString() {
			return packageName;
		}
	}
}
