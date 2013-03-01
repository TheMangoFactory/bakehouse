package com.mangofactory.bakehouse.core;

import java.text.MessageFormat;
import java.util.List;

import lombok.Getter;
import lombok.SneakyThrows;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.mangofactory.bakehouse.core.compilers.CompilationProblem;
import com.mangofactory.bakehouse.core.io.FilePath;

public class CompilationFailedResource implements Resource {

	private final Resource originalResource;
	@Getter
	private final List<CompilationProblem> problems;

	public CompilationFailedResource(Resource originalResource, CompilationProblem problem)
	{
		this(originalResource,Lists.newArrayList(problem));
	}
	public CompilationFailedResource(Resource originalResource, List<CompilationProblem> problems)
	{
		this.originalResource = originalResource;
		this.problems = problems;
	}
	public String getResourceType() {
		return originalResource.getResourceType();
	}

	@SneakyThrows
	String getHtmlTemplate()
	{
		String template = IOUtils.toString(getClass().getResource("compilationFailed.html"));
		return template;
	}
	@SneakyThrows
	public String getHtml() {
		StringBuilder sb = new StringBuilder()
			.append("<p>Compilation failed: <br />")
			.append(getErrorMessages())
			.append("</p>");
		String result = getHtmlTemplate().replace("$CONTENT_GOES_HERE$", sb.toString());
		return result;
	}
	public List<FilePath> getResourcePaths() {
		return originalResource.getResourcePaths();
	}
	public String getErrorMessages()
	{
		return CompilationProblem.getMessage(getProblems());
	}
	public Resource setResourcePaths(List<FilePath> paths) {
		// No-op
		return this;
	}

}
