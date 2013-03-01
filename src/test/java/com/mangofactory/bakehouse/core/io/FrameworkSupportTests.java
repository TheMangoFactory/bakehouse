package com.mangofactory.bakehouse.core.io;

import org.junit.Test;

import com.mangofactory.bakehouse.core.AbstractFileManipulationTests;

public class FrameworkSupportTests extends AbstractFileManipulationTests {

	@Test
	public void writesFrameworkFiles()
	{
		FrameworkSupport support = new FrameworkSupport();
		FilePath fp = FilePath.fromFile(getGeneratedAssetsFolder());
		support.writeFrameworkFiles(fp);
	}
}
