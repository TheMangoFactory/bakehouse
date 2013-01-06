package com.mangofactory.bakehouse.core.compilers;

import java.io.File;

import com.mangofactory.bakehouse.core.Resource;

public interface Compiler {
	CompilationResult compile(Resource resource, File targetFile);
}
