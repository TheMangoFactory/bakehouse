package com.mangofactory.bakehouse.core.io;

import java.io.File;
import java.util.List;

import javax.servlet.ServletContext;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.context.WebApplicationContext;

import com.google.common.collect.Lists;

@Slf4j
public class FileManager implements ServletContextAware {

	private File targetDir;
	private ServletContext servletContext;

	public FileManager()
	{
		this(new File("generated"));
	}
	public FileManager(File targetDir)
	{
		this.targetDir = targetDir;
	}

	/**
	 * Returns an absolute FilePath from FilePath.
	 * 
	 * @param filePath
	 * @return
	 */
	public FilePath makeAbsolute(FilePath filePath)
	{
		if (filePath.isSerlvetRelative())
		{
			String absolutePath = servletContext.getRealPath(filePath.getPath());
			return FilePath.forAbsolutePath(absolutePath);
		} else {
			return filePath;
		}
	}

	public FilePath makeServletRelative(FilePath filePath)
	{
		if (filePath.isSerlvetRelative())
		{
			return filePath;
		} else {
			if (servletContext == null)
			{
				throw new IllegalStateException("Cannot resolve serlvet path, as no ServletContext was found - are you sure you're running within a servlet?");
			}
			String servletBasePath = servletContext.getRealPath("/");
			String path = new File(servletBasePath).toURI().relativize(filePath.getUri()).getPath();
			path = makeContextRelative(path,servletContext);
			return FilePath.forServletPath(path);
		}
	}

	/**
	 * Returns a file that is named derived
	 * from fileName, but is guaranteed not to yet
	 * exist.
	 * @param filePath
	 * @return
	 */
	@SneakyThrows
	public File getNewFile(String fileName)
	{
		String canonicalChild = FilenameUtils.concat(targetDir.getCanonicalPath(), fileName);
		File file = new File(canonicalChild);
		Integer lastSuffix = 0;
		while (file.exists())
		{
			String nextCandidate = FilenameUtils.removeExtension(canonicalChild);
			if (lastSuffix != 0 && nextCandidate.endsWith("_" + lastSuffix))
			{
				String suffix = "_" + lastSuffix;
				nextCandidate = nextCandidate.substring(0, nextCandidate.length() - suffix.length());
			}
			lastSuffix++;
			nextCandidate += "_" + lastSuffix;
			nextCandidate += "." + FilenameUtils.getExtension(canonicalChild);
			canonicalChild = nextCandidate; 
			file = new File(canonicalChild);
		}
		return file;
	}
	/**
	 * Returns a file that is named derived
	 * from fileName, but is guaranteed not to yet
	 * exist.
	 * @param filePath
	 * @return
	 */
	@SneakyThrows
	public File getNewFile(FilePath filePath)
	{
		String fileName = filePath.getFileName();
		return getNewFile(fileName);
	}

	private String makeContextRelative(String path, ServletContext servletContext) {
		String contextPath = servletContext.getContextPath();
		if (contextPath.length() == 0)
		{
			contextPath = "/";
		}
		if (!path.startsWith("/") && !contextPath.endsWith("/"))
		{
			contextPath += "/";
		}
		return contextPath + path;
	}
	public List<FilePath> makeServletRelative(List<FilePath> resourcePaths) {
		List<FilePath> result = Lists.newArrayList();
		for (FilePath filePath : resourcePaths)
		{
			result.add(makeServletRelative(filePath));
		}
		return result;
	}

	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		String realPath =servletContext.getRealPath(targetDir.getPath());
		log.info("Updated target folder for generated content from {} to {}, relative to Servlet root",targetDir.getPath(),realPath);
		targetDir = new File(realPath);
	}
}
