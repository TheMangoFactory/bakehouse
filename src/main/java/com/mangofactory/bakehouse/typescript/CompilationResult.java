package com.mangofactory.bakehouse.typescript;

import com.mangofactory.bakehouse.core.Resource;

public interface CompilationResult {

	Resource getCompiledResource();
	Boolean isSuccessful();
	String getMessages();
}
