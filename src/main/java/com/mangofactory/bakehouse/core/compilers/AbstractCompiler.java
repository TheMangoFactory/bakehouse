package com.mangofactory.bakehouse.core.compilers;

import java.io.File;
import java.io.IOException;
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

import com.google.common.collect.Maps;
import com.mangofactory.bakehouse.core.CompilationFailedResource;
import com.mangofactory.bakehouse.core.DefaultResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.exec.LogCollectingOutputStream;
import com.mangofactory.bakehouse.core.io.FilePath;

@Slf4j
public abstract class AbstractCompiler implements Compiler {

	protected static final Map<String,String> DEFAULT_UNIX_ENV;
	static {
		DEFAULT_UNIX_ENV = Maps.newHashMap();
		DEFAULT_UNIX_ENV.put("PATH", "/usr/bin:/bin:/usr/sbin:/sbin:/usr/local/bin");
	}
	
	abstract protected CommandLine getCompileCommand(Resource resource, File targetFile) throws IOException;
	/**
	 * Returns a short description for the type of assets that are being compiled.
	 * Eg. "Typescript" or "LessCSS"
	 * @return
	 */
	abstract protected String getDescription();
	
	/**
	 * Returns the type of compiled asset - eg:
	 * text/javascript.
	 * 
	 */
	abstract protected String getResourceType();
	
	@Setter
	private Map<String,String> environment;
	
	protected Map<String, String> getEnvironment() {
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

	/**
	 * Defines a fallback resource, if it is detected that
	 * compilation cannot continue.
	 * 
	 * If a value is returned, this is used, and compilation
	 * is not attempted.
	 * 
	 * Returns null to indicate that compilation should continue.
	 * 
	 * This is used in environments where compilation cannot continue,
	 * but a reasonable default is available.
	 * 
	 * For example - a production server, where NodeJS is not available
	 * so a node-based compilation cannot continue.  However, a reasonable
	 * default is made available at build-time.
	 * 
	 * @return
	 */
	protected List<FilePath> getFallbackFilePaths(Resource resource) {
		return null;
	}
	
	@SneakyThrows
	public CompilationResult compile(Resource resource, File targetFile)
	{
		if (getFallbackFilePaths(resource) != null)
		{
			log.info("Not compiling {} - using fallback instead",resource.toString());
			CompilationResult compilationResult = DefaultCompilationResult.successfulResult(DefaultResource.fromPaths(getFallbackFilePaths(resource),getResourceType()));
			return compilationResult;
		}
		CommandLine commandLine = getCompileCommand(resource, targetFile);
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
			log.info("Compiling {}: {}",getDescription(), outputReader.toString());
			log.info("Compiled {} files to {} successfully", resource.getResourcePaths().size(), targetFile.getPath());
			
			compiledResource = generateSuccessfulResource(outputReader,targetFile);
		} else {
			log.warn("Compiling {} failed: {} ", getDescription(), outputReader.toString());
			compiledResource = generateFailedResource(resource, outputReader);
		}
		return new ProcessCompilationResult(executor, exitCode, outputReader, compiledResource);
	}
	/**
	 * Generates a resource that will be used in-place to describe the 
	 * failed compilation attempt.
	 * 
	 * The default implementation simply generates a comment indicating that
	 * the failure occurred, and the reason.
	 * Subclasses may override this to provide specialized behaviour.
	 * 
	 * @param resource
	 * @param outputReader
	 * @return
	 */
	protected Resource generateFailedResource(Resource resource,
			LogCollectingOutputStream outputReader) {
		Resource compiledResource;
		CompilationProblem problem = new CompilationProblem(-1,-1,outputReader.toString());
		compiledResource = new CompilationFailedResource(resource, problem);
		return compiledResource;
	}
	
	/**
	 * Generates a resource to use for the succesfully compiled file.
	 * The compiled resouce is available at targetFile.
	 * 
	 * The default implementation returns a {@link DefaultResource} which
	 * generates a <script /> tag for the generated resource.
	 * 
	 * Subclasses may override this to provide specialized behaviour.
	 * @param outputReader 
	 * 
	 * @param targetFile
	 * @return
	 */
	protected Resource generateSuccessfulResource(LogCollectingOutputStream outputReader, File targetFile) {
		Resource compiledResource;
		compiledResource = DefaultResource.fromFiles(getResourceType(), targetFile);
		return compiledResource;
	}
	
	private LogCollectingOutputStream monitorExecutor(Executor executor) {
		LogCollectingOutputStream outputStream = new LogCollectingOutputStream();
		PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream);
		executor.setStreamHandler(streamHandler);
		return outputStream;
	}
}
