package com.mangofactory.bakehouse.angularjs;

import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;

import com.google.common.collect.Lists;
import com.mangofactory.bakehouse.config.BakehouseConfig;
import com.mangofactory.bakehouse.core.Resource;
import com.mangofactory.bakehouse.core.ResourceProcessor;
import com.mangofactory.bakehouse.core.io.FilePath;

@Slf4j
public class NgMinifyProcessor implements ResourceProcessor {

	private static final List<NodeVisitor> DEFAULT_VISITORS = Lists.newArrayList();
	static {
		DEFAULT_VISITORS.add(new ControllerDeclarationVisitor());
	}
	
	String regex = "\\w+\\.controller\\(['\"]\\w*['\"]\\s*[,]\\s*(function)"; //'\\w*',\\s*function\\(.[^)]*\\)\\s*{";
	Pattern pattern = Pattern.compile(regex);
	
	public NgMinifyProcessor()
	{
		this.visitors = DEFAULT_VISITORS;
	}
	public NgMinifyProcessor(List<NodeVisitor> visitors)
	{
		this.visitors = visitors;
	}
	@Getter @Setter
	private List<NodeVisitor> visitors = Lists.newArrayList();
	public Resource process(Resource resource) {
		// TODO Auto-generated method stub
		return null;
	}

	public void configure(BakehouseConfig config) {
		// TODO Auto-generated method stub
		
	}

	public List<FilePath> getAdditionalFilesToMonitor() {
		return Collections.emptyList();
	}
	
	boolean matches(String line)
	{
		Matcher m = pattern.matcher(line);
		return m.matches();
	}
	
	public void process(String source)
	{
		Parser parser = new Parser();
		AstRoot root = parser.parse(source, "", 0);
		
		root.visit(new NodeVisitor() {
			
			public boolean visit(AstNode node) {
				for (NodeVisitor visitor : visitors)
				{
					if (!visitor.visit(node))
						return false;
				}
				return true;
			}
			
		});
	}
	
	
}

