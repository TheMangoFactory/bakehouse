package com.mangofactory.bakehouse.core.compilers;

import com.mangofactory.bakehouse.core.Resource;

public interface CompilationResult {

	Resource getCompiledResource();
	Boolean isSuccessful();
	String getMessages();
}
