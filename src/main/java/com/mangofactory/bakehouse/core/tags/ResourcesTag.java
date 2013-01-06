package com.mangofactory.bakehouse.core.tags;

import java.util.Set;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.google.common.collect.Sets;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.ResourceCache;

@Slf4j @Component
public class ResourcesTag extends TagSupport {

	ResourceCache _resourceCache;
	
	@Getter @Setter 
	private String configuration;
	
	@Getter @Setter
	private String type;
	
	private Set<String> childrenResources = Sets.newHashSet();
	@Override
	public int doStartTag() throws JspException {
		// Reset local variables, as tags can be pooled
		// Instance variables can only be trusted between doStartTag() and doEndTag();
		childrenResources = Sets.newHashSet();
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
			WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
			_resourceCache = applicationContext.getBean(ResourceCache.class);
		}
		return _resourceCache;
	}
	public void addChild(ResourceTag child)
	{
		String realPath = pageContext.getServletContext().getRealPath(child.getSrc());
		childrenResources.add(realPath);
	}

}
