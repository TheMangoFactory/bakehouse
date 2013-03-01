package com.mangofactory.bakehouse.core.io;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.mangofactory.bakehouse.core.AbstractFileManipulationTests;

public class ConcatenatedFilesetTests extends AbstractFileManipulationTests {

	@Test
	public void calculatesLineNumbersCorrectly()
	{
		ConcatenatedFileset fileset = new ConcatenatedFileset(resourceFilePath("testFile1.js"),resourceFilePath("testFile2.js"));
		
		assertThat(fileset.getFilePathAtLine(0), equalTo(resourceFilePath("testFile1.js")));
		assertThat(fileset.getFilePathAtLine(1), equalTo(resourceFilePath("testFile2.js")));
	}
	
	@Test @SneakyThrows
	public void concatenatesCorrectly()
	{
		ConcatenatedFileset fileset = new ConcatenatedFileset(resourceFilePath("testFile1.js"),resourceFilePath("testFile2.js"));
		String expected = FileUtils.readFileToString(testResource("expectedConcatenationResult.js"));
		
		assertThat(fileset.getConcatenatedSource(),equalTo(expected));
		
	}
}
