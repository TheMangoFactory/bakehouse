package com.mangofactory.bakehouse.typescript;

import java.io.File;
import java.io.IOException;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.SystemUtils;

import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.compilers.AbstractCompiler;

/**
 * Encapsulates calling out to the typescript
 * compiler.
 * 
 * @author martypitt
 *
 */
@Slf4j
public class TypescriptCompiler extends AbstractCompiler {

	private final String pathToTsc;
	
	public TypescriptCompiler() {
		pathToTsc = getDefaultTscPath();
	}
	public TypescriptCompiler(String pathToTsc)
	{
		this.pathToTsc = pathToTsc;
		
	}
	
	@Override
	protected CommandLine getCompileCommand(Resource resource, File targetFile)
			throws IOException {
		CommandLine commandLine = new CommandLine(pathToTsc);
		commandLine.addArgument("--out");
		commandLine.addArgument(targetFile.getCanonicalPath());
		for (File file : resource.getFiles())
		{
			commandLine.addArgument(file.getCanonicalPath());
		}
		return commandLine;
	}
	private String getDefaultTscPath()
	{
		if (SystemUtils.IS_OS_UNIX)
		{
			return "/usr/local/bin/tsc";
		}
		if (SystemUtils.IS_OS_WINDOWS)
		{
			throw new NotImplementedException("Need reasonable defaults for a path for a windows OS... can you provide one?");
		}
		throw new IllegalStateException("No environment defined, and no suitalbe default could be found");
	}
	@Override
	protected String getDescription() {
		return "Typescript";
	}
	@Override
	protected String getResourceType() {
		return "text/javascript";
	}
	
}
