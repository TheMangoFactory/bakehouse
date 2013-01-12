package com.mangofactory.bakehouse.config;

import java.util.List;

import com.google.common.collect.Lists;
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

	public BakehouseConfigBuilder with(ResourceProcessor... processors)
	{
		List<ResourceProcessor> processorList = Lists.newArrayList(processors);
		builder.setProcessors(configurationName, processorList);
		return builder;
	}
	// Convenience methods
	public BakehouseConfigBuilder serveAsSingleFile(String filename)
	{
		return with(new ConcatenateResourcesProcessor(filename));
	}
}
