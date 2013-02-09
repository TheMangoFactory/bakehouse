package com.mangofactory.bakehouse.angularjs;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.AstRoot;

public class ControllerDeclarationVisitorTests {

	NgMinifyProcessor processor;
	ControllerDeclarationVisitor visitor;
	@Before
	public void setup()
	{
		processor = new NgMinifyProcessor();
		visitor = new ControllerDeclarationVisitor();
	}
	
	@Test
	public void detectsControllerDeclaration()
	{
		Parser parser = new Parser();
		String input = "myApp.controller('myController', function($scope, $http) {\n" +
				"// some stuff here\n" +
				"});";
		AstRoot astRoot = parser.parse(input, "", 0);
		AstNode next = (AstNode) astRoot.getFirstChild();
		assertTrue(visitor.isControllerDeclaration(next));
		assertTrue(visitor.usesMinifyUnsafeSyntax(next));
	}
	
	@Test
	public void reWritesNodeCorrectly()
	{
		Parser parser = new Parser();
		String input = "myApp.controller('myController', function($scope, $http) {\n" +
				"// some stuff here\n" +
				"});";
		AstRoot astRoot = parser.parse(input, "", 0);
		AstNode node = (AstNode) astRoot.getFirstChild();
		
		visitor.rewrite(node);
		
		String rewritten = node.toSource();
		String expected = "myApp.controller('myController', [\"$scope\", \"$http\", function($scope, $http) {\n" +
							"}]);";
		assertThat(rewritten.trim(),equalTo(expected.trim()));
	}
	
	@Test
	public void rewritesNodeWithNoDependenciesCorrectly()
	{
		Parser parser = new Parser();
		String input = "myApp.controller('myController', function() {\n" +
				"// some stuff here\n" +
				"});";
		AstRoot astRoot = parser.parse(input, "", 0);
		AstNode node = (AstNode) astRoot.getFirstChild();
		
		visitor.rewrite(node);
		
		String rewritten = node.toSource();
		String expected = "myApp.controller('myController', [function() {\n" +
							"}]);";
		assertThat(rewritten.trim(),equalTo(expected.trim()));
	}
	
	@Test
	public void parseExpected()
	{
		Parser parser = new Parser();
		String input = "myApp.controller('myController', ['$scope','$http', function($scope, $http) {\n" +
				"// some stuff here\n" +
				"}]);";
		AstRoot astRoot = parser.parse(input, "", 0);
		AstNode next = (AstNode) astRoot.getFirstChild();
	}
}
