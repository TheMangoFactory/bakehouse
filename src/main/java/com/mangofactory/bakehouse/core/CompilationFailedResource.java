package com.mangofactory.bakehouse.core;

import java.io.File;
import java.util.List;

import com.mangofactory.bakehouse.core.io.FilePath;

public class CompilationFailedResource implements Resource {

	private final Resource originalResource;
	private final String errorMessage;

	public CompilationFailedResource(Resource originalResource, String errorMessage)
	{
		this.originalResource = originalResource;
		this.errorMessage = errorMessage;
	}
	public String getResourceType() {
		return originalResource.getResourceType();
	}

	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<!-- Compilation of resouce failed:\n")
		.append(errorMessage).append("\n")
		.append("-->");
		return sb.toString();
	}
	public List<FilePath> getResourcePaths() {
		return originalResource.getResourcePaths();
	}
	public Resource setResourcePaths(List<FilePath> paths) {
		// No-op
		return this;
	}

}
