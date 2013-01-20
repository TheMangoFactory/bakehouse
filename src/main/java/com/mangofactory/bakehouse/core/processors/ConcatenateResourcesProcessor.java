package com.mangofactory.bakehouse.core.processors;

import java.io.File;
import java.util.Collections;
import java.util.List;

import lombok.AccessLevel;
import lombok.Setter;
import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;

import com.google.common.collect.Lists;
import com.mangofactory.bakehouse.config.BakehouseConfig;
import com.mangofactory.bakehouse.core.DefaultResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.ResourceProcessor;
import com.mangofactory.bakehouse.core.io.FileManager;
import com.mangofactory.bakehouse.core.io.FilePath;

public class ConcatenateResourcesProcessor implements ResourceProcessor {

	@Setter(AccessLevel.PACKAGE)
	private FileManager fileRepository;
	private final String targetFilename;
	
	public ConcatenateResourcesProcessor(String targetFilename) {
		this.targetFilename = targetFilename;
	}
	
	

	@SneakyThrows
	public Resource process(Resource resource) {
		StringBuilder sb = new StringBuilder();
		for (FilePath filePath : resource.getResourcePaths())
		{
			if (sb.length() > 0)
			{
				sb.append("\n");
			}
			filePath = fileRepository.makeAbsolute(filePath);
			sb.append(FileUtils.readFileToString(filePath.getFile()));
		}
		File newFile = fileRepository.getNewFile(targetFilename);
		FileUtils.write(newFile, sb.toString());
		
		return resource.setResourcePaths(Lists.newArrayList(FilePath.fromFile(newFile)));
	}



	public void configure(BakehouseConfig config) {
		this.fileRepository = config.getFileManager();
	}



	public List<FilePath> getAdditionalFilesToMonitor() {
		return Collections.emptyList();
	}

}
