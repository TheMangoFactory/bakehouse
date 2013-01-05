package com.mangofactory.bakehouse.core.tags;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import lombok.Getter;
import lombok.Setter;

public class ResourceTag extends SimpleTagSupport {

	@Getter @Setter
	private String src;
	@Override
	public void doTag() throws JspException, IOException {
		ResourcesTag parent = (ResourcesTag) getParent();
		parent.addChild(this);
	}
}
