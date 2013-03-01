package com.mangofactory.bakehouse.core;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.mangofactory.bakehouse.core.compilers.CompilationProblem;

@RunWith(MockitoJUnitRunner.class)
public class CompilationFailedResourceTest {

	@Mock
	Resource originalResource;
	@Mock
	CompilationProblem problem;
	@Before
	public void setup()
	{
	}
	@Test
	public void testGetsTemplate()
	{
		CompilationFailedResource resource = new CompilationFailedResource(originalResource, problem);
		String htmlTemplate = resource.getHtmlTemplate();
		assertThat(htmlTemplate, notNullValue());
	}
}
