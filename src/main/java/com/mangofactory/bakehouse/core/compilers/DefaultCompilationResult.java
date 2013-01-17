package com.mangofactory.bakehouse.core.compilers;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.mangofactory.bakehouse.core.CompilationFailedResource;
import com.mangofactory.bakehouse.core.Resource;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public class DefaultCompilationResult implements CompilationResult {

	@Getter
	private final Resource compiledResource;
	@Getter
	private final String messages;
	private final Boolean successful;
	
	public Boolean isSuccessful()
	{
		return successful;
	}

	public static CompilationResult successfulResult(Resource resource)
	{
		return successfulResult(resource,"");
	}
	public static CompilationResult successfulResult(Resource resource, String messages)
	{
		return new DefaultCompilationResult(resource, messages, true);
	}
	
	public static CompilationResult failedResult(Resource originalResource, String errorMessage)
	{
		CompilationFailedResource resource = new CompilationFailedResource(originalResource, errorMessage);
		return new DefaultCompilationResult(resource, errorMessage, false);
	}
	
}
