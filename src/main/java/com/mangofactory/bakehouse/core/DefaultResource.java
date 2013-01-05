package com.mangofactory.bakehouse.core;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.SneakyThrows;

import com.google.common.collect.Lists;

public class DefaultResource implements Resource {

	private List<File> files;
	private final Iterable<String> resourcePaths;
	@Getter
	private final String resourceType;
	

	public static DefaultResource fromPaths(String resourceType, String... resourcePaths)
	{
		return new DefaultResource(Arrays.asList(resourcePaths), resourceType);
	}
	public static DefaultResource fromPaths(Iterable<String> resourcePaths,String resourceType)
	{
		return new DefaultResource(resourcePaths, resourceType);
	}
	@SneakyThrows
	public static DefaultResource fromFiles(Iterable<File> files, String resourceType) {
		List<String> newResourceNames = Lists.newArrayList();
		for (File file : files)
		{
			newResourceNames.add(file.getCanonicalPath());
		}
		return new DefaultResource(newResourceNames,resourceType);
	}
	public static DefaultResource fromFiles(String resourceType, File... files)  {
		return fromFiles(Arrays.asList(files), resourceType);
	}
	private DefaultResource(Iterable<String> resourcePaths, String resourceType) {
		this.resourcePaths = resourcePaths;
		this.resourceType = resourceType;
	}
	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		for (String resourcePath : resourcePaths)
		{
			sb.append("<script src='")
				.append(resourcePath)
				.append("' type='")
				.append(resourceType)
				.append("'></script>\n");
		}
		return sb.toString();
	}

	public List<File> getFiles() {
		if (files == null)
		{
			files = Lists.newArrayList();
			for (String resourcePath : resourcePaths)
			{
				files.add(new File(resourcePath));
			}
		}
		return files;
	}

}
