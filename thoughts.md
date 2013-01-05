// make javascript files available
<bakehouse:scripts type="text/javascript">
	<bakehouse:script src="/resources/file1.js" />
	<bakehouse:script src="/resources/file2.js" />
</bakehouse:scripts>

// Development config:
Should be that none is required, as the files are just served as-is.

// Production config:
resources("/resources").withSuffix(".js").serveAsSingleFile();

// which should generate a tag similar to...
<script src="/resources/generated/my-generated-file.js" type="text/javascript" />
 
// the above is a short-cut to...
resources("/resources").withSuffix(".js")
	.processUsing(new ConcatenateResourcesProcessor())

// Add GZip compression at production
resources("/resources").withSuffix(".js")
	.processUsing(new ConcatenateResourcesProcessor(), new GZipResourcesProcessor())

// Add JsLint monitoring during dev-time
resources("/resources").withSuffix(".js")
	.processUsing(new JsLintResourcesProcessor())
	
// Make *.less available as *.css
<bakehouse:style src="/resources/styles.less" />
resources("/resources").withSuffix(".less")
	.processUsing(new LessCompilerProcessor())
	
