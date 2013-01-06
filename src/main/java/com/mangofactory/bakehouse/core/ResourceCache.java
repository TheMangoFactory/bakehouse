package com.mangofactory.bakehouse.core;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.vfs2.FileChangeEvent;
import org.apache.commons.vfs2.FileListener;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemManager;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.impl.DefaultFileMonitor;
import org.apache.commons.vfs2.util.FileObjectUtils;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mangofactory.bakehouse.core.ResourceCache.BuildResourceRequest;

@Component @Slf4j
public class ResourceCache {

	private Map<String, List<ResourceProcessor>> configurationMap = Maps.newHashMap();
	private Map<String, CachedResource> cache = Maps.newHashMap();
	private CacheInvalidatingFileListener fileListener;
	private DefaultFileMonitor fileMonitor;
	private FileSystemManager fileSystemManager;
	
	public ResourceCache() throws FileSystemException {
		this(VFS.getManager());
	}
	public ResourceCache(FileSystemManager fileSystemManager)
	{
		fileListener = new CacheInvalidatingFileListener(this);
		fileMonitor = new DefaultFileMonitor(fileListener);
		fileMonitor.start();
		this.fileSystemManager = fileSystemManager;
	}
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
		if (cache.containsKey(configuration))
		{
			log.info("Serving resource '{}' from cache",configuration);
			return cache.get(configuration).getResource();
		} else {
			BuildResourceRequest request = new BuildResourceRequest(configuration, type, resourcePaths);
			return buildResource(request);
		}
	}
	private Resource buildResource(BuildResourceRequest request) {
		String configuration = request.getConfiguration();
		log.info("Building resource '{}'", configuration);
		Resource resource = getDefaultResource(request.getResourcePaths(),request.getType());
		if (configurationMap.containsKey(configuration))
		{
			List<ResourceProcessor> processors = configurationMap.get(configuration);
			for (ResourceProcessor resourceProcessor : processors) {
				resource = resourceProcessor.process(resource);
			}
		} else {
			log.warn("No matching configuration defined for '{}'. Using default resource",configuration);
		}
		log.info("Caching resource '{}'", configuration);
		cache(request,resource);
		watchPaths(request.getResourcePaths(),configuration);
		return resource;
	}
	@SneakyThrows
	private void watchPaths(Iterable<String> resourcePaths, String configuration) {
		fileListener.addFiles(configuration, resourcePaths);
		for (String resourcePath : resourcePaths) {
			FileObject fileObject = fileSystemManager.resolveFile(resourcePath);
			fileMonitor.addFile(fileObject);
			log.info("Now watching {} for changes",resourcePath);
		}
	}
	private void cache(BuildResourceRequest request, Resource resource) {
		CachedResource cachedResource = new CachedResource(resource.getResourceType(), resource, request);
		cache.put(request.getConfiguration(), cachedResource);
	}
	private Resource getDefaultResource(Iterable<String> resourcePaths, String resourceType) {
		DefaultResource resource = DefaultResource.fromPaths(resourcePaths,resourceType);
		return resource;
	}
	
	public void invalidate(String configuration) {
		CachedResource cachedResource = cache.remove(configuration);
		if (cachedResource != null)
		{
			log.info("Cache of '{}' invalidated - rebuilding",configuration);
			buildResource(cachedResource.request);
		} else {
			log.error("Cannot invalidate configuration '{}' as was not found in the cache",configuration);
		}
	}
	
	@Data
	class BuildResourceRequest {
		private final String configuration;
		private final String type;
		private final Iterable<String> resourcePaths;
	}
	@Data
	class CachedResource {
		private final String type;
		private final Resource resource;
		private final BuildResourceRequest request;
	}
	
	class CacheInvalidatingFileListener implements FileListener
	{
		private final ResourceCache cache;
		private final Map<String, String> filesToConfiguration;
		
		public CacheInvalidatingFileListener(ResourceCache cache)
		{
			this.cache = cache;
			filesToConfiguration = Maps.newHashMap();
		}
		public void addFiles(String configuration, Iterable<String> files)
		{
			for (String filePath : files) {
				filesToConfiguration.put(filePath, configuration);
			}
		}
		public void fileCreated(FileChangeEvent event) throws Exception {
			handleEvent(event); 
		}

		public void fileDeleted(FileChangeEvent event) throws Exception {
			handleEvent(event); 			
		}

		public void fileChanged(FileChangeEvent event) throws Exception {
			handleEvent(event); 			
		}
		private void handleEvent(FileChangeEvent event) {
			String filePath = event.getFile().getName().getPath();
			if (filesToConfiguration.containsKey(filePath))
			{
				String configuration = filesToConfiguration.get(filePath);
				log.info("File '{}' changed - invalidating cache for configuration '{}'",event.getFile().getName().getBaseName(),configuration);
				cache.invalidate(configuration);
			}
		}
	}
}


