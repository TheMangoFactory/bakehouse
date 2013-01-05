package com.mangofactory.bakehouse.core.processors;

import java.io.File;

import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.mangofactory.bakehouse.core.DefaultResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.ResourceProcessor;
import com.mangofactory.bakehouse.core.io.FileRepository;

public class ConcatenateResourcesProcessor implements ResourceProcessor {

	private final FileRepository fileRepository;
	private final String targetFilename;
	
	public ConcatenateResourcesProcessor(String targetFilename, FileRepository fileRepository) {
		this.targetFilename = targetFilename;
		this.fileRepository = fileRepository;
	}

	@SneakyThrows
	public Resource process(Resource resource) {
		StringBuilder sb = new StringBuilder();
		for (File file : resource.getFiles())
		{
			if (sb.length() > 0)
			{
				sb.append("\n");
			}
			sb.append(FileUtils.readFileToString(file));
		}
		File newFile = fileRepository.getNewFile(targetFilename);
		FileUtils.write(newFile, sb.toString());
		
		String path = fileRepository.getServletPath(newFile);
		return DefaultResource.fromPaths(resource.getResourceType(),path);
	}

}
