package com.mangofactory.bakehouse.typescript;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.File;

import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.mangofactory.bakehouse.core.AbstractFileManipulationTests;
import com.mangofactory.bakehouse.core.DefaultResource;

public class TypescriptProcessTests extends AbstractFileManipulationTests {

	TypescriptCompiler process;
	@Before
	public void setup()
	{
		process = new TypescriptCompiler("/usr/local/bin/tsc");
	}
	@Test @SneakyThrows
	public void compilesTypescript()
	{
		
		DefaultResource resource = DefaultResource.fromFiles("text/javascript",testResource("typescript.ts"));
		File tempFile = getNewTempFile("js");
		CompilationResult compilationResult = process.compile(resource, tempFile);
		assertTrue("Compilation failed: " + compilationResult.getMessages(), compilationResult.isSuccessful());
		assertThat(compilationResult.getCompiledResource().getFiles().size(), equalTo(1));
		
		assertTrue(tempFile.exists());
		
		String generatedTypescript = FileUtils.readFileToString(tempFile);
		String expectedTypescript = FileUtils.readFileToString(testResource("expectedTypescriptCompilationResult.js"));
		generatedTypescript = normalizeLineEndings(generatedTypescript);
		expectedTypescript = normalizeLineEndings(expectedTypescript);
		
		assertThat(generatedTypescript, equalTo(expectedTypescript));
	}
	private String normalizeLineEndings(String input) {
		input = input.replaceAll("\\r\\n", "\n");
		input = input.replaceAll("\\r", "\n");
		return input;
	}
}
