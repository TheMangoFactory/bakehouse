package com.mangofactory.bakehouse.core.processors;

import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.googlecode.jatl.Html;
import com.mangofactory.bakehouse.core.CompilationFailedResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.io.FilePath;

public class CompilationFailureReportingResource implements Resource {

	private final CompilationFailedResource failedResource;
	private final FilePath reportPath;
	private final FilePath frameworkPath;

	public CompilationFailureReportingResource(CompilationFailedResource failedResource, FilePath reportPath, FilePath frameworkPath)
	{
		this.failedResource = failedResource;
		this.reportPath = reportPath;
		this.frameworkPath = frameworkPath;
	}
	
	public String getResourceType() {
		return failedResource.getResourceType();
	}

	public String getHtml() {
		StringWriter sw = new StringWriter();
		new Html(sw)
			.script().src(getPath("shCore.js")).type("text/javascript").end()
			.script().src(getPath("shBrushJScript.js")).type("text/javascript").end()
			.link().href(getPath("shThemeDefault.css")).rel("stylesheet").type("text/css").end()
			.script().src(getPath("jquery-1.8.3.js")).type("text/javascript").end()
			.script().type("text/javascript")
			.raw(getJavascript())
			.endAll();
		return sw.toString();
		
	}

	private String getPath(String path) {
		return FilenameUtils.concat(frameworkPath.getPath(),path);
	}

	private String getJavascript() {
		return new StringBuilder()
			.append("$(function() {\n")
			.append("$('body').load('")
			.append(reportPath.getPath())
			.append("', function() {  SyntaxHighlighter.all(); } );\n")
			.append("});")
			.toString();
	}

	public List<FilePath> getResourcePaths() {
		return failedResource.getResourcePaths();
	}

	public Resource setResourcePaths(List<FilePath> paths) {
		// no-op
		return this;
	}

	public Boolean isCachable() {
		return false;
	}

}
