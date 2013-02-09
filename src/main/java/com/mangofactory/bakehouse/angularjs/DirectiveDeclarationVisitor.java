package com.mangofactory.bakehouse.angularjs;

import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.NodeVisitor;

public class DirectiveDeclarationVisitor  extends AbstractAngularJsVisitor implements NodeVisitor {

	public boolean visit(AstNode node) {
		return true;
	}

}
