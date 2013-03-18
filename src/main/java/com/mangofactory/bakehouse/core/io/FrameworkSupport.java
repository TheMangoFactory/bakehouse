package com.mangofactory.bakehouse.core.io;

import java.io.File;
import java.util.List;

import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;

public class FrameworkSupport {

	private final static List<String> ASSETS = Lists.newArrayList(
			"/shBrushJScript.js",
			"/shCore.css",
			"/shCore.js",
			"/jquery-1.8.3.js",
			"/shThemeRDark.css");
	
	@SneakyThrows
	public void writeFrameworkFiles(FilePath frameworkPath) {
		for (String asset : ASSETS)
		{
			String source = IOUtils.toString(getClass().getResourceAsStream(asset));
			String assetFileName = FilenameUtils.getName(asset);
			String destinationFilename = FilenameUtils.concat(frameworkPath.getPath(), assetFileName);
			FileUtils.write(new File(destinationFilename), source);
		}
	}
}
