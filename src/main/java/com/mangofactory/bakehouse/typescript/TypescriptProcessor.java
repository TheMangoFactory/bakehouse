package com.mangofactory.bakehouse.typescript;

import java.util.Collections;
import java.util.List;

import com.mangofactory.bakehouse.core.ResourceProcessor;
import com.mangofactory.bakehouse.core.io.FilePath;
import com.mangofactory.bakehouse.core.processors.AbstractCompilingProcessor;
import com.mangofactory.typescript.EcmaScriptVersion;

public class TypescriptProcessor extends AbstractCompilingProcessor<TypescriptCompilerAdapter> implements ResourceProcessor {

	public TypescriptProcessor(String targetFilename, EcmaScriptVersion esVersion)
	{
		this(targetFilename);
		getCompiler().setEcmaScriptVersion(esVersion);
		
	}
	public TypescriptProcessor(String targetFilename)
	{
		this(targetFilename,new TypescriptCompilerAdapter());
	}
	public TypescriptProcessor(String targetFilename, TypescriptCompilerAdapter compiler)
	{
		super(targetFilename,compiler);
	}
	public List<FilePath> getAdditionalFilesToMonitor() {
		return Collections.emptyList();
	}
}
