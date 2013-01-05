package com.mangofactory.bakehouse.core.exec;

import java.util.List;

import lombok.Getter;

import org.apache.commons.exec.LogOutputStream;

import com.google.common.collect.Lists;

public class LogCollectingOutputStream extends LogOutputStream {

	private final StringBuilder sb = new StringBuilder();
	@Getter
	private final List<String> lines = Lists.newLinkedList();
	@Override
	protected void processLine(String line, int level) {
		lines.add(line);
		sb.append(line).append("\n");
	}
	
	@Override
	public String toString() {
		return sb.toString();
	}

}
