package com.am1goo.bloodseeker.android.trails;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.util.ArraySet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.am1goo.bloodseeker.android.AppContext;
import com.am1goo.bloodseeker.android.IResult;
import com.am1goo.bloodseeker.android.ITrail;
import com.am1goo.bloodseeker.android.Utilities;

import dalvik.system.DexFile;

public class PackageNameTrail extends BaseTrail {

	private final String[] packageNames;
	
	public PackageNameTrail(String packageName) {
		this.packageNames = new String[] { packageName };
	}
	
	public PackageNameTrail(String[] packageNames) {
		this.packageNames = packageNames;
	}
	
	@Override
	public void seek(AppContext context, List<IResult> result, List<Exception> exceptions) {
		if (packageNames == null)
			return;

		try {
			Set<String> foundPackages = findPackageName(context, packageNames, exceptions);
			for (String foundPackage : foundPackages) {
				result.add(new Result(foundPackage));
			}
		}
		catch (Exception ex) {
			exceptions.add(ex);
		}
		catch (Error err) {
			exceptions.add(new Exception(err));
		}
	}

	private Set<String> findPackageName(AppContext context, String[] packageNames, List<Exception> exceptions) {
		Activity activity = context.getActivity();
		if (activity == null)
			return new HashSet<String>();

		JarFile jarFile = context.getBaseApk();
		if (jarFile == null)
			return new HashSet<String>();

		Set<String> results = new HashSet<String>();
		findPackageName(packageNames, jarFile, "classes.dex", results, exceptions);

		boolean next = true;
		int nextIndex = 2;
		while (next) {
			boolean found = findPackageName(packageNames, jarFile, "classes" + nextIndex + ".dex", results, exceptions);
			if (found) {
				nextIndex++;
			} else {
				next = false;
			}
		}
		return results;
	}

	private static boolean findPackageName(String[] packageNames, JarFile jarFile, String filename, Set<String> results, List<Exception> exceptions) {
		ZipEntry zipEntry = jarFile.getEntry(filename);
		if (zipEntry == null)
			return false;

		InputStream inputStream = null;
		try {
			inputStream = jarFile.getInputStream(zipEntry);
		} catch (IOException ex) {
			exceptions.add(ex);
		}

		if (inputStream == null)
			return false;

		findPackageName(packageNames, inputStream, results, exceptions);

		try {
			inputStream.close();
		} catch (IOException ex) {
			exceptions.add(ex);
		}
		return true;
	}

	private static void findPackageName(String[] packageNames, InputStream inputStream, Set<String> results, List<Exception> exceptions) {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("classes", ".dex");
			OutputStream outputStream = new FileOutputStream(tempFile);

			byte[] buffer = new byte[8 * 1024];
			int bytesRead;
			while ((bytesRead = inputStream.read(buffer)) != -1) {
				outputStream.write(buffer, 0, bytesRead);
			}
			outputStream.close();

			DexFile dexFile = new DexFile(tempFile);
			Enumeration<String> dexEntries = dexFile.entries();
			while (dexEntries.hasMoreElements()) {
				String className = dexEntries.nextElement();

				for (String packageName : packageNames) {
					if (packageName == null)
						continue;

					if (className.startsWith(packageName)) {
						results.add(packageName);
					}
				}
			}
		}
		catch (IOException ex) {
			exceptions.add(ex);
		}
		finally {
			if (tempFile != null) {
				boolean deleted = tempFile.delete();
				if (!deleted)
					tempFile.deleteOnExit();
			}
		}
	}
	
	public class Result implements IResult {
		private final String packageName;
		
		public Result(String packageName) {
			this.packageName = packageName;
		}
		
		@Override
		public String toString() {
			return "Package '" + packageName + "' found";
		}
	}
}
