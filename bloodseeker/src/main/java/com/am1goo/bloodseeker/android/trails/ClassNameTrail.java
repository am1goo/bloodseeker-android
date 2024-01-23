package com.am1goo.bloodseeker.android.trails;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.am1goo.bloodseeker.android.AppContext;
import com.am1goo.bloodseeker.android.IResult;
import com.am1goo.bloodseeker.android.ITrail;
import com.am1goo.bloodseeker.android.Utilities;
import com.am1goo.bloodseeker.android.update.IRemoteUpdateTrail;
import com.am1goo.bloodseeker.android.update.RemoteUpdateFile;
import com.am1goo.bloodseeker.android.update.RemoteUpdateReader;
import com.am1goo.bloodseeker.android.update.RemoteUpdateWriter;

public class ClassNameTrail extends BaseTrail implements IRemoteUpdateTrail {

	private String[] classNames;

	public ClassNameTrail() {
	}
	
	public ClassNameTrail(String className) {
		this(new String[] { className } );
	}
	
	public ClassNameTrail(String[] classNames) {
		this.classNames = classNames;
	}

	@Override
	public void load(RemoteUpdateReader reader) throws IOException {
		classNames = reader.readStringArray();
	}

	@Override
	public void save(RemoteUpdateWriter writer) throws IOException {
		writer.writeStringArray(classNames, RemoteUpdateFile.CHARSET_NAME);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ClassNameTrail that = (ClassNameTrail) o;
		return Arrays.equals(classNames, that.classNames);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(classNames);
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
