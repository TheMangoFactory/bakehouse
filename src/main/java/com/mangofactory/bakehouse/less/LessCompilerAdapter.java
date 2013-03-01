package com.mangofactory.bakehouse.less;

import java.io.File;

import lombok.SneakyThrows;

import org.lesscss.LessCompiler;

import com.mangofactory.bakehouse.core.CssResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.compilers.CompilationResult;
import com.mangofactory.bakehouse.core.compilers.Compiler;
import com.mangofactory.bakehouse.core.compilers.DefaultCompilationResult;
import com.mangofactory.bakehouse.core.io.FilePath;

public class LessCompilerAdapter implements Compiler {

	private final LessCompiler compiler;
	public LessCompilerAdapter(LessCompiler compiler)
	{
		this.compiler = compiler;
	}
	@SneakyThrows
	public CompilationResult compile(Resource resource, File targetFile) {
		FilePath sourcePath = getAbsoluteSourcePath(resource);
		try {
			compiler.compile(sourcePath.getFile(), targetFile);
			CssResource cssResource = new CssResource(FilePath.fromFile(targetFile));
			return DefaultCompilationResult.successfulResult(cssResource);
		} catch (Exception e) {
			return DefaultCompilationResult.failedResult(resource, e);
		}
	}
	private FilePath getAbsoluteSourcePath(Resource resource) {
		if (resource.getResourcePaths().size() != 1)
		{
			throw new IllegalArgumentException("LessCompiler can only handle a single file at a time");
		}
		FilePath filePath = resource.getResourcePaths().get(0);
		if (filePath.isSerlvetRelative())
		{
			throw new IllegalArgumentException("Expected an absolute file path");
		}
		return filePath;
	}

}
