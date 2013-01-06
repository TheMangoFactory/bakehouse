package com.mangofactory.bakehouse.less;

import com.mangofactory.bakehouse.core.CssResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.processors.AbstractCompilingProcessor;

public class LessCssProcessor extends AbstractCompilingProcessor {

	public LessCssProcessor(String targetFilename)
	{
		this(targetFilename, new LessCssCompiler());
	}
	public LessCssProcessor(String targetFilename, LessCssCompiler compiler)
	{
		super(targetFilename,compiler);
	}
	
	@Override
	protected Resource getServletRelativeResource(Resource compiledResource,
			String servletRelativePath) {
		return new CssResource(servletRelativePath);
	}
}
