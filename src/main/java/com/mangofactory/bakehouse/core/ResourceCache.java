package com.mangofactory.bakehouse.core;

import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

@Component @Slf4j
public class ResourceCache {

	private Map<String, List<ResourceProcessor>> configurationMap = Maps.newHashMap();
	
	public void addConfiguration(String configurationName, ResourceProcessor... processors)
	{
		List<ResourceProcessor> processorList = Lists.newArrayList(processors);
		addConfiguration(configurationName, processorList);
	}
	public void addConfiguration(String configurationName,
			List<ResourceProcessor> processorList) {
		if (configurationMap.containsKey(configurationName))
		{
			configurationMap.get(configurationName).addAll(processorList);
		} else {
			configurationMap.put(configurationName, processorList);
		}
	}
	public Resource getResourceGroup(String configuration, String type, Iterable<String> resourcePaths)
	{
		Resource resource = getDefaultResource(resourcePaths,type);
		if (configurationMap.containsKey(configuration))
		{
			List<ResourceProcessor> processors = configurationMap.get(configuration);
			for (ResourceProcessor resourceProcessor : processors) {
				resource = resourceProcessor.process(resource);
			}
		} else {
			log.warn("No matching configuration defined for '{}'. Using default resource",configuration);
		}
		return resource;
	}
	private Resource getDefaultResource(Iterable<String> resourcePaths, String resourceType) {
		DefaultResource resource = DefaultResource.fromPaths(resourcePaths,resourceType);
		return resource;
	}
	
}
