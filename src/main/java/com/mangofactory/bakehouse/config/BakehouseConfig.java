package com.mangofactory.bakehouse.config;

import lombok.Getter;

import com.mangofactory.bakehouse.core.ResourceCache;
import com.mangofactory.bakehouse.core.io.FileManager;

public class BakehouseConfig {

	@Getter
	private final FileManager fileManager;
	@Getter
	private final ResourceCache resourceCache;
	@Getter
	private final boolean serveResourcesFromCdn;

	BakehouseConfig(FileManager fileManager,
			ResourceCache resourceCache, boolean serveResourcesFromCdn) {
		this.fileManager = fileManager;
		this.resourceCache = resourceCache;
		this.serveResourcesFromCdn = serveResourcesFromCdn;
		configureProcessors();
	}
	
	private void configureProcessors()
	{
		resourceCache.configureProcessors(this);
	}
}
