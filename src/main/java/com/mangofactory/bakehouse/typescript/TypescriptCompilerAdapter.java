package com.mangofactory.bakehouse.typescript;

import java.io.File;
import java.util.List;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import com.google.common.collect.Lists;
import com.mangofactory.bakehouse.core.DefaultResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.compilers.CompilationProblem;
import com.mangofactory.bakehouse.core.compilers.CompilationResult;
import com.mangofactory.bakehouse.core.compilers.Compiler;
import com.mangofactory.bakehouse.core.compilers.DefaultCompilationResult;
import com.mangofactory.bakehouse.core.io.ConcatenatedFileset;
import com.mangofactory.bakehouse.core.io.FileUtils;
import com.mangofactory.typescript.CompilationContext;
import com.mangofactory.typescript.CompilationContextRegistry;
import com.mangofactory.typescript.EcmaScriptVersion;
import com.mangofactory.typescript.TypescriptCompilationProblem;
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
	
	public EcmaScriptVersion getEcmaScriptVersion()
	{
		return compiler.getEcmaScriptVersion();
	}
	public void setEcmaScriptVersion(EcmaScriptVersion version)
	{
		compiler.setEcmaScriptVersion(version);
	}
	@SneakyThrows
	public CompilationResult compile(Resource resource, File targetFile) {
		ConcatenatedFileset fileset = new ConcatenatedFileset(resource.getResourcePaths());
		String typescriptSource = fileset.getConcatenatedSource();
		// TODO : This isn't right, it assumes that all files are sitting
		// in the same directory.
		File rootFile = resource.getResourcePaths().get(0).getFile();
		File rootDirectory = rootFile.getParentFile();
		CompilationContext compilationContext = CompilationContextRegistry.getNew(rootDirectory);
		try {
			compilationContext.setThrowExceptionOnCompilationFailure(false);
			compiler.compile(typescriptSource, targetFile, compilationContext);
			
			if (compilationContext.hasProblems())
			{
				List<CompilationProblem> problems = getCompilationProblems(compilationContext.getProblems(),fileset);
				return DefaultCompilationResult.failedResult(resource, problems);
			} else {
				DefaultResource compiledResource = DefaultResource.fromFiles("text/javascript", targetFile);
				return DefaultCompilationResult.successfulResult(compiledResource);
			}
		} catch (Exception e) {
			return DefaultCompilationResult.failedResult(resource, e);
		} finally {
			CompilationContextRegistry.destroy(compilationContext);
		}
	}
	private List<CompilationProblem> getCompilationProblems(
			List<TypescriptCompilationProblem> problems, ConcatenatedFileset fileset) {
		List<CompilationProblem> result = Lists.newArrayList();
		for (TypescriptCompilationProblem typescriptProblem : problems)
		{
			
			CompilationProblem problem = new CompilationProblem(
					typescriptProblem.getLine(), 
					typescriptProblem.getColumn(),
					typescriptProblem.getMessage());
			problem.setFilePath(fileset.getFilePathAtLine(typescriptProblem.getLine()));
			problem.setSource(fileset.getSourceForFileAtLine(typescriptProblem.getLine()));
			
			result.add(problem);
		}
		return result;
	}
}
