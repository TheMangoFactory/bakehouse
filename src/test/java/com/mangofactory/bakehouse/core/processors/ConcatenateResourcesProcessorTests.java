package com.mangofactory.bakehouse.core.processors;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

import java.io.File;

import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import com.mangofactory.bakehouse.core.AbstractFileManipulationTests;
import com.mangofactory.bakehouse.core.DefaultResource;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.io.FileManager;
import com.mangofactory.bakehouse.core.io.FilePath;

public class ConcatenateResourcesProcessorTests extends AbstractFileManipulationTests {

	private ConcatenateResourcesProcessor processor;

	FileManager fileRepository;
	@Before
	public void setup()
	{
		fileRepository = new FileManager(getGeneratedAssetsFolder());
		processor = new ConcatenateResourcesProcessor("AppCode.js");
		processor.setFileRepository(fileRepository);
	}
	@Test @SneakyThrows
	public void concatenatesFiles()
	{
		File file1 = getTempCopyOf(testResource("testFile1.js"));
		File file2 = getTempCopyOf(testResource("testFile2.js"));
		
		DefaultResource resource = DefaultResource.fromFiles("text/javascript",file1,file2);
		
		Resource processedResource = processor.process(resource);
		
		assertThat(processedResource.getResourcePaths(), hasSize(1));
		FilePath filePath = processedResource.getResourcePaths().get(0);
		assertThat(filePath.getFileName(),equalTo("AppCode.js"));
		
		String fileContents = FileUtils.readFileToString(filePath.getFile());
		assertThat(fileContents,equalTo("// Hello from Test File 1\n// Hello from Test File 2"));
	}
	
	@Test 
	public void htmlPointsToConcatenatedFile()
	{
		File file1 = getTempCopyOf(testResource("testFile1.js"));
		File file2 = getTempCopyOf(testResource("testFile2.js"));
		
		DefaultResource resource = DefaultResource.fromFiles("text/javascript",file1,file2);
		
		Resource processedResource = processor.process(resource);

		String fileName = processedResource.getResourcePaths().get(0).getPath();
		
		String expected = "<script src='" + fileName + "' type='text/javascript'></script>\n";
		assertThat(processedResource.getHtml(),equalTo(expected));
	}
}
