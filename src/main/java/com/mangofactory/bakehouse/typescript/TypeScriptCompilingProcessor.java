package com.mangofactory.bakehouse.typescript;

import lombok.Setter;

import lombok.Getter;

import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.ResourceProcessor;
import com.mangofactory.bakehouse.core.io.FileRepository;

public class TypeScriptCompilingProcessor implements ResourceProcessor {

	private final String targetFilename;
	private final FileRepository fileRepository;

	public TypeScriptCompilingProcessor(String targetFilename, FileRepository fileRepository, String pathToTsc)
	{
		this(targetFilename, fileRepository, new TypescriptProcess(pathToTsc));
	}
	public TypeScriptCompilingProcessor(String targetFilename, FileRepository fileRepository, TypescriptProcess process)
	{
		this.targetFilename = targetFilename;
		this.fileRepository = fileRepository;
		this.typescriptProcess = process;
	}
	@Getter @Setter
	private TypescriptProcess typescriptProcess;
	
	public Resource process(Resource resource) {
		typescriptProcess.compile(resource, fileRepository.getNewFile(targetFilename));
		return null;
	}

}
