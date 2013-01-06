package com.mangofactory.bakehouse.typescript;

import java.io.File;

import com.mangofactory.bakehouse.config.BakehouseConfig;
import com.mangofactory.bakehouse.core.DefaultResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.ResourceProcessor;
import com.mangofactory.bakehouse.core.io.FileManager;
import com.sun.corba.se.impl.resolver.FileResolverImpl;

public class TypescriptProcessor implements ResourceProcessor {

	private final String targetFilename;
	private FileManager fileManager;
	private final TypescriptCompiler compiler;
	public TypescriptProcessor(String targetFilename)
	{
		this(targetFilename,new TypescriptCompiler());
	}
	public TypescriptProcessor(String targetFilename, TypescriptCompiler compiler)
	{
		this.targetFilename = targetFilename;
		this.compiler = compiler;
		
	}
	public Resource process(Resource resource) {
		CompilationResult compilationResult = compiler.compile(resource, fileManager.getNewFile(targetFilename));
		if (compilationResult.isSuccessful())
		{
			Resource compiledResource = compilationResult.getCompiledResource();
			return makeServletRelative(compiledResource);	
		} else {
			return compilationResult.getCompiledResource();
		}
		
		
	}
	private Resource makeServletRelative(Resource compiledResource) {
		File compiledFile = compiledResource.getFiles().get(0);
		String path = fileManager.getServletPath(compiledFile);
		Resource relativeResource = DefaultResource.fromPaths(compiledResource.getResourceType(), path);
		return relativeResource;
	}
	public void configure(BakehouseConfig config) {
		this.fileManager = config.getFileManager();
	}

}
