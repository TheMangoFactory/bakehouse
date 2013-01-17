package com.mangofactory.bakehouse.less;

import java.util.List;

import org.lesscss.LessCompiler;

import com.google.common.collect.Lists;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.io.FilePath;
import com.mangofactory.bakehouse.core.processors.AbstractCompilingProcessor;

public class LessCssProcessor extends AbstractCompilingProcessor<LessCompilerAdapter> {

	public LessCssProcessor(String targetFilename)
	{
		this(targetFilename, new LessCompiler());
	}
	public LessCssProcessor(String targetFilename, LessCompiler compiler)
	{
		super(targetFilename,new LessCompilerAdapter(compiler));
	}
	
	@Override
	public Resource process(Resource resource) {
		if (resource.getResourcePaths().size() > 1)
		{
			return processMultipleFiles(resource);
		} else {
			return super.process(resource);
		}
	}
	private Resource processMultipleFiles(Resource resource) {
		Resource finalResource = null;
		for (FilePath filePath : resource.getResourcePaths())
		{
			Resource resourceToCompile = resource.setResourcePaths(Lists.newArrayList(filePath));
			Resource compiledResource = super.process(resourceToCompile);
			if (finalResource == null)
			{
				finalResource = compiledResource;
			} else {
				finalResource = appendFilePaths(finalResource, compiledResource);
			}
		}
		return finalResource;
	}
	private Resource appendFilePaths(Resource source,
			Resource resourceToAppendFrom) {
		List<FilePath> compiledFilePaths = Lists.newArrayList(source.getResourcePaths());
		compiledFilePaths.addAll(resourceToAppendFrom.getResourcePaths());
		source = source.setResourcePaths(compiledFilePaths);
		return source;
	}
	
}
