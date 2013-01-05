package com.mangofactory.bakehouse.core;

import java.io.File;
import java.util.List;

public interface Resource {

	String getResourceType();
	String getHtml();
	List<File> getFiles();
}
