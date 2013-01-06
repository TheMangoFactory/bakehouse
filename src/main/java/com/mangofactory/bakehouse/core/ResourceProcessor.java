package com.mangofactory.bakehouse.core;

import com.mangofactory.bakehouse.config.BakehouseConfig;

public interface ResourceProcessor {

	public Resource process(Resource resource);
	
	public void configure(BakehouseConfig config);
}
