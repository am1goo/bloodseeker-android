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

import com.am1goo.bloodseeker.android.IResult;
import com.am1goo.bloodseeker.android.ITrail;
import com.am1goo.bloodseeker.android.Utilities;

import dalvik.system.DexFile;

public class PackageNameTrail implements ITrail {

	private final String[] packageNames;
	
	public PackageNameTrail(String packageName) {
		this.packageNames = new String[] { packageName };
	}
	
	public PackageNameTrail(String[] packageNames) {
		this.packageNames = packageNames;
	}

	private final static String unityPlayer = "com.unity3d.player.UnityPlayer";
	
	@Override
	public void seek(List<IResult> result, List<Exception> exceptions) {
		try {
			Set<String> foundPackages = findPackageName(packageNames, exceptions);
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

	private Set<String> findPackageName(String[] packageNames, List<Exception> exceptions) {
		Class<?> activityClass = Utilities.getClass(unityPlayer, exceptions);
		if (activityClass == null) {
			exceptions.add(new Exception("class " + unityPlayer + " not found"));
			return new HashSet<String>();
		}

		Activity activity;
		try {
			Field fi = activityClass.getDeclaredField("currentActivity");
			activity = (Activity) fi.get(null);
		}
		catch (Exception ex) {
			exceptions.add(ex);
			activity = null;
		}

		if (activity == null) {
			exceptions.add(new Exception("activity '" + unityPlayer + "' is not found"));
			return new HashSet<String>();
		}

		Set<String> results = new HashSet<String>();
		Context ctx = activity.getBaseContext();
		ApplicationInfo appInfo = ctx.getApplicationInfo();
		JarFile jarFile = null;
		try {
			jarFile = new JarFile(appInfo.sourceDir);
			findPackageName(packageNames, jarFile, "classes.dex", results, exceptions);

			boolean next = true;
			int nextIndex = 2;
			while (next) {
				boolean found = findPackageName(packageNames, jarFile, "classes" + nextIndex + ".dex", results, exceptions);
				if (found) {
					nextIndex++;
				}
				else {
					next = false;
				}
			}
			return results;
		}
		catch (IOException ex) {
			exceptions.add(ex);
			return results;
		}
		finally {
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException ex) {
					exceptions.add(ex);
				}
			}
		}
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
