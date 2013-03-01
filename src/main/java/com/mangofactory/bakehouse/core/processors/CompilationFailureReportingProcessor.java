package com.mangofactory.bakehouse.core.processors;

import java.io.File;
import java.io.StringWriter;
import java.util.Collections;
import java.util.List;

import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;

import com.googlecode.jatl.Html;
import com.mangofactory.bakehouse.config.BakehouseConfig;
import com.mangofactory.bakehouse.core.CompilationFailedResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.ResourceProcessor;
import com.mangofactory.bakehouse.core.compilers.CompilationProblem;
import com.mangofactory.bakehouse.core.io.FileManager;
import com.mangofactory.bakehouse.core.io.FilePath;

public class CompilationFailureReportingProcessor implements ResourceProcessor {

	private FileManager fileManager;

	public Resource process(Resource resource) {
		if (!(resource instanceof CompilationFailedResource))
			return resource;
		
		CompilationFailedResource failure = (CompilationFailedResource) resource;

		File failureReport = writeFailureReport(failure);
		FilePath servletPath = fileManager.makeServletRelative(FilePath.fromFile(failureReport));
		FilePath frameworkSupportPath = fileManager.makeServletRelative(fileManager.writeFrameworkSupportClasses());
		CompilationFailureReportingResource reportingResource = new CompilationFailureReportingResource(failure, servletPath, frameworkSupportPath);
		return reportingResource;
	}

	@SneakyThrows
	private File writeFailureReport(CompilationFailedResource failure) {
		StringWriter sw = new StringWriter();
		Html html = new Html(sw)
			.div().style("position: fixed; top: 0; left: 0; z-index: 999;  width: 100%;  height: 23px;")
				.h1().text("Compilation failed").end();
		
		for (CompilationProblem problem : failure.getProblems())
		{
			String message = problem.getMessage() + "\n" + problem.getLocationDescription();
			html.pre().text(message).end()
				.script().type("syntaxhighlighter").classAttr("brush: js; first-line: " + getStartLine(problem) + "; highlight: " + problem.getLine())
					.raw(getCDataBlock(problem))
				.end();
		}
		
		html.endAll();
		
		
		File file = fileManager.getNewFile("CompilationFailureReport.html");
		FileUtils.write(file, sw.toString());
		
		return file;
	}

	private String getCDataBlock(CompilationProblem problem) {
		StringBuilder sb = new StringBuilder()
			.append("<![CDATA[\n");
		int startLine = Math.max(0, getStartLine(problem));
		int endLine = Math.min(problem.getLineCount(), problem.getLine() + 3);

		sb.append(problem.getSource(startLine, endLine));
		sb.append("\n")
		.append("]]>");
		
		return sb.toString();
	}

	private int getStartLine(CompilationProblem problem) {
		return problem.getLine() - 3;
	}

	public void configure(BakehouseConfig config) {
		fileManager = config.getFileManager();
	}

	public List<FilePath> getAdditionalFilesToMonitor() {
		return Collections.emptyList();
	}

}
