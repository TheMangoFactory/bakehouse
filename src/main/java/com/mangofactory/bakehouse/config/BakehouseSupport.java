package com.mangofactory.bakehouse.config;

import java.io.File;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.mangofactory.bakehouse.core.ResourceCache;
import com.mangofactory.bakehouse.core.io.FileManager;

@Configuration
public class BakehouseSupport {

	@Getter @Setter
	private String generatedSourcesFolder = "generated";
	
	@Bean
	public FileManager getFileManager()
	{
		return new FileManager(new File(generatedSourcesFolder));
	}
	
	@Bean @SneakyThrows
	public ResourceCache getResourceCache(FileManager fileManager)
	{
		return new ResourceCache(fileManager);
	}
	
	@Bean
	public BakehouseConfigBuilder getBuilder(ResourceCache resourceCache, FileManager fileManager)
	{
		return new BakehouseConfigBuilder(resourceCache,fileManager);
	}
}
