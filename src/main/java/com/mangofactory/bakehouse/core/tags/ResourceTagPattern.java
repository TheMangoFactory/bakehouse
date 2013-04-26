package com.mangofactory.bakehouse.core.tags;

import org.apache.commons.io.FilenameUtils;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName="of")
public class ResourceTagPattern {
	private final String pattern;
	
	public boolean isRecursive()
	{
		return pattern.contains("**");
	}
	public String getBasePath() {
		return FilenameUtils.getFullPath(pattern);
	}
	public String getExtension() {
		return FilenameUtils.getExtension(pattern);
	}
}
