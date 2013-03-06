package com.mangofactory.bakehouse.core;

import java.util.List;

import lombok.Getter;

import com.google.common.collect.Lists;
import com.mangofactory.bakehouse.core.io.FilePath;

public class CssResource implements Resource {

	@Getter
	private final List<FilePath> resourcePaths;
	public CssResource(FilePath resourcePath)
	{
		this.resourcePaths = Lists.newArrayList(resourcePath);
	}
	public CssResource(List<FilePath> resourcePaths)
	{
		this.resourcePaths = resourcePaths;
	}
	public String getResourceType() {
		return "text/css";
	}

	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		
		for (FilePath filePath : resourcePaths)
		{
			sb.append("<link rel='stylesheet' type='text/css' href='")
			.append(filePath.getPath())
			.append("'></link>\n");
		}
		return sb.toString();
	}
	public Resource setResourcePaths(List<FilePath> paths) {
		return new CssResource(paths);
	}
	public Boolean isCachable() {
		return true;
	}
}
