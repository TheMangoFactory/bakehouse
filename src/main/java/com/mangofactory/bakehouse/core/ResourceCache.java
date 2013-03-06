package com.mangofactory.bakehouse.core;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspPage;

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

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mangofactory.bakehouse.config.BakehouseConfig;
import com.mangofactory.bakehouse.core.io.FileManager;
import com.mangofactory.bakehouse.core.io.FilePath;
import com.mangofactory.bakehouse.core.processors.CompilationFailureReportingProcessor;

@Slf4j
public class ResourceCache {

	private Map<String, List<ResourceProcessor>> configurationMap = Maps.newHashMap();
	// Key: JSP Page, value:  the cache for that page.
	private Map<String, Map<String, CachedResource>> pageCache = Maps.newHashMap();
	private CacheInvalidatingFileListener fileListener;
	private DefaultFileMonitor fileMonitor;
	private FileSystemManager fileSystemManager;
	private final FileManager fileManager;
	private CompilationFailureReportingProcessor failureReportingProcessor = new CompilationFailureReportingProcessor();
	public ResourceCache(FileManager fileManager) throws FileSystemException {
		this(VFS.getManager(), fileManager);
	}
	
	// TODO: Issue #3 - Consolidate FileManager and FileSystemManager 
	public ResourceCache(FileSystemManager fileSystemManager, FileManager fileManager)
	{
		this.fileManager = fileManager;
		fileListener = new CacheInvalidatingFileListener(this);
		fileMonitor = new DefaultFileMonitor(fileListener);
		fileMonitor.start();
		this.fileSystemManager = fileSystemManager;
	}
	public void setConfiguration(String configurationName, List<ResourceProcessor> processors)
	{
		if (configurationMap.containsKey(configurationName))
			configurationMap.remove(configurationName);
		addConfiguration(configurationName, processors);
	}
	public void setConfiguration(String configurationName, ResourceProcessor... processors)
	{
		List<ResourceProcessor> processorList = Lists.newArrayList(processors);
		setConfiguration(configurationName, processorList);
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
	public Resource getResourceGroup(String path, String configuration, String type, List<FilePath> resourcePaths)
	{
		Map<String, CachedResource> cache = null;
		boolean canUseCache = false;
		if (pageCache.containsKey(path))
		{
			cache = pageCache.get(path);
			if (cache.containsKey(configuration))
			{
				 if (cache.get(configuration).hashEquals(resourcePaths.hashCode()))
				 {
					 canUseCache = true;
				 } else {
					 log.info("Cached version of '{}' doesn't match - rebuilding", configuration);
					 canUseCache = false;
				 }
			}
		}
		
		if (canUseCache)
		{
			log.info("Serving resource '{}' from cache",configuration);
			return cache.get(configuration).getResource();
		} else {
			BuildResourceRequest request = new BuildResourceRequest(path, configuration, type, resourcePaths);
			return buildResource(request);
		}
	}
	private Resource buildResource(BuildResourceRequest request) {
		String configuration = request.getConfiguration();
		log.info("Building resource '{}'", configuration);
		List<FilePath> filePathsToMonitor = Lists.newArrayList(request.getResourcePaths());
		Resource resource = getDefaultResource(request.getResourcePaths(),request.getType());
		if (configurationMap.containsKey(configuration))
		{
			List<ResourceProcessor> processors = getProcessors(configuration); 
			for (ResourceProcessor resourceProcessor : processors) {
				resource = resourceProcessor.process(resource);
				filePathsToMonitor.addAll(resourceProcessor.getAdditionalFilesToMonitor());
			}
		} else {
			log.warn("No matching configuration defined for '{}'. Using default resource",configuration);
		}
		
		List<FilePath> servletRelativePaths = fileManager.makeServletRelative(resource.getResourcePaths());
		resource = resource.setResourcePaths(servletRelativePaths);

		if (resource.isCachable())
		{
			log.info("Caching resource '{}'", configuration);
			cache(request,resource);		
		}
		
		watchPaths(filePathsToMonitor,request.getCacheIndex());
		return resource;
	}

	private List<ResourceProcessor> getProcessors(String configuration) {
		List<ResourceProcessor> processors = configurationMap.get(configuration);
		// decorate ... TODO : This, more elegantly.
		processors.add(failureReportingProcessor);
		return processors;
	}

	@SneakyThrows
	private void watchPaths(List<FilePath> resourcePaths, CacheIndex cacheIndex) {
		fileListener.addFiles(cacheIndex, resourcePaths);
		for (FilePath resourcePath : resourcePaths) {
			FileObject fileObject = fileSystemManager.resolveFile(resourcePath.getPath());
			fileMonitor.addFile(fileObject);
			log.info("Now watching {} for changes",resourcePath.getPath());
		}
	}
	private void cache(BuildResourceRequest request, Resource resource) {
		CachedResource cachedResource = new CachedResource(resource.getResourceType(), resource, request);
		if (!pageCache.containsKey(request.getPath()))
		{
			pageCache.put(request.getPath(), new HashMap<String, CachedResource>());
		}
		Map<String,CachedResource> cache = pageCache.get(request.getPath());
		cache.put(request.getConfiguration(), cachedResource);
	}
	private Resource getDefaultResource(List<FilePath> resourcePaths, String resourceType) {
		DefaultResource resource = DefaultResource.fromPaths(resourcePaths,resourceType);
		return resource;
	}
	
	public void invalidate(Collection<CacheIndex> configurations) {
		// Take a copy to avoid a ConcurrentModificationException
		List<CacheIndex> configurationsToChange = Lists.newArrayList(configurations);
		for (CacheIndex cacheIndex : configurationsToChange) {
			Map<String, CachedResource> cache = pageCache.get(cacheIndex.getPath());
			CachedResource cachedResource = cache.remove(cacheIndex.getConfiguration());
			if (cachedResource != null)
			{
				log.info("Cache of '{}' for page '{}' invalidated - rebuilding",cacheIndex.getConfiguration(),cacheIndex.getPath());
				buildResource(cachedResource.request);
			} else {
				log.error("Cannot invalidate configuration '{}' for page '{}' as was not found in the cache",cacheIndex.getConfiguration(),cacheIndex.getPath());
			}
		}
	}
	
	@Data
	class BuildResourceRequest {
		private final String path;
		private final String configuration;
		private final String type;
		private final List<FilePath> resourcePaths;
		private CacheIndex cacheIndex;
		public int getResourceHash()
		{
			return resourcePaths.hashCode();
		}
		
		public CacheIndex getCacheIndex()
		{
			if (cacheIndex == null)
			{
				cacheIndex = new CacheIndex(path, configuration);
			}
			return cacheIndex;
		}
	}
	@Data
	class CachedResource {
		private final String type;
		private final Resource resource;
		private final BuildResourceRequest request;

		public boolean hashEquals(int hashCode) {
			return request.getResourceHash() == hashCode;
		}
	}
	
	@Data
	class CacheIndex {
		private final String path;
		private final String configuration;
	}
	
	class CacheInvalidatingFileListener implements FileListener
	{
		private final ResourceCache cache;
		private final Multimap<FilePath, CacheIndex> filesToConfiguration;
		
		public CacheInvalidatingFileListener(ResourceCache cache)
		{
			this.cache = cache;
			filesToConfiguration = HashMultimap.create();
		}
		public void addFiles(CacheIndex cacheIndex, List<FilePath> files)
		{
			for (FilePath filePath : files) {
				filesToConfiguration.put(filePath, cacheIndex);
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
			FilePath filePath = FilePath.forAbsolutePath(event.getFile().getName().getPath());
			if (filesToConfiguration.containsKey(filePath))
			{
				Collection<CacheIndex> configurations = filesToConfiguration.get(filePath);
				for (CacheIndex configuration : configurations)
				{
					log.info("File '{}' changed - invalidating cache for configuration '{}'",event.getFile().getName().getBaseName(),configuration);
				}
				cache.invalidate(configurations);
			} else {
				log.error("File '{}' changed - but is not associated with any configuration - ignoring", event.getFile().getName().getBaseName());
			}
		}
	}

	public void configureProcessors(BakehouseConfig bakehouseConfig) {
		for (List<ResourceProcessor> processorList : configurationMap.values())
		{
			for (ResourceProcessor resourceProcessor : processorList) {
				resourceProcessor.configure(bakehouseConfig);
			}
		}
		failureReportingProcessor.configure(bakehouseConfig);
	}
}


