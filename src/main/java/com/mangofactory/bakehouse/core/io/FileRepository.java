package com.mangofactory.bakehouse.core.io;

import java.io.File;

import javax.servlet.ServletContext;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

@Component
@Slf4j
public class FileRepository implements ApplicationContextAware {

	private File targetDir;
	private WebApplicationContext wac;
	
	public FileRepository()
	{
		this(new File("generated"));
	}
	public FileRepository(File targetDir)
	{
		this.targetDir = targetDir;
	}

	/**
	 * Returns a file that is named derived
	 * from fileName, but is guaranteed not to yet
	 * exist.
	 * @param fileName
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
	public void setApplicationContext(ApplicationContext applicationContext)
			throws BeansException {
		if (applicationContext instanceof WebApplicationContext)
		{
			wac = (WebApplicationContext) applicationContext;
			String realPath = wac.getServletContext().getRealPath(targetDir.getPath());
			log.info("Updated target folder for generated content from {} to {}, relative to Servlet root",targetDir.getPath(),realPath);
			targetDir = new File(realPath);
		}
	}
	
	public String getServletPath(File file)
	{
		if (wac == null)
		{
			throw new IllegalStateException("Cannot resolve serlvet path, as no WebApplicationContext was found - are you sure you're running within a servlet?");
		}
		ServletContext servletContext = wac.getServletContext();
		String servletBasePath = servletContext.getRealPath("/");
		String path = new File(servletBasePath).toURI().relativize(file.toURI()).getPath();
		return makeContextRelative(path,servletContext);
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
}
