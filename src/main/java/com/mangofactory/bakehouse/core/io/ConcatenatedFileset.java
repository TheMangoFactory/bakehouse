package com.mangofactory.bakehouse.core.io;

import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.collect.Lists;

@RequiredArgsConstructor
public class ConcatenatedFileset {

	private final List<ConcatenatedFile> files = Lists.newArrayList();
	@Getter
	private final List<FilePath> filePaths;
	private String concatenatedSource;
	

	public ConcatenatedFileset(FilePath... paths)
	{
		filePaths = Lists.newArrayList(paths);
	}
	
	@SneakyThrows
	public String getConcatenatedSource()
	{
		if (concatenatedSource == null)
		{
			concatenate();
		}
		return concatenatedSource;
	}

	private void concatenate() {
		if (!files.isEmpty())
			return;
		
		StringBuilder sb = new StringBuilder();
		int endLineNumber = -1;
		for (FilePath path : filePaths)
		{
			ConcatenatedFile file = new ConcatenatedFile(path,endLineNumber + 1);
			endLineNumber = file.getEndLineNumber();
			files.add(file);
			if (sb.length() > 0)
				sb.append("\n");
			sb.append(file.getSource());
		}
		
		concatenatedSource = sb.toString();
	}

	public FilePath getFilePathAtLine(int i) {
		return getFileAtLine(i).filePath;
		
	}
	public String getSourceForFileAtLine(Integer line) {
		return getFileAtLine(line).source;
	}

	private ConcatenatedFile getFileAtLine(int line) {
		concatenate();
		for (ConcatenatedFile file : files)
		{
			if (file.containsLineNumber(line))
				return file;
		}
		return null;
	}

	@RequiredArgsConstructor
	class ConcatenatedFile
	{
		private final FilePath filePath;
		@Getter
		private final int startLineNumber;
		private Integer endLineNumber;
		
		private String source;
		
		@SneakyThrows
		public String getSource()
		{
			if (source == null)
			{
				source = FileUtils.readFileToString(filePath.getFile());
			}
			return source;
		}
		
		public boolean containsLineNumber(int i) {
			return i >= startLineNumber
					&& i <= endLineNumber;
		}

		public int getEndLineNumber()
		{
			if (endLineNumber == null)
			{
				endLineNumber = startLineNumber + StringUtils.countMatches(getSource(), "\n");
			}
			return endLineNumber;
		}
	}
}
