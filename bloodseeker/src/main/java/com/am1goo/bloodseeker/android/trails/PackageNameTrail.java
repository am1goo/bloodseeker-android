package com.am1goo.bloodseeker.android.trails;

import android.app.Activity;

import androidx.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import com.am1goo.bloodseeker.BloodseekerExceptions;
import com.am1goo.bloodseeker.android.AndroidAppContext;
import com.am1goo.bloodseeker.IResult;
import com.am1goo.bloodseeker.update.IRemoteUpdateTrail;
import com.am1goo.bloodseeker.update.RemoteUpdateFile;
import com.am1goo.bloodseeker.update.RemoteUpdateReader;
import com.am1goo.bloodseeker.update.RemoteUpdateWriter;

import dalvik.system.DexFile;

public class PackageNameTrail extends BaseAndroidTrail implements IRemoteUpdateTrail {

	private String[] packageNames;

	public PackageNameTrail() {
	}
	
	public PackageNameTrail(String packageName) {
		this( new String[] { packageName } );
	}
	
	public PackageNameTrail(String[] packageNames) {
		this.packageNames = packageNames;
	}

	@Override
	public void load(RemoteUpdateReader reader) throws IOException {
		packageNames = reader.readStringArray();
	}

	@Override
	public void save(RemoteUpdateWriter writer) throws IOException {
		writer.writeStringArray(packageNames, RemoteUpdateFile.CHARSET_NAME);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		PackageNameTrail that = (PackageNameTrail) o;
		return Arrays.equals(packageNames, that.packageNames);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(packageNames);
	}

	@Override
	public void seek(List<IResult> result, BloodseekerExceptions exceptions) {
		if (packageNames == null)
			return;

		AndroidAppContext context = getContext();
		if (context == null)
			return;

		try {
			Set<String> foundPackages = findPackageName(context, packageNames, exceptions);
			for (String foundPackage : foundPackages) {
				result.add(new Result(foundPackage));
			}
		}
		catch (Exception ex) {
			exceptions.add(this, ex);
		}
		catch (Error err) {
			exceptions.add(this, new Exception(err));
		}
	}

	private Set<String> findPackageName(AndroidAppContext context, String[] packageNames, BloodseekerExceptions exceptions) {
		Activity activity = context.getActivity();
		if (activity == null)
			return new HashSet<String>();

		JarFile jarFile = context.getBaseApkJar();
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

	private static boolean findPackageName(String[] packageNames, JarFile jarFile, String filename, Set<String> results, BloodseekerExceptions exceptions) {
		ZipEntry zipEntry = jarFile.getEntry(filename);
		if (zipEntry == null)
			return false;

		InputStream inputStream = null;
		try {
			inputStream = jarFile.getInputStream(zipEntry);
		} catch (IOException ex) {
			exceptions.add(PackageNameTrail.class, ex);
		}

		if (inputStream == null)
			return false;

		findPackageName(packageNames, inputStream, results, exceptions);

		try {
			inputStream.close();
		} catch (IOException ex) {
			exceptions.add(PackageNameTrail.class, ex);
		}
		return true;
	}

	@SuppressWarnings("deprecation")
	private static void findPackageName(String[] packageNames, InputStream inputStream, Set<String> results, BloodseekerExceptions exceptions) {
		File tempFile = null;
		try {
			tempFile = File.createTempFile("classes", ".dex");
			OutputStream outputStream = Files.newOutputStream(tempFile.toPath());

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
			exceptions.add(PackageNameTrail.class, ex);
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
		
		@NonNull
        @Override
		public String toString() {
			return "Package '" + packageName + "' found";
		}
	}
}
