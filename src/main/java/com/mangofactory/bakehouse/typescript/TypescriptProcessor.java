package com.mangofactory.bakehouse.typescript;

import com.mangofactory.bakehouse.core.ResourceProcessor;
import com.mangofactory.bakehouse.core.processors.AbstractCompilingProcessor;

public class TypescriptProcessor extends AbstractCompilingProcessor implements ResourceProcessor {

	public TypescriptProcessor(String targetFilename)
	{
		this(targetFilename,new TypescriptCompilerAdapter());
	}
	public TypescriptProcessor(String targetFilename, TypescriptCompilerAdapter compiler)
	{
		super(targetFilename,compiler);
	}
}
