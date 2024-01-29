package com.am1goo.bloodseeker.trails;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.am1goo.bloodseeker.BloodseekerExceptions;
import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.ITrail;
import com.am1goo.bloodseeker.update.IRemoteUpdateTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateFile;
import com.am1goo.bloodseeker.update.RemoteUpdateReader;
import com.am1goo.bloodseeker.update.RemoteUpdateWriter;

public class LibraryTrail implements IRemoteUpdateTrail, ITrail {

	private String[] libraryNames;

	public LibraryTrail() {
	}
	
	public LibraryTrail(String libraryName) {
		this(new String[] { libraryName } );
	}
	
	public LibraryTrail(String[] libraryNames) {
		this.libraryNames = libraryNames;
	}

	@Override
	public void load(RemoteUpdateReader reader) throws IOException {
		libraryNames = reader.readStringArray();
	}

	@Override
	public void save(RemoteUpdateWriter writer) throws IOException {
		writer.writeStringArray(libraryNames, RemoteUpdateFile.CHARSET_NAME);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		LibraryTrail that = (LibraryTrail) o;
		return Arrays.equals(libraryNames, that.libraryNames);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(libraryNames);
	}

	@Override
	public void seek(List<IResult> result, BloodseekerExceptions exceptions) {
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
				exceptions.add(this, ex);
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
		
		@NonNull
        @Override
		public String toString() {
			return "Library '" + libraryName + "' found";
		}
	}
}
