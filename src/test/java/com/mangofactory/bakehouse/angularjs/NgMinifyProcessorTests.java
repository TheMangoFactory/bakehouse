package com.mangofactory.bakehouse.angularjs;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.mangofactory.bakehouse.core.AbstractFileManipulationTests;

public class NgMinifyProcessorTests extends AbstractFileManipulationTests {

	NgMinifyProcessor processor;
	@Before
	public void setup()
	{
		processor = new NgMinifyProcessor();
	}
	@Test
	public void reformatsControllerDeclaration()
	{
		String input = "myApp.controller('MyAccountCtrl', function($scope, $http) {\n" +
				"// some stuff here\n" +
				")};";
		assertTrue(processor.matches(input));
	}
	@Test
	public void buildingRegex()
	{
		String input = "myApp.controller('myController', function";
		assertTrue(processor.matches(input));
	}
	
	@Test
	public void usingParser()
	{
		String input = "myApp.controller('MyAccountCtrl', function($scope, $http) {\n" +
				"// some stuff here\n" +
				"});";
		processor.process(input);
	}
	
	@Test
	public void parserDetectsControllerDeclaration()
	{
		String input = "myApp.controller('MyAccountCtrl', function($scope, $http) {\n" +
				"// some stuff here\n" +
				"});";
		processor.process(input);
	}
}
