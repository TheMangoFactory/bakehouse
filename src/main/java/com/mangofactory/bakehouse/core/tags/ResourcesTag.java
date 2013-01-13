package com.mangofactory.bakehouse.core.tags;

import java.util.List;

import javax.servlet.jsp.JspException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.ResourceCache;
import com.mangofactory.bakehouse.core.io.FilePath;

public class ResourcesTag extends SpringAwareTagSupport {

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
		
		Resource resource = resourceCache.getResourceGroup(configuration,type,childrenResources);
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
		String realPath = pageContext.getServletContext().getRealPath(child.getSrc());
		addResourcePath(FilePath.forAbsolutePath(realPath));
	}
	void addResourcePath(FilePath filePath) {
		if (!childrenResources.contains(filePath))
		{
			childrenResources.add(filePath);
		}
	}

}
