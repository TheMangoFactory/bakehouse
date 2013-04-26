package com.mangofactory.bakehouse.core.tags;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;

import org.apache.commons.lang.StringUtils;

import com.mangofactory.bakehouse.config.BakehouseConfig;

@NoArgsConstructor 
public class ResourceTag extends SpringAwareTagSupport {

	private BakehouseConfig _config;
	@Getter @Setter
	private String src;
	
	@Getter @Setter
	private String pattern;
	
	public static ResourceTag forPattern(String pattern)
	{
		ResourceTag tag = new ResourceTag();
		tag.setPattern(pattern);
		return tag;
		
	}
	
	@Getter @Setter
	private String type = "text/javascript";
	
	@Getter @Setter 
	private String cdn;
	
	@Override
	public int doStartTag() throws JspException {
		ResourcesTag parent = (ResourcesTag) getParent();
		if (parent != null)
		{
			parent.addChild(this);	
		} else {
			applyDefaultProcessing();
		}
		return SKIP_BODY;
	}

	private void applyDefaultProcessing() {
		BakehouseConfig config = getBakehouseConfig();
		if (config.isServeResourcesFromCdn() && !StringUtils.isEmpty(cdn))
		{
			writeResourceTag(cdn);
		} else {
			writeResourceTag(src);
		}
	}

	@SneakyThrows
	private void writeResourceTag(String url) {
		pageContext.getOut().write("<script src='" + url + "' type='" + type + "'></script>");
	}

	private BakehouseConfig getBakehouseConfig() {
		if (_config == null)
		{
			_config = getBean(BakehouseConfig.class);
		}
		return _config;
	}
}
