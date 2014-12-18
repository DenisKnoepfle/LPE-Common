/**
 * Copyright 2014 SAP AG
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lpe.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.tools.ant.DirectoryScanner;

/**
 * Utility class for file operations.
 * 
 * @author Alexander Wert, Roozbeh Farahbod
 * 
 */
public final class LpeFileUtils {

	private static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";
	private static final String JAR_FILE_EXTENSION = ".jar";
	private static final int BUFFER_SIZE = 1024 * 512;
	private static final byte[] BUFFER = new byte[BUFFER_SIZE];

	/**
	 * private constructor due to utility class.
	 */
	private LpeFileUtils() {

	}

	/**
	 * Reads the lines of the given file.
	 * 
	 * @param fileName
	 *            file to read
	 * @return List of Strings representing the lines of the file
	 * @throws IOException
	 *             is thrown if file cannot be read.
	 */
	public static List<String> readLines(String fileName) throws IOException {
		List<String> result = new ArrayList<String>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(fileName)));

		String line = "";

		do {
			line = reader.readLine();
			if (line != null) {
				result.add(line);
			}
		} while (line != null);

		reader.close();

		return result;
	}

	/**
	 * Writes the given lines to a file.
	 * 
	 * @param fileName
	 *            name of the file where to write the lines
	 * @param lines
	 *            lines to write
	 * @throws IOException
	 *             thrown if file cannot be opened
	 */
	public static void writeLines(String fileName, List<String> lines) throws IOException {
		PrintWriter pw = new PrintWriter(new FileOutputStream(fileName));

		for (String line : lines) {
			pw.println(line);
		}

		pw.close();
	}

	/**
	 * Writes the given content to the file.
	 * 
	 * @param content
	 *            content to write
	 * @param fileName
	 *            name of the file where to write the content
	 * @throws IOException
	 *             thrown if file cannot be opened
	 */
	public static void printToFile(String content, String fileName) throws IOException {
		final PrintWriter pw = new PrintWriter(new FileWriter(fileName));
		pw.println(content);
		pw.close();
	}

	/**
	 * Reads the content of the given file.
	 * 
	 * @param fileName
	 *            name of the file where to write the content
	 * @throws IOException
	 *             thrown if stream cannot be read
	 * @return the string read from the file
	 * @see LpeStreamUtils#readFromInputStream(java.io.InputStream)
	 */
	public static String readFromFile(String fileName) throws IOException {
		return LpeStreamUtils.readFromInputStream(new FileInputStream(fileName));
	}

	/**
	 * Writes the object to the given file.
	 * 
	 * @param fileName
	 *            name of the file where to write the object
	 * @param object
	 *            the object to write
	 * @throws IOException
	 *             if an I/O error occurs
	 */
	public static void writeObject(String fileName, Object object) throws IOException {
		ObjectOutputStream outStream = null;
		try {
			BufferedOutputStream bufferedOutStream = new BufferedOutputStream(new FileOutputStream(fileName));
			outStream = new ObjectOutputStream(bufferedOutStream);
			outStream.writeObject(object);
		} finally {
			if (outStream != null) {
				outStream.close();
			}
		}
	}

	/**
	 * Reads the object of the given file.
	 * 
	 * @param file
	 *            the file to read from
	 * @return the object read from the given file
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws ClassNotFoundException
	 *             if class of the serialized object cannot be found
	 */
	public static Object readObject(File file) throws IOException, ClassNotFoundException {
		ObjectInputStream objectIn = null;
		Object object = null;
		try {
			BufferedInputStream bufferedInStream = new BufferedInputStream(new FileInputStream(file));
			objectIn = new ObjectInputStream(bufferedInStream);
			object = objectIn.readObject();
		} finally {
			if (objectIn != null) {
				objectIn.close();
			}
		}
		return object;
	}

	/**
	 * Returns an array of files that their names match the given pattern.
	 * 
	 * @param baseDir
	 *            directory where to search
	 * @param pattern
	 *            filename pattern
	 * @return an array of file names; if there is no such file, it returns an
	 *         empty array.
	 */
	public static String[] getFileNames(String baseDir, String pattern) {
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setIncludes(new String[] { pattern });
		scanner.setBasedir(baseDir);
		scanner.setCaseSensitive(false);
		scanner.scan();
		final String[] result = scanner.getIncludedFiles();
		if (result == null) {
			return new String[] {};
		} else {
			return result;
		}
	}

	/**
	 * Returns a list of files that their names match the given pattern.
	 * 
	 * @param baseDir
	 *            directory where to search
	 * @param pattern
	 *            filename pattern
	 * @return a list of file names; if there is no such file, it returns an
	 *         empty array.
	 */
	public static List<String> getFileNamesAsList(String baseDir, String pattern) {
		return new ArrayList<String>(Arrays.asList(getFileNames(baseDir, pattern)));
	}

	/**
	 * Returns an array of directories that their names match the given pattern.
	 * 
	 * @param baseDir
	 *            directory where to search
	 * @param pattern
	 *            filename pattern
	 * @return an array of directory names; if there is no such directory, it
	 *         returns an empty array.
	 */
	public static String[] getDirNames(String baseDir, String pattern) {
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setIncludes(new String[] { pattern });
		scanner.setBasedir(baseDir);
		scanner.setCaseSensitive(false);
		scanner.scan();
		final String[] result = scanner.getIncludedDirectories();
		if (result == null) {
			return new String[] {};
		} else {
			return result;
		}
	}

	/**
	 * Returns a list of directories that their names match the given pattern.
	 * 
	 * @param baseDir
	 *            directory where to search
	 * @param pattern
	 *            filename pattern
	 * @return a list of directory names; if there is no such directory, it
	 *         returns an empty array.
	 */
	public static List<String> getDirNamesAsList(String baseDir, String pattern) {
		return new ArrayList<String>(Arrays.asList(getDirNames(baseDir, pattern)));
	}

	/**
	 * Given a base directory and a path to a file, it creates a full path to
	 * the file. If the base directory is not absolute, it adds the application
	 * root directory It also takes care of missing file separators.
	 * 
	 * @param baseDir
	 *            base directory
	 * @param fileName
	 *            file name
	 * @param rootFolder
	 *            root folder of the application
	 * @return the absolute path to the file
	 */
	public static String toFullPath(String baseDir, String fileName, String rootFolder) {
		String result = LpeStringUtils.concatFileName(baseDir, fileName);

		File file = new File(result);
		if (!file.isAbsolute()) {
			result = LpeStringUtils.concatFileName(rootFolder, result);
		}

		return result;
	}

	/**
	 * Returns true if the given filename has is an absolute path.
	 * 
	 * @param fileName
	 *            a file name
	 * 
	 * @return true if passed fileName is absolute, otherwise false
	 */
	public static boolean isAbsolutePath(String fileName) {
		final File file = new File(fileName);
		return file.isAbsolute();
	}

	/**
	 * Returns <code>true</code> if the given file exists.
	 * 
	 * @param fileName
	 *            a file name
	 * 
	 * @return true if file exists, otherwise false
	 */
	public static boolean fileExists(String fileName) {
		final File file = new File(fileName);
		return file.exists();
	}

	/**
	 * Given a base directory and a path to a file, it concatinates the two
	 * parts and takes care of missing file separators.
	 * 
	 * @param baseDir
	 *            base directory
	 * @param fileName
	 *            file name
	 * @return the absolute path to the file
	 */
	public static String concatFileName(String baseDir, String fileName) {
		return LpeStringUtils.concatFileName(baseDir, fileName);
	}

	/**
	 * Removes a whole directory.
	 * 
	 * @param directory
	 *            directory to be removed
	 * @throws IOException
	 *             thrown if dir cannot be removed
	 */
	public static void removeDir(String directory) throws IOException {
		File targetDir = new File(directory);
		if (!targetDir.exists()) {
			return;
		}

		if (targetDir.isDirectory()) {
			for (File child : targetDir.listFiles()) {
				if (child != null) {
					removeDir(child.getAbsolutePath());
				}
			}
			targetDir.delete();
		} else {
			targetDir.delete();
		}
	}

	/**
	 * Copies the specified file to the specified directory.
	 * 
	 * @param sourceFile
	 *            file to copy
	 * @param targetDir
	 *            directory where to copy the file
	 * @return new file path
	 * @throws IOException
	 *             if copying fails
	 */
	public static String copyFileToDir(String sourceFile, String targetDir) throws IOException {
		File source = new File(sourceFile);
		if (!targetDir.endsWith("/")) {
			targetDir += "/";
		}
		File target = new File(targetDir + source.getName());
		FileInputStream fis = new FileInputStream(source);
		FileOutputStream fos = new FileOutputStream(target);
		copy(fis, fos);
		fis.close();
		fos.close();
		return target.getAbsolutePath();
	}

	/**
	 * Recursively traverses the directory looking for all files.
	 * 
	 * @param directory
	 *            directory to search within
	 * @return a set of full file names
	 */
	public static List<String> getAllFiles(String directory) {
		List<String> resultList = new ArrayList<String>();
		getAllFiles(new File(directory), resultList);
		return resultList;
	}

	private static void getAllFiles(File file, final List<String> resultList) {
		if (!file.exists()) {
			return;
		} else if (file.isFile()) {
			resultList.add(file.getAbsolutePath());
		} else if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				getAllFiles(child, resultList);
			}
		}
	}

	/**
	 * Copies a whole directory.
	 * 
	 * @param source
	 *            directory to be copied
	 * @param destination
	 *            destination path for the copy
	 * @throws IOException
	 *             if copying fails
	 */
	public static void copyDirectory(String source, String destination) throws IOException {

		File srcDir = new File(source);
		File destDir = new File(destination);

		if (!srcDir.exists()) {
			throw new IOException("Failed copying directory! Source directory does not exist.");
		}

		if (srcDir.isDirectory()) {

			// if directory not exists, create it
			if (!destDir.exists()) {
				destDir.mkdir();
			}

			for (File srcFile : srcDir.listFiles()) {
				File destFile = new File(destDir, srcFile.getName());
				// recursive copy
				copyDirectory(srcFile.getAbsolutePath(), destFile.getAbsolutePath());
			}

		} else {

			InputStream in = new FileInputStream(srcDir);
			OutputStream out = new FileOutputStream(destDir);
			copy(in, out);
			in.close();
			out.close();
		}
	}

	/**
	 * Copies all bytes from an input stream to a given output stream.
	 * 
	 * @param in
	 *            the input stream to read from
	 * @param out
	 *            the output stream to write to
	 * @throws IOException
	 *             if copying fails
	 */
	protected static void copy(final InputStream in, final OutputStream out) throws IOException {
		int bytesRead;
		while ((bytesRead = in.read(BUFFER)) != -1) {
			out.write(BUFFER, 0, bytesRead);
		}
	}

	/**
	 * Finds all jar files inside the passed directory.
	 * 
	 * @param rootDir
	 *            directory to search for jar files
	 * @return list of all jar files
	 */
	public static List<String> findAllJarsInside(String rootDir) {
		List<String> jarPaths = new ArrayList<String>();
		File file = new File(rootDir);
		if (file != null && file.isDirectory()) {
			for (File child : file.listFiles()) {
				jarPaths.addAll(findAllJarsInside(child.getAbsolutePath()));
			}
		} else if (file != null && file.getName().endsWith(JAR_FILE_EXTENSION)) {
			jarPaths.add(file.getAbsolutePath());
		}
		return jarPaths;

	}

	/**
	 * Unpacks the specified file into the specified directory.
	 * 
	 * @param file
	 *            the file to unpack
	 * @param directory
	 *            the directory to unpack into
	 */
	public static void unzip(final File file, final File directory) {
		try {
			final ZipFile zipFile = new ZipFile(file, ZipFile.OPEN_READ);
			try {
				final Enumeration<? extends ZipEntry> zipEntries = zipFile.entries();
				while (zipEntries.hasMoreElements()) {
					final ZipEntry zipEntry = zipEntries.nextElement();
					final String zipEntryPath = zipEntry.getName();
					final File targetFile = new File(directory, zipEntryPath);
					targetFile.getParentFile().mkdirs();
					if (zipEntry.isDirectory()) {
						continue;
					}
					final InputStream zipEntryIn = zipFile.getInputStream(zipEntry);
					try {
						final OutputStream out = new FileOutputStream(targetFile);
						try {
							copy(zipEntryIn, out);
						} finally {
							out.close();
						}
					} finally {
						zipEntryIn.close();
					}
				}
			} finally {
				zipFile.close();
			}
		} catch (final IOException e) {
			throw new RuntimeException("Failed to unzip file:", e);
		}
	}

	/**
	 * Packs the content of the specified source folder into the specified
	 * target ZIP file.
	 * 
	 * @param source
	 *            the source folder holding the content
	 * @param target
	 *            the ZIP file to pack the content into
	 * @param fileFilter
	 *            the file filter used to filter the files that will be added;
	 *            may be <code>null</code>
	 */
	public static void zip(final File source, final File target, final FileFilter fileFilter) {
		pack(source, target, fileFilter);
	}

	/**
	 * Packs the content of the specified source folder into the specified
	 * target ZIP file.
	 * 
	 * @param source
	 *            the source folder holding the content
	 * @param target
	 *            the ZIP file to pack the content into
	 */
	public static void zip(final File source, final File target) {
		zip(source, target, null);
	}

	/**
	 * Packs the content of the specified source folder into the specified
	 * target ZIP file.
	 * 
	 * @param source
	 *            the source folder holding the content
	 * @param target
	 *            the ZIP file to pack the content into
	 */
	public static void zip(final String source, final String target) {
		File srcFile = new File(source);
		File targetFile = new File(target);
		zip(srcFile, targetFile);
	}

	/**
	 * Moves a file.
	 * 
	 * @param sourceFile
	 *            source file path
	 * @param destination
	 *            destination file path
	 * @throws IOException
	 *             if moving fails
	 */
	public static void moveFileTo(final String sourceFile, final String destination) throws IOException {
		File srcFile = new File(sourceFile);
		File targetDir = new File(destination);
		if (!targetDir.isDirectory()) {
			throw new IOException("Cannot move file " + sourceFile + " to directory " + destination + "! "
					+ destination + " is not a directory.");
		}
		if (!srcFile.isFile()) {
			throw new IOException("Cannot move file " + sourceFile + " to directory " + destination + "! " + srcFile
					+ " is not a file.");
		}

		if (!srcFile.renameTo(new File(targetDir.getAbsolutePath() + "/" + srcFile.getName()))) {
			throw new IOException("Failed moving file " + sourceFile + " to directory " + destination + "!");
		}
	}

	/**
	 * Moves source file to destination file.
	 * 
	 * @param sourceFile
	 *            file to move
	 * @param destinationFile
	 *            destination file to move to
	 * @throws IOException
	 *             if moving fails
	 */
	public static void moveFile(final String sourceFile, final String destinationFile) throws IOException {
		File srcFile = new File(sourceFile);
		File targetFile = new File(destinationFile);
		if (targetFile.exists()) {
			targetFile.delete();
		}
		if (!srcFile.renameTo(targetFile)) {
			throw new IOException("Failed moving file " + sourceFile + " to file " + destinationFile + "!");
		}

	}

	/**
	 * Packs the content of the specified source folder into the specified
	 * target file.
	 * 
	 * @param source
	 *            the source folder holding the content
	 * @param target
	 *            the file to pack the content into
	 * @param fileFilter
	 *            the file filter used to filter the files that will be added;
	 *            may be <code>null</code>
	 */
	private static void pack(final File source, final File target, final FileFilter fileFilter) {
		if (target.exists() && target.isDirectory()) {
			throw new RuntimeException("Target for zip must not be a directory but a file!");
		}
		final ZipOutputStream zipOut;
		try {

			zipOut = new ZipOutputStream(new FileOutputStream(target));

			pack(source, zipOut, source.getAbsolutePath().length() + 1, fileFilter);
			zipOut.close();
		} catch (final FileNotFoundException e) {
			throw new RuntimeException(e);
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void pack(final File file, final ZipOutputStream out, final int pathStartIndex,
			final FileFilter fileFilter) {
		if (fileFilter != null && !fileFilter.accept(file)) {
			/*
			 * Ignore file/directory.
			 */
			return;
		}

		final String filePath = file.getAbsolutePath();
		String entryName = "";
		if (filePath.length() > pathStartIndex) {
			entryName = filePath.substring(pathStartIndex).replace('\\', '/');
		}
		if (file.isDirectory()) {
			if (!entryName.isEmpty()) {
				try {
					/*
					 * Directory entries are determined as such by the trailing
					 * '/'.
					 */
					final ZipEntry zipEntry;
					final boolean packAsJar = out instanceof JarOutputStream;
					if (packAsJar) {
						zipEntry = new JarEntry(entryName + "/");
					} else {
						zipEntry = new ZipEntry(entryName + "/");
					}
					out.putNextEntry(zipEntry);
					out.closeEntry();
				} catch (final IOException e) {
					throw new RuntimeException("Failed to write folder entry to zip file: ", e);
				}
			}
			for (final File directoryItem : file.listFiles()) {
				pack(directoryItem, out, pathStartIndex, fileFilter);
			}
		} else {
			/*
			 * Manifest was added earlier (see according comment above).
			 */
			if (!MANIFEST_PATH.equals(entryName)) {
				packFile(file, out, entryName);
			}
		}
	}

	private static void packFile(final File file, final ZipOutputStream out, final String entryName) {
		FileInputStream fileIn;
		try {
			fileIn = new FileInputStream(file);
		} catch (final FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		try {
			try {
				final ZipEntry zipEntry;
				final boolean packAsJar = out instanceof JarOutputStream;
				if (packAsJar) {
					zipEntry = new JarEntry(entryName);
				} else {
					zipEntry = new ZipEntry(entryName);
				}
				out.putNextEntry(zipEntry);
				try {
					copy(fileIn, out);
				} finally {
					out.closeEntry();
				}
			} finally {
				fileIn.close();
			}
		} catch (final IOException e) {
			throw new RuntimeException("Failed to write file content to zip file: ", e);
		}
	}

	/**
	 * Creates a directory for the passed path. Creates also all parent
	 * directories if these don't exist.
	 * 
	 * @param targetDir
	 *            directory to create
	 * @return true if successful
	 */
	public static boolean createDir(String targetDir) {
		File dir = new File(targetDir);
		if (!dir.exists()) {
			return dir.mkdirs();
		} else if (dir.isDirectory()) {
			return true;
		}
		return false;
	}

}
