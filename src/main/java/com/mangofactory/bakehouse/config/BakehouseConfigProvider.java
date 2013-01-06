package com.mangofactory.bakehouse.config;

import org.springframework.context.annotation.Bean;

/**
 * The base entry point for configuration.
 * 
 * Projects should implement a {@link BakehouseConfigProvider}
 * within a @Configuration bean, often scoped to specific
 * runtime profile.
 * 
 * Note - the build method must be annotated with a 
 * {@link Bean} annotation
 * @author martypitt
 *
 */
public interface BakehouseConfigProvider {

	BakehouseConfig build(BakehouseConfigBuilder builder);
}
