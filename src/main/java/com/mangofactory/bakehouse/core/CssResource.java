package com.mangofactory.bakehouse.core;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class CssResource implements Resource {

	private final String resourcePath;
	public CssResource(String resourcePath)
	{
		this.resourcePath = resourcePath;
	}
	public String getResourceType() {
		return "text/css";
	}

	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<link rel='stylesheet' type='text/css' href='")
			.append(resourcePath)
			.append("'></link>\n");
		return sb.toString();
	}

	public List<File> getFiles() {
		return Arrays.asList(new File(resourcePath));
	}

}
