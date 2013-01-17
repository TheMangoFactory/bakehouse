package com.mangofactory.bakehouse.typescript;

import java.io.File;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import com.mangofactory.bakehouse.core.DefaultResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.compilers.CompilationResult;
import com.mangofactory.bakehouse.core.compilers.Compiler;
import com.mangofactory.bakehouse.core.compilers.DefaultCompilationResult;
import com.mangofactory.bakehouse.core.io.FileUtils;
import com.mangofactory.typescript.TypescriptCompiler;

/**
 * Encapsulates calling out to the typescript
 * compiler.
 * 
 * @author martypitt
 *
 */
@Slf4j
public class TypescriptCompilerAdapter implements Compiler {

	private final TypescriptCompiler compiler;
	
	public TypescriptCompilerAdapter() {
		this(new TypescriptCompiler());
	}
	public TypescriptCompilerAdapter(TypescriptCompiler compiler)
	{
		this.compiler = compiler;
	}
	@SneakyThrows
	public CompilationResult compile(Resource resource, File targetFile) {
		String typescriptSource = FileUtils.concatenateFilePaths(resource.getResourcePaths());
		try {
			compiler.compile(typescriptSource, targetFile);
			DefaultResource compiledResource = DefaultResource.fromFiles("text/javascript", targetFile);
			return DefaultCompilationResult.successfulResult(compiledResource);
		} catch (Exception e) {
			String errorMessage = e.getMessage();
			return DefaultCompilationResult.failedResult(resource, errorMessage);
		}
	}
}
