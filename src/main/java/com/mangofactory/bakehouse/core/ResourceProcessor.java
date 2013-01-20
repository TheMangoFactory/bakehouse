package com.mangofactory.bakehouse.core;

import java.util.List;

import com.mangofactory.bakehouse.config.BakehouseConfig;
import com.mangofactory.bakehouse.core.io.FilePath;

public interface ResourceProcessor {

	public Resource process(Resource resource);
	
	public void configure(BakehouseConfig config);
	
	public List<FilePath> getAdditionalFilesToMonitor();
}
