package com.mangofactory.bakehouse.core.compilers;

import java.util.List;

import lombok.Data;

import org.apache.commons.lang.StringUtils;

import com.mangofactory.bakehouse.core.io.FilePath;


@Data
public class CompilationProblem {

	private final Integer line;
	private final Integer column;
	private final String message;

	private String source;
	private FilePath filePath;
	
	@Override
	public String toString()
	{
		return message  + " (" + line + " , " + column + ")";
	}
	
	public static String getMessage(List<CompilationProblem> problems)
	{
		return StringUtils.join(problems, "\n");
	}
	
	public String getLocationDescription()
	{
		if (filePath == null)
		{
			return "Unknown location";
		} else {
			return filePath.getFileName() + " Line: " + line + " Column: " + column;
		}
	}
	public String getSource(int startLine, int endLine)
	{
		if (StringUtils.isEmpty(source))
		{
			return "";
		}
		
		StringBuilder sb = new StringBuilder();
		String[] split = source.split("\n");
		for (int i = startLine; i <= endLine; i++)
		{
			if (sb.length() > 0)
				sb.append("\n");
			sb.append(split[i - 1].trim());
		}
		return sb.toString();
	}

	public int getLineCount() {
		if (source == null)
		{
			return 0;
		} else {
			return source.split("\n").length;
		}
	}
}