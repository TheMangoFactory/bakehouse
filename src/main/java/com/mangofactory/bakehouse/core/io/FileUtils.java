package com.mangofactory.bakehouse.core.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.collect.Lists;

public class FileUtils {

	/**
	 * Concantenates the contents of multiple filePaths.
	 * Note - the file Paths must be absolute.
	 * 
	 * @param filePaths
	 * @return
	 * @throws IOException
	 */
	public static String concatenateFilePaths(List<FilePath> filePaths) throws IOException
	{
		List<File> files = Lists.newArrayList();
		for (FilePath filePath : filePaths) {
			files.add(filePath.getFile());
		}
		return concatenateFiles(files);
	}
	/**
	 * Concantenates the contents of multiple filePaths.
	 * File paths are resolved to be absolute using the {@link FileManager}.
	 * 
	 * @param filePaths
	 * @param fileManager
	 * @return
	 * @throws IOException
	 */
	public static String concatenateFilePaths(List<FilePath> filePaths, FileManager fileManager) throws IOException
	{
		List<File> files = Lists.newArrayList();
		for (FilePath filePath : filePaths) {
			files.add(fileManager.makeAbsolute(filePath).getFile());
		}
		return concatenateFiles(files);
	}
	public static String concatenateFiles(List<File> files) throws IOException
	{
		StringBuilder sb = new StringBuilder();
		for (File file : files) {
			if (sb.length() > 0)
				sb.append("\n");
			String fileContents = org.apache.commons.io.FileUtils.readFileToString(file);
			sb.append(fileContents);
		}
		return sb.toString();
	}
}
