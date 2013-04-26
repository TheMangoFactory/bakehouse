package com.mangofactory.bakehouse.core.tags;

import java.io.File;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.ResourceCache;
import com.mangofactory.bakehouse.core.io.FilePath;

public class ResourcesTag extends SpringAwareTagSupport {

	public interface FileResolver {
		String getRealPath(String path);
	}
	
	@Getter @Setter
	private FileResolver fileResolver = new ServletFileResolver();
	ResourceCache _resourceCache;
	
	@Getter @Setter 
	private String configuration;
	
	@Getter @Setter
	private String type;
	
	// Use a list,  instead of a set, as even though the list of
	// resources must be unique (set-like), the order they are 
	// added must also be preserved.
	@Getter(AccessLevel.PACKAGE)
	private List<FilePath> childrenResources = Lists.newArrayList();
	
	@Override
	public int doStartTag() throws JspException {
		// Reset local variables, as tags can be pooled
		// Instance variables can only be trusted between doStartTag() and doEndTag();
		childrenResources = Lists.newArrayList();
		return EVAL_BODY_INCLUDE;
	}
	@Override @SneakyThrows
	public int doEndTag() throws JspException {
		ResourceCache resourceCache = getResourceCache();
		HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
		String requestPath = request.getRequestURI();
		
		Resource resource = resourceCache.getResourceGroup(requestPath,configuration,type,childrenResources);
		pageContext.getOut().write(resource.getHtml());
		return super.doEndTag();
	}

	private ResourceCache getResourceCache() {
		if (_resourceCache == null)
		{
			_resourceCache = getBean(ResourceCache.class);
		}
		return _resourceCache;
	}
	public void addChild(ResourceTag child)
	{
		if (StringUtils.isEmpty(child.getPattern()))
		{
			addChildWithSource(child);
		} else {
			addChildWithPattern(child);
		}
	}
	private void addChildWithPattern(ResourceTag child) {
		ResourceTagPattern pattern = ResourceTagPattern.of(child.getPattern());
		String path = fileResolver.getRealPath(pattern.getBasePath());
		val dir = new File(path);
		val extensions = new String[]{pattern.getExtension()};
		Collection<File> listFiles = FileUtils.listFiles(dir,extensions,pattern.isRecursive());
		
		for (File file : listFiles) {
			addResourcePath(FilePath.fromFile(file));
		}
	}
	private void addChildWithSource(ResourceTag child) {
		String realPath = fileResolver.getRealPath(child.getSrc());
		addResourcePath(FilePath.forAbsolutePath(realPath));
	}
	void addResourcePath(FilePath filePath) {
		if (!childrenResources.contains(filePath))
		{
			childrenResources.add(filePath);
		}
	}

	
	private class ServletFileResolver implements FileResolver
	{
		public String getRealPath(String path) {
			return pageContext.getServletContext().getRealPath(path);
		}
		
	}
}
