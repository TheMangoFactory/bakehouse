package com.mangofactory.bakehouse.core;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.SneakyThrows;

import com.google.common.collect.Lists;
import com.mangofactory.bakehouse.core.io.FilePath;

public class DefaultResource implements Resource {

	@Getter
	private final List<FilePath> resourcePaths;
	@Getter
	private final String resourceType;
	

	public static DefaultResource fromPaths(String resourceType, FilePath... resourcePaths)
	{
		return new DefaultResource(Arrays.asList(resourcePaths), resourceType);
	}
	public static DefaultResource fromPaths(List<FilePath> resourcePaths,String resourceType)
	{
		return new DefaultResource(resourcePaths, resourceType);
	}
	@SneakyThrows
	public static DefaultResource fromFiles(Iterable<File> files, String resourceType) {
		List<FilePath> filePaths = FilePath.fromFiles(files);
		return new DefaultResource(filePaths,resourceType);
	}
	public static DefaultResource fromFiles(String resourceType, File... files)  {
		return fromFiles(Arrays.asList(files), resourceType);
	}
	private DefaultResource(List<FilePath> resourcePaths, String resourceType) {
		this.resourcePaths = resourcePaths;
		this.resourceType = resourceType;
	}
	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		for (FilePath resourcePath : getResourcePaths())
		{
			sb.append("<script src='")
				.append(resourcePath.getPath())
				.append("' type='")
				.append(resourceType)
				.append("'></script>\n");
		}
		return sb.toString();
	}
	public Resource setResourcePaths(List<FilePath> paths) {
		return new DefaultResource(paths, resourceType);
	}
}
