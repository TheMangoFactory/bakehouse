package com.mangofactory.bakehouse.core.io;

import java.io.File;
import java.net.URI;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import org.apache.commons.io.FilenameUtils;

import com.google.common.collect.Lists;

@RequiredArgsConstructor
@ToString
public class FilePath {
	
	@Getter
	private final String path;
	private final Boolean serlvetRelative;
	
	public static FilePath forAbsolutePath(String path)
	{
		return new FilePath(path,false);
	}
	public static FilePath forServletPath(String path)
	{
		return new FilePath(path,true);
	}
	public static List<FilePath> fromFiles(Iterable<File> files)
	{
		List<FilePath> result = Lists.newArrayList();
		for (File file : files)
		{
			result.add(forAbsolutePath(file.getAbsolutePath()));
		}
		return result;
	}
	public static FilePath fromFile(File file) {
		return forAbsolutePath(file.getAbsolutePath());
	}
	
	public String getFileName()
	{
		return FilenameUtils.getName(path);
	}
	public Boolean isSerlvetRelative() {
		return serlvetRelative;
	}
	public URI getUri()
	{
		if (isSerlvetRelative())
		{
			throw new IllegalStateException("Cannot generate a URI of a servlet relative filePath.  Transform to absolute using the FileManager");
		}
		return new File(path).toURI();
	}
	public File getFile()
	{
		if (isSerlvetRelative())
		{
			throw new IllegalStateException("Cannot generate a URI of a servlet relative filePath.  Transform to absolute using the FileManager");
		}
		return new File(path);
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result
				+ ((serlvetRelative == null) ? 0 : serlvetRelative.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FilePath other = (FilePath) obj;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (serlvetRelative == null) {
			if (other.serlvetRelative != null)
				return false;
		} else if (!serlvetRelative.equals(other.serlvetRelative))
			return false;
		return true;
	}
}
