package com.mangofactory.bakehouse.less;

import java.io.File;
import java.io.IOException;

import lombok.SneakyThrows;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.SystemUtils;

import com.mangofactory.bakehouse.core.CssResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.compilers.AbstractCompiler;
import com.mangofactory.bakehouse.core.exec.LogCollectingOutputStream;

public class LessCssCompiler extends AbstractCompiler {

	private final String pathToLessC;

	public LessCssCompiler() {
		pathToLessC = getDefaultLessCPath();
	}
	public LessCssCompiler(String pathToLessC)
	{
		this.pathToLessC = pathToLessC;

	}
	@Override
	protected CommandLine getCompileCommand(Resource resource, File targetFile)
			throws IOException {
		if (resource.getFiles().size() != 1)
		{
			throw new IllegalStateException("LessCSS Compiler only supports a single input file.  Concatenate the resources toegether first, by chaining a ConcatenateResourceProcessor before this one");
		}
		File inputFile = resource.getFiles().get(0);
		CommandLine commandLine = new CommandLine(pathToLessC);
		commandLine.addArgument(inputFile.getCanonicalPath());
		return commandLine;
	}

	@Override @SneakyThrows
	protected Resource generateSuccessfulResource(
			LogCollectingOutputStream outputReader, File targetFile) {
		FileUtils.write(targetFile, outputReader.toString());
		return new CssResource(targetFile.getPath());
	}
	@Override
	protected String getDescription() {
		return "LessCSS";
	}

	@Override
	protected String getResourceType() {
		return "text/css";
	}

	private String getDefaultLessCPath()
	{
		if (SystemUtils.IS_OS_UNIX)
		{
			return "/usr/local/bin/lessc";
		}
		if (SystemUtils.IS_OS_WINDOWS)
		{
			throw new NotImplementedException("Need reasonable defaults for a path for a windows OS... can you provide one?");
		}
		throw new IllegalStateException("No environment defined, and no suitalbe default could be found");
	}

}
