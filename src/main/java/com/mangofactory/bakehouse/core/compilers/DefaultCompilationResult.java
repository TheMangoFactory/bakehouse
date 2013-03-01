package com.mangofactory.bakehouse.core.compilers;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.google.common.collect.Lists;
import com.mangofactory.bakehouse.core.CompilationFailedResource;
import com.mangofactory.bakehouse.core.Resource;

@RequiredArgsConstructor(access=AccessLevel.PRIVATE)
public class DefaultCompilationResult implements CompilationResult {

	@Getter
	private final Resource compiledResource;
	@Getter
	private final String messages;
	@Getter
	private final List<CompilationProblem> problems;
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
		return new DefaultCompilationResult(resource, messages, null, true);
	}
	public static CompilationResult failedResult(Resource originalResource, Exception e)
	{
		CompilationProblem problem = new CompilationProblem(-1, -1, "An exception occurred: \n" + e.getMessage());
		return failedResult(originalResource,problem);
	}
	public static CompilationResult failedResult(Resource originalResource,
			CompilationProblem problem) {
		return failedResult(originalResource, Lists.newArrayList(problem));
	}
	public static CompilationResult failedResult(Resource originalResource,
			List<CompilationProblem> problems) {
		CompilationFailedResource resource = new CompilationFailedResource(originalResource, problems);
		return new DefaultCompilationResult(resource, resource.getErrorMessages(), problems, false);
	}
	
}
