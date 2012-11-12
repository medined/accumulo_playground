package com.codebits;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.list.TreeList;

public class FindTextInAccumuloSource {

	int javaFileCounter = 1;
	public int getBytesCounter = 0;
	Pattern pattern = Pattern.compile(".*[\\s\\(](.*).getBytes\\(\\)(.*)");
	
	private static String PREFIX = "/home/medined/workspace/accumulo/trunk/";
	static List<String> ignoredFiles = new TreeList();
	static {
		ignoredFiles.add(PREFIX + "server/src/main/java/org/apache/accumulo/server/master/LiveTServerSet.java");
		ignoredFiles.add(PREFIX + "server/src/main/java/org/apache/accumulo/server/tabletserver/Tablet.java");
		ignoredFiles.add(PREFIX + "server/src/main/java/org/apache/accumulo/server/util/VerifyTabletAssignments.java");
		ignoredFiles.add(PREFIX + "server/src/main/java/org/apache/accumulo/server/monitor/servlets/TServersServlet.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/client/mapreduce/InputFormatBase.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/client/admin/FindMax.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/client/impl/TabletLocatorImpl.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/file/rfile/bcfile/Utils.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/file/rfile/bcfile/TFile.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/file/rfile/bcfile/BCFile.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/file/rfile/bcfile/ByteArray.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/file/BloomFilterLayer.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/util/shell/commands/GetSplitsCommand.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/util/TextUtil.java");
		ignoredFiles.add(PREFIX + "src/main/java/org/apache/accumulo/core/util/format/DefaultFormatter.java");
		ignoredFiles.add(PREFIX + "src/main/java/org/apache/accumulo/core/util/format/BinaryFormatter.java");
		ignoredFiles.add(PREFIX + "src/main/java/org/apache/accumulo/core/util/LocalityGroupUtil.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/data/Mutation.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/data/ComparableBytes.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/data/Range.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/data/KeyExtent.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/data/Key.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/iterators/conf/ColumnUtil.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/iterators/conf/ColumnSet.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/iterators/user/IntersectingIterator.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/util/format/DefaultFormatter.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/util/format/BinaryFormatter.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/util/LocalityGroupUtil.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/iterators/user/IndexedDocIterator.java");
		ignoredFiles.add(PREFIX + "core/src/main/java/org/apache/accumulo/core/iterators/OrIterator.java");
	}
	
	public void doIt(final String path) {
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();

		for (int i = 0; i < listOfFiles.length; i++) {
			final String filename = listOfFiles[i].getName();
			final String filepath = path + "/" + filename;
			if ("test".equals(filename)) {
				// ignore test files
			} else if ("examples".equals(filename)) {
					// ignore example files
			} else if (ignoredFiles.contains(filepath)) {
				// examined file, probably uses Text objects instead of String objects.
			} else if (listOfFiles[i].isDirectory()) {
				doIt(path + "/" + filename);
			} else if (listOfFiles[i].isFile()) {
				if (filename.endsWith("Test.java")) {
					// skipping unit tests.
				} else if (filename.endsWith(".java")) {
					int rv = findGetBytes(filepath);
					if (rv > 0) {
					    getBytesCounter += rv;
						javaFileCounter++;
					}
				}
			}
		}
	}

	public int findGetBytes(final String filename) {
		int found = 0;
		BufferedReader bf = null;
		boolean displayedFilename = false;

		try {
			bf = new BufferedReader(new FileReader(filename));

			// Start a line count and declare a string to hold our current line.
			int linecount = 0;
			String line;

			// Loop through each line, stashing the line into our line variable.
			while ((line = bf.readLine()) != null) {
				// Increment the count and find the index of the word
				linecount++;

				Matcher m = pattern.matcher(line);
				if (m.matches()) {
					if (!displayedFilename) {
						System.out.println(javaFileCounter + ": " + filename);
						displayedFilename = true;
					}
					String group = m.group(1);
					if ("\"\"".equals(group)) {
						// do nothing.
					} else {
						System.out.println("\tLine " + linecount + "; "+ line);
						found++;
					}
				}
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (bf != null) {
				try {
					bf.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
		return found;
	}

	public static void main(String[] args) {
		String path = "/home/medined/workspace/accumulo/trunk";

		FindTextInAccumuloSource driver = new FindTextInAccumuloSource();
		driver.doIt(path);
		
		System.out.println("===========================================");
		System.out.println("getBytesCounter: " + driver.getBytesCounter);
	}

}
