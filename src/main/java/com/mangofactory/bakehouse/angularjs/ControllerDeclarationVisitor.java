package com.mangofactory.bakehouse.angularjs;

import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.mozilla.javascript.ast.ArrayLiteral;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.FunctionNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;
import org.mozilla.javascript.ast.StringLiteral;

import com.google.common.collect.Lists;

@Slf4j
public class ControllerDeclarationVisitor extends AbstractAngularJsVisitor implements NodeVisitor {

	public boolean visit(AstNode node) {
		if (isControllerDeclaration(node))
		{
			log.info("Found controller declaration");
			rewrite(node);
			return false;
		}
		return true;
	}
	
	
	
	boolean isControllerDeclaration(AstNode node) {
		Name rightPropertyName = getRightPropertyGetName(node);
		return rightPropertyName != null && rightPropertyName.getIdentifier().equals("controller");
	}

	boolean usesMinifyUnsafeSyntax(AstNode node) {
		FunctionCall functionCall = getFunctionCall(node);
		if (functionCall.getArguments().size() != 2)
			return false;
		
		AstNode firstArgument = functionCall.getArguments().get(0);
		if (!(firstArgument instanceof StringLiteral))
			return false;
		
		AstNode secondArgument = functionCall.getArguments().get(1);
		if (!(secondArgument instanceof FunctionNode))
			return false;
		return true;
	}

	public void rewrite(AstNode node) {
		FunctionCall controllerDeclarationFunction = getFunctionCall(node);

		StringLiteral controllerName = (StringLiteral) controllerDeclarationFunction.getArguments().get(0);
		FunctionNode controllerFunction = (FunctionNode) controllerDeclarationFunction.getArguments().get(1);

		// REset the arguments.
		controllerDeclarationFunction.setArguments(null);
		controllerDeclarationFunction.addArgument(controllerName);
		
		
		List<AstNode> minifySafeElements = Lists.newArrayList();
		List<AstNode> params = controllerFunction.getParams();
		for (AstNode param : params) {
			Name name = (Name) param;
			String identifier = name.getIdentifier();
			StringLiteral sl = new StringLiteral();
			sl.setQuoteCharacter('"');
			sl.setValue(identifier);
			minifySafeElements.add(sl);
		}
		minifySafeElements.add(controllerFunction);
		ArrayLiteral minifySafeParams = new ArrayLiteral();
		minifySafeParams.setElements(minifySafeElements);
		controllerDeclarationFunction.addArgument(minifySafeParams);
	}

}
