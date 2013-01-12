package com.mangofactory.bakehouse.core;

import java.util.List;

import com.mangofactory.bakehouse.core.io.FilePath;

public interface Resource {

	String getResourceType();
	String getHtml();
	List<FilePath> getResourcePaths();
	
	Resource setResourcePaths(List<FilePath> paths);
}
