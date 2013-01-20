package com.mangofactory.bakehouse.less;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;
import org.lesscss.LessCompiler;

import com.google.common.collect.Lists;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.io.FilePath;
import com.mangofactory.bakehouse.core.processors.AbstractCompilingProcessor;

@Slf4j
public class LessCssProcessor extends AbstractCompilingProcessor<LessCompilerAdapter> {

	// This is the actual regex:
	// @import\s*["']([.[^"']]*)["'];
	// matches:
	// @import (whitespace) (quote) (one or more non-quote characters) (quote) (semicolon)
	static final String REGEX = "@import\\s*[\"']([.[^\"']]*)[\"'];";
	static final Pattern IMPORT_PATTERN = Pattern.compile(REGEX);
	@Getter
	private List<FilePath> additionalFilesToMonitor;
	public LessCssProcessor(String targetFilename)
	{
		this(targetFilename, new LessCompiler());
	}
	public LessCssProcessor(String targetFilename, LessCompiler compiler)
	{
		super(targetFilename,new LessCompilerAdapter(compiler));
	}
	
	@Override
	public Resource process(Resource resource) {
		return processMultipleFiles(resource);
	}
	private Resource processMultipleFiles(Resource resource) {
		Resource finalResource = null;
		additionalFilesToMonitor = Lists.newArrayList();
		for (FilePath filePath : resource.getResourcePaths())
		{
			Resource resourceToCompile = resource.setResourcePaths(Lists.newArrayList(filePath));
			Resource compiledResource = super.process(resourceToCompile);
			if (finalResource == null)
			{
				finalResource = compiledResource;
			} else {
				finalResource = appendFilePaths(finalResource, compiledResource);
			}
			additionalFilesToMonitor.addAll(scanForImports(filePath));
		}
		return finalResource;
	}
	private Resource appendFilePaths(Resource source,
			Resource resourceToAppendFrom) {
		List<FilePath> compiledFilePaths = Lists.newArrayList(source.getResourcePaths());
		compiledFilePaths.addAll(resourceToAppendFrom.getResourcePaths());
		source = source.setResourcePaths(compiledFilePaths);
		return source;
	}
	
	List<FilePath> scanForImports(List<FilePath> filePaths) {
		List<FilePath> result = Lists.newArrayList();
		for (FilePath filePath : filePaths) {
			result.addAll(scanForImports(filePath));
		}
		return result;
	}
	@SneakyThrows
	List<FilePath> scanForImports(FilePath filePath) {
		List<FilePath> result = Lists.newArrayList();
		File sourceFile = filePath.getFile();
		List<String> lines = FileUtils.readLines(sourceFile);
		for (String line : lines) {
			Matcher m = IMPORT_PATTERN.matcher(line);
			if (m.matches())
			{
				String importedFileName = m.group(1);
				File importedFilePath = new File(sourceFile.toURI().resolve(new URI(importedFileName)));
				if (importedFilePath.exists())
				{
					result.add(FilePath.fromFile(importedFilePath));
				} else {
					log.warn("File '{}' declares an import of '{}' - but the resolved file of '{}' does not exist.",new Object[] {sourceFile.getName(), importedFileName, importedFilePath.getCanonicalPath()});
				}
			}
		}
		// Recurse through the list
		result.addAll(scanForImports(result));
		return result;
	}
}
