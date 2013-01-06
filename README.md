# Bakehouse
Cooks up your HTML resources for you.

 * Declare your resources in your jsp
 * Configure pre-processors (eg., Coffeescript compilers, Typescript compilers) suitable for your environment (eg., Dev, Prod)
 * Deploy!
 
Bakehouse:
 * Monitors your files for changes during dev, and recompiles for you
 * Deploys automatically
 * Caches your resources
 
## Example:

1.  Declare your resources in your jsp:

	<%@ taglib prefix="bakehouse" uri="http://www.mangofactory.com/bakehouse" %>
	<head>
	    <bakehouse:resource src="angular.js" cdn="http://ajax.googleapis.com/ajax/libs/angularjs/1.0.3/angular.min.js"/>
	    <bakehouse:resources configuration="javascript" type="text/javascript">
	        <bakehouse:resource src="file1.js"/>
	        <bakehouse:resource src="file2.js"/>
	    </bakehouse:resources>
	    <bakehouse:resources configuration="typescript" type="text/javascript">
	        <bakehouse:resource src="typescript.ts"/>
	    </bakehouse:resources>
	</head>
	 
2.  Define a configuration:

	@Configuration
	@Profile("Production")
	public class ExampleBakehouseConfig implements BakehouseConfigProvider {
	
		@Override @Bean
		public BakehouseConfig build(BakehouseConfigBuilder builder) {
			return builder
				.process("javascript").serveAsSingleFile("AppCode.js")
				.process("typescript").with(new TypescriptProcessor("TypescriptCode.js"))
				.serveResourcesFromCdn()
				.build();
		}
	}

3.  Deploy:
In production, the above generates the following html:

	<head>
		<script src='http://ajax.googleapis.com/ajax/libs/angularjs/1.0.3/angular.min.js' type='text/javascript'></script>
		<script src='/bakehouse-example/generated/AppCode.js' type='text/javascript'></script>
		<script src='/bakehouse-example/generated/TypescriptCode.js' type='text/javascript'></script>
	</head>

# Wanna see more?
This project is currently a POC only.
If you're interested, and wanna see it developed, you need to let me know!
Add your voice to the conversation [here](https://github.com/martypitt/bakehouse/issues/4)
