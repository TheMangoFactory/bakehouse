package com.mangofactory.bakehouse.core.tags;

import javax.servlet.jsp.tagext.TagSupport;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

public abstract class SpringAwareTagSupport extends TagSupport {

	protected <T> T getBean(Class<T> beanClass)
	{
		WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
		return applicationContext.getBean(beanClass);
	}
}
