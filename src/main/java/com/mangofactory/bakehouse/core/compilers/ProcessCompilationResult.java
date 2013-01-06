package com.mangofactory.bakehouse.core.compilers;

import org.apache.commons.exec.Executor;
import org.apache.commons.exec.LogOutputStream;

import com.mangofactory.bakehouse.core.Resource;

public class ProcessCompilationResult implements CompilationResult {

	private final Executor executor;
	private final Resource compiledResource;
	private final LogOutputStream output;
	private final int exitValue;

	public ProcessCompilationResult(Executor executor, int exitValue, LogOutputStream output, Resource compiledResource)
	{
		this.executor = executor;
		this.exitValue = exitValue;
		this.output = output;
		this.compiledResource = compiledResource;
	}
	public Resource getCompiledResource() {
		return compiledResource;
	}

	public Boolean isSuccessful() {
		return !executor.isFailure(exitValue);
	}

	public String getMessages() {
		return output.toString();
	}

}
