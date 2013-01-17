package com.mangofactory.bakehouse.less;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;

import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.lesscss.LessCompiler;

import com.mangofactory.bakehouse.core.AbstractFileManipulationTests;
import com.mangofactory.bakehouse.core.DefaultResource;
import com.mangofactory.bakehouse.core.compilers.CompilationResult;
import com.mangofactory.bakehouse.core.compilers.Compiler;

public class LessCssCompilerTests extends AbstractFileManipulationTests {

	Compiler compiler;
	
	@Before
	public void setup()
	{
		compiler = new LessCompilerAdapter(new LessCompiler());
	}
	@Test @SneakyThrows
	public void compilesSingleFile()
	{
		
		DefaultResource resource = DefaultResource.fromFiles("text/css",testResource("stylesheet.less"));
		File tempFile = getNewTempFile("css");
		CompilationResult compilationResult = compiler.compile(resource, tempFile);
		assertTrue("Compilation failed: " + compilationResult.getMessages(), compilationResult.isSuccessful());
		assertThat(compilationResult.getCompiledResource().getResourcePaths().size(), equalTo(1));
		
		assertTrue(tempFile.exists());
		
		String generatedCss = FileUtils.readFileToString(tempFile);
		String expectedCss = FileUtils.readFileToString(testResource("expectedLessCompilationResult.css"));
		generatedCss = normalizeLineEndings(generatedCss);
		expectedCss = normalizeLineEndings(expectedCss);
		
		assertThat(generatedCss, equalTo(expectedCss));
	}

	@Test @SneakyThrows
	public void compilesBootstrapCss()
	{
		DefaultResource resource = DefaultResource.fromFiles("text/css",testResource("less/bootstrap-main.less"));
		File tempFile = getNewTempFile("css");
		CompilationResult compilationResult = compiler.compile(resource, tempFile);
		assertTrue("Compilation failed: " + compilationResult.getMessages(), compilationResult.isSuccessful());
	}
	
	
}
