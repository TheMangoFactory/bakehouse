package com.mangofactory.bakehouse.core.tags;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class ResourceTagPatternTests {

	@Test
	public void testRecursive()
	{
		assertTrue(ResourceTagPattern.of("/a/b/c/**.js").isRecursive());
		assertFalse(ResourceTagPattern.of("/a/b/c/*.js").isRecursive());
	}
	@Test
	public void testBasePath()
	{
		assertThat(ResourceTagPattern.of("/a/b/c/**.js").getBasePath(), equalTo("/a/b/c/"));
		assertThat(ResourceTagPattern.of("/**.js").getBasePath(), equalTo("/"));
		assertThat(ResourceTagPattern.of("/a/b/c/*.js").getBasePath(), equalTo("/a/b/c/"));
		assertThat(ResourceTagPattern.of("/*.js").getBasePath(), equalTo("/"));
	}
	@Test
	public void testExtensions()
	{
		assertThat(ResourceTagPattern.of("/a/b/c/**.js").getExtension(),equalTo("js"));
		assertThat(ResourceTagPattern.of("/a/b/c/*.js").getExtension(),equalTo("js"));
	}
}
