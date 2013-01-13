package com.mangofactory.bakehouse.core.tags;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import com.mangofactory.bakehouse.core.io.FilePath;

public class ResourcesTagTests {

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
}
