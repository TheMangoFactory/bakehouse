package com.mangofactory.bakehouse.less;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;

import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mangofactory.bakehouse.config.BakehouseConfig;
import com.mangofactory.bakehouse.core.AbstractFileManipulationTests;
import com.mangofactory.bakehouse.core.DefaultResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.compilers.CompilationResult;
import com.mangofactory.bakehouse.core.io.FileManager;
import com.mangofactory.bakehouse.core.io.FilePath;

@RunWith(MockitoJUnitRunner.class)
public class LessCssProcessorTests extends AbstractFileManipulationTests {

	LessCssProcessor processor;

	@Mock
	BakehouseConfig config;
	
	@Test @SneakyThrows
	public void compilesMultipleFiles()
	{
		
		// Same as with a single file - lets just compile the test file twice.
		DefaultResource resource = DefaultResource.fromFiles("text/css",testResource("stylesheet.less"),testResource("stylesheet.less"));
		LessCssProcessor processor = getNewProcessor("stylesheet.css");
		Resource compiledResource = processor.process(resource);
		// should have 2 files now.
		assertThat(compiledResource.getResourcePaths().size(), equalTo(2));

		String expectedCss = FileUtils.readFileToString(testResource("expectedLessCompilationResult.css"));
		expectedCss = normalizeLineEndings(expectedCss);
		
		for (FilePath filePath : compiledResource.getResourcePaths())
		{
			String generatedCss = FileUtils.readFileToString(filePath.getFile());
			generatedCss = normalizeLineEndings(generatedCss);
			
			assertThat(generatedCss, equalTo(expectedCss));
		}
	}
	
	private LessCssProcessor getNewProcessor(String targetFilename)
	{
		LessCssProcessor processor = new LessCssProcessor(targetFilename);
		FileManager fileManager = new FileManager(getGeneratedAssetsFolder());
		when(config.getFileManager()).thenReturn(fileManager);
		processor.configure(config);
		return processor;
	}
}
