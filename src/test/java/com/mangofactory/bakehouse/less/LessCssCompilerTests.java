package com.mangofactory.bakehouse.less;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;

import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.mangofactory.bakehouse.core.AbstractFileManipulationTests;
import com.mangofactory.bakehouse.core.DefaultResource;
import com.mangofactory.bakehouse.core.compilers.CompilationResult;

public class LessCssCompilerTests extends AbstractFileManipulationTests {

	LessCssCompiler compiler;
	@Before
	public void setup()
	{
		compiler = new LessCssCompiler();
	}
	@Test @SneakyThrows
	public void compilesTypescript()
	{
		
		DefaultResource resource = DefaultResource.fromFiles("text/css",testResource("stylesheet.less"));
		File tempFile = getNewTempFile("css");
		CompilationResult compilationResult = compiler.compile(resource, tempFile);
		assertTrue("Compilation failed: " + compilationResult.getMessages(), compilationResult.isSuccessful());
		assertThat(compilationResult.getCompiledResource().getFiles().size(), equalTo(1));
		
		assertTrue(tempFile.exists());
		
		String generatedCss = FileUtils.readFileToString(tempFile);
		String expectedCss = FileUtils.readFileToString(testResource("expectedLessCompilationResult.css"));
		generatedCss = normalizeLineEndings(generatedCss);
		expectedCss = normalizeLineEndings(expectedCss);
		
		assertThat(generatedCss, equalTo(expectedCss));
	}
	
}
