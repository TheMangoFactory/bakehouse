package com.mangofactory.bakehouse.config;

import com.mangofactory.bakehouse.core.ResourceProcessor;
import com.mangofactory.bakehouse.core.processors.ConcatenateResourcesProcessor;

public class ProcessorConfigBuilder {

	private final String configurationName;
	private final BakehouseConfigBuilder builder;

	public ProcessorConfigBuilder(String configurationName,
			BakehouseConfigBuilder bakehouseConfigBuilder) {
		this.configurationName = configurationName;
		this.builder = bakehouseConfigBuilder;
	}

	public BakehouseConfigBuilder with(ResourceProcessor processor)
	{
		builder.addProcessor(configurationName,processor);
		return builder;
	}
	// Convenience methods
	public BakehouseConfigBuilder serveAsSingleFile(String filename)
	{
		return with(new ConcatenateResourcesProcessor(filename));
	}
}
