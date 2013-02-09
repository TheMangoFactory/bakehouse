package com.mangofactory.bakehouse.angularjs;

import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;

public class DirectiveRewriterVisitorTests {

	private DirectiveDeclarationVisitor visitor;
	@Before
	public void setup()
	{
		visitor = new DirectiveDeclarationVisitor();
	}
	@Test
	public void rewritesDirectiveDeclaration()
	{
		String input = "angular.module('components', [])\n"
				+ ".directive('directive1',function($http,Book) { return null; })\n"
				+ ".directive('directive2',function($route) { return null; });";
		
		AstRoot astRoot = new Parser().parse(input, "", 0);
		visitor.visit((AstNode) astRoot.getFirstChild());
		
	}
}
