package com.am1goo.bloodseeker.android.trails;

import java.util.List;

import com.am1goo.bloodseeker.android.AppContext;
import com.am1goo.bloodseeker.android.IResult;
import com.am1goo.bloodseeker.android.ITrail;

public class LibraryTrail extends BaseTrail {

	private final String[] libraryNames;
	
	public LibraryTrail(String libraryName) {
		this.libraryNames = new String[] { libraryName };
	}
	
	public LibraryTrail(String[] libraryNames) {
		this.libraryNames = libraryNames;
	}
	
	@Override
	public void seek(AppContext context, List<IResult> result, List<Exception> exceptions) {
		if (libraryNames == null)
			return;

		for (String libraryName : libraryNames) {
			if (libraryName == null)
				continue;

			boolean isLibraryLoaded;  
			try {
				System.loadLibrary(libraryName);
				isLibraryLoaded = true;
			}
			catch (UnsatisfiedLinkError ex) {
				//do nothing
				isLibraryLoaded = false;
			}
			catch (Exception ex) {
				exceptions.add(ex);
				isLibraryLoaded = false;
			}
			
			if (isLibraryLoaded) {
				result.add(new Result(libraryName));
			}
		}
	}
	
	public class Result implements IResult {
		private final String libraryName;
		
		public Result(String libraryName) {
			this.libraryName = libraryName;
		}
		
		@Override
		public String toString() {
			return "Library '" + libraryName + "' found";
		}
	}
}
