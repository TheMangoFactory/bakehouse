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

	@Autowired
	ResourceCache resourceCache;
	
	@Getter @Setter 
	private String configuration;
	
	@Getter @Setter
	private String type;
	
	private Set<ResourceTag> children = Sets.newHashSet();
	
	private Set<String> childrenResources = Sets.newHashSet();
	@Override
	public int doStartTag() throws JspException {
		return EVAL_BODY_INCLUDE;
	}
	@Override @SneakyThrows
	public int doEndTag() throws JspException {
		WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
		resourceCache = applicationContext.getBean(ResourceCache.class);
		
		Resource resource = resourceCache.getResourceGroup(configuration,type,childrenResources);
		pageContext.getOut().write(resource.getHtml());
		return super.doEndTag();
	}

	public void addChild(ResourceTag child)
	{
		children.add(child);
		String realPath = pageContext.getServletContext().getRealPath(child.getSrc());
		childrenResources.add(realPath);
	}

}
