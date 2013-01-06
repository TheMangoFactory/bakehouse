package com.mangofactory.bakehouse.typescript;

import java.io.File;
import java.util.List;
import java.util.Map;

import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.SystemUtils;
import org.springframework.stereotype.Component;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mangofactory.bakehouse.core.CompilationFailedResource;
import com.mangofactory.bakehouse.core.DefaultResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.exec.LogCollectingOutputStream;

/**
 * Encapsulates calling out to the typescript
 * compiler.
 * 
 * @author martypitt
 *
 */
@Slf4j
public class TypescriptCompiler {

	private static final Map<String,String> DEFAULT_UNIX_ENV;
	static {
		DEFAULT_UNIX_ENV = Maps.newHashMap();
		DEFAULT_UNIX_ENV.put("PATH", "/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin");
	}
	private final String pathToTsc;
	@Setter
	private Map<String,String> environment;
	public TypescriptCompiler() {
		pathToTsc = getDefaultTscPath();
	}
	public TypescriptCompiler(String pathToTsc)
	{
		this.pathToTsc = pathToTsc;
		
	}
	@SneakyThrows
	public CompilationResult compile(Resource resource, File targetFile)
	{
		CommandLine commandLine = new CommandLine(pathToTsc);
		commandLine.addArgument("--out");
		commandLine.addArgument(targetFile.getCanonicalPath());
		for (File file : resource.getFiles())
		{
			commandLine.addArgument(file.getCanonicalPath());
		}
		Executor executor = new DefaultExecutor();
		
		LogCollectingOutputStream outputReader = monitorExecutor(executor);
		int exitCode;
		try
		{
			Map<String,String> environment = getEnvironment();
			exitCode = executor.execute(commandLine, environment);
		} catch (ExecuteException e)
		{
			exitCode = e.getExitValue();
		}
		Resource compiledResource = null;
		if (exitCode == 0)
		{
			log.info("Compiling Typescript: ",outputReader.toString());
			log.info("Compiled {} files to {} successfully", resource.getFiles().size(), targetFile.getPath());
			compiledResource = DefaultResource.fromFiles("text/javascript", targetFile);
		} else {
			log.warn("Compiling typescript failed: {} ", outputReader.toString());
			compiledResource = new CompilationFailedResource(resource, outputReader.toString());
		}
		return new ProcessCompilationResult(executor, exitCode, outputReader, compiledResource);
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
	private Map<String, String> getEnvironment() {
		if (environment != null)
		{
			return environment;
		}
		if (SystemUtils.IS_OS_UNIX)
		{
			return DEFAULT_UNIX_ENV;
		}
		if (SystemUtils.IS_OS_WINDOWS)
		{
			throw new NotImplementedException("Need reasonable defaults for a path for a windows OS... can you provide one?");
		}
		throw new IllegalStateException("No environment defined, and no suitalbe default could be found");
	}
	private LogCollectingOutputStream monitorExecutor(Executor executor) {
		LogCollectingOutputStream outputStream = new LogCollectingOutputStream();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		executor.setStreamHandler(streamHandler);
		return outputStream;
	}
}
