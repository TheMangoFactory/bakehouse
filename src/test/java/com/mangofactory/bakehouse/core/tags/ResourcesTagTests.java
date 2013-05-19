package com.mangofactory.bakehouse.core.tags;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import lombok.SneakyThrows;

import org.junit.Test;

import com.mangofactory.bakehouse.core.AbstractFileManipulationTests;
import com.mangofactory.bakehouse.core.io.FilePath;
import com.mangofactory.bakehouse.core.tags.ResourcesTag.FileResolver;

public class ResourcesTagTests extends AbstractFileManipulationTests {

	@Test
	public void cannotAddDuplicateFilePaths()
	{
		ResourcesTag tag = new ResourcesTag();
		tag.addResourcePath(FilePath.forAbsolutePath("/a/b/c"));
		tag.addResourcePath(FilePath.forAbsolutePath("/a/b/c"));
		assertThat(tag.getChildrenResources().size(),equalTo(1));
	}
	
	@Test
	public void resourcesAreReturnedInOrderAdded()
	{
		ResourcesTag tag = new ResourcesTag();
		tag.addResourcePath(FilePath.forAbsolutePath("/a/b/c"));
		tag.addResourcePath(FilePath.forAbsolutePath("/d/e/f"));
		assertThat(tag.getChildrenResources().size(),equalTo(2));
		
		assertThat(tag.getChildrenResources().get(0).getPath(),equalTo("/a/b/c"));
		assertThat(tag.getChildrenResources().get(1).getPath(),equalTo("/d/e/f"));
	}
	
	@Test
	public void canAddFilesByWildcard()
	{
		ResourcesTag tag = new ResourcesTag();
		tag.setFileResolver(new TestFileResolver());
		
		tag.addChild(ResourceTag.forPattern("*.less"));
		assertThat(tag.getChildrenResources().size(),equalTo(4));
		assertTrue(tag.getChildrenResources().contains(FilePath.fromFile(testResource("import-test-a.less"))));
		assertTrue(tag.getChildrenResources().contains(FilePath.fromFile(testResource("import-test-b.less"))));
		assertTrue(tag.getChildrenResources().contains(FilePath.fromFile(testResource("import-test-c.less"))));
		assertTrue(tag.getChildrenResources().contains(FilePath.fromFile(testResource("stylesheet.less"))));
		
	}
	@Test
	public void canAddRecursiveFilesByWildcard()
	{
		ResourcesTag tag = new ResourcesTag();
		tag.setFileResolver(new TestFileResolver());
		
		tag.addChild(ResourceTag.forPattern("**.js"));
		assertThat(tag.getChildrenResources().size(),equalTo(5));
		assertTrue(tag.getChildrenResources().contains(FilePath.fromFile(testResource("js/empty.js"))));
		assertTrue(tag.getChildrenResources().contains(FilePath.fromFile(testResource("testFile1.js"))));
	}

	class TestFileResolver implements FileResolver {
		@SneakyThrows
		public String getRealPath(String path) {
			return testResource(path).getCanonicalPath();
		}
		
	}
}
