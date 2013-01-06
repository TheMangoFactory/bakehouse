package com.mangofactory.bakehouse.core.processors;

import java.io.File;

import com.mangofactory.bakehouse.config.BakehouseConfig;
import com.mangofactory.bakehouse.core.DefaultResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.ResourceProcessor;
import com.mangofactory.bakehouse.core.compilers.CompilationResult;
import com.mangofactory.bakehouse.core.compilers.Compiler;
import com.mangofactory.bakehouse.core.io.FileManager;

public abstract class AbstractCompilingProcessor implements ResourceProcessor{

	private final Compiler compiler;
	private final String targetFilename;
	
	private FileManager fileManager;

	public AbstractCompilingProcessor(String targetFilename, Compiler compiler)
	{
		this.targetFilename = targetFilename;
		this.compiler = compiler;
	}
	
	public void configure(BakehouseConfig config) {
		this.fileManager = config.getFileManager();
	}
	public Resource process(Resource resource) {
		CompilationResult compilationResult = compiler.compile(resource, fileManager.getNewFile(targetFilename));
		if (compilationResult.isSuccessful())
		{
			Resource compiledResource = compilationResult.getCompiledResource();
			File compiledFile = compiledResource.getFiles().get(0);
			String path = fileManager.getServletPath(compiledFile);
			return getServletRelativeResource(compiledResource,path);	
		} else {
			return compilationResult.getCompiledResource();
		}
	}
	
	/**
	 * Generates a new resource, based off compiledResource that is servletRelative.
	 * 
	 * The servletRelativePath is passed as a param.
	 * 
	 * Generally, compilers return an absolute path on resources, not a servlet-relative path.
	 * This method converts the resource so it is servlet relative.
	 * 
	 * The default implementation generates a {@link DefaultResource}.  Subclasses
	 * may override this behaviour.
	 * 
	 * @param compiledResource
	 * @param servletRelativePath
	 * @return
	 */
	protected Resource getServletRelativeResource(Resource compiledResource, String servletRelativePath) {
		Resource relativeResource = DefaultResource.fromPaths(compiledResource.getResourceType(), servletRelativePath);
		return relativeResource;
	}


}
