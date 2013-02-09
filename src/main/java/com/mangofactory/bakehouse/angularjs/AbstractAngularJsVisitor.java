package com.mangofactory.bakehouse.angularjs;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.ExpressionStatement;
import org.mozilla.javascript.ast.FunctionCall;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.PropertyGet;

public abstract class AbstractAngularJsVisitor {
	ExpressionStatement getExpressionStatement(AstNode node)
	{
		if (!(node instanceof ExpressionStatement))
			return null;
		ExpressionStatement statement = (ExpressionStatement) node;
		return statement;
	}
	
	FunctionCall getFunctionCall(AstNode node)
	{
		ExpressionStatement expressionStatement = getExpressionStatement(node);
		if (expressionStatement == null)
			return null;
		AstNode expression = expressionStatement.getExpression();
		if (!(expression instanceof FunctionCall))
			return null;
		return (FunctionCall) expression;
	}
	
	PropertyGet getPropertyGet(AstNode node)
	{
		FunctionCall call = getFunctionCall(node);
		if (call == null)
			return null;
		AstNode target = call.getTarget();
		if (!(target instanceof PropertyGet))
			return null;
		return (PropertyGet) target;
	}
	
	Name getRightPropertyGetName(AstNode node)
	{
		PropertyGet propertyGet = getPropertyGet(node);
		if (propertyGet == null)
			return null;
		AstNode right = propertyGet.getRight();
		if (!(right instanceof Name))
			return null;
		return (Name) right;
	}
}
