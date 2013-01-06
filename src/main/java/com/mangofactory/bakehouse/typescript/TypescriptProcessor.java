package com.mangofactory.bakehouse.typescript;

import java.io.File;

import com.mangofactory.bakehouse.core.DefaultResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.ResourceProcessor;
import com.mangofactory.bakehouse.core.io.FileRepository;
import com.sun.corba.se.impl.resolver.FileResolverImpl;

public class TypescriptProcessor implements ResourceProcessor {

	private final String targetFilename;
	private final FileRepository fileRepository;
	private final TypescriptCompiler compiler;
	public TypescriptProcessor(String targetFilename, FileRepository fileRepository)
	{
		this(targetFilename,fileRepository, new TypescriptCompiler());
	}
	public TypescriptProcessor(String targetFilename, FileRepository fileRepository, TypescriptCompiler compiler)
	{
		this.targetFilename = targetFilename;
		this.fileRepository = fileRepository;
		this.compiler = compiler;
		
	}
	public Resource process(Resource resource) {
		CompilationResult compilationResult = compiler.compile(resource, fileRepository.getNewFile(targetFilename));
		Resource compiledResource = compilationResult.getCompiledResource();
		return makeServletRelative(compiledResource);
		
	}
	private Resource makeServletRelative(Resource compiledResource) {
		File compiledFile = compiledResource.getFiles().get(0);
		String path = fileRepository.getServletPath(compiledFile);
		Resource relativeResource = DefaultResource.fromPaths(compiledResource.getResourceType(), path);
		return relativeResource;
	}

}
