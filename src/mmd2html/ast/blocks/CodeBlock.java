/**
 * @file CodeBlock.java
 * @brief Represents a fenced code block in the document.
 * @author [madpang, t-tang-rfc, claude]
 * @date [created: 2025-06-09, updated: 2026-10-14]
 * 
 * @details A code block is started with "``` <string-without-whitespace>" (e.g. "``` java") to indicate the code type, and it ends with EXACTLY "```".
 */

package mmd2html.ast.blocks;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Optional;

import mmd2html.util.CommonUtil;
import mmd2html.util.LineCursor;

public final class CodeBlock implements IBlock {
	// Instance member(s)
	private List<String> codeLines = new ArrayList<>();
	private String codeType = null; // e.g. "java", "python", etc.	

	@Override
	public String getTypeName() {
		return "code";
	}

	@Override
	public List<String> toHTML() throws IOException {
		// An empty code block gives an empty <pre>, like an empty MetaInfo block is allowed.
		List<String> htmlLines = new ArrayList<>();
		htmlLines.add("<pre>");
		for (String line : codeLines) {
			htmlLines.add(CommonUtil.escapeHTML(line));
		}
		htmlLines.add("</pre>");
		return htmlLines;
	}

	public String getCodeType() {
		return codeType;
	}

	/** 
	 * @brief Static factory / parser
	 * @details A non-match leaves the cursor untouched;
	 * @details success consumes both fences, or everything up to EOF without a closing fence.
	 */
	public static Optional<IBlock> parse(LineCursor cursor) throws IOException {
		Matcher matcher = getCodeBlockOpeningPattern().matcher(cursor.peek());
		if (!matcher.matches()) {
			return Optional.empty();
		}
		// Code block opening found
		CodeBlock block = new CodeBlock();
		block.codeType = matcher.group(1);
		cursor.next();
		// Parse the code block content until the closing fence is found.
		String closingFence = getCodeBlockFence();
		String currentLine = null;
		while ((currentLine = cursor.next()) != null && !currentLine.equals(closingFence)) {
			block.codeLines.add(currentLine);
		}
		return Optional.of(block);
	}

	// --- Private Utility Functions
	private static String getCodeBlockFence() {
		return "```";
	}

	private static Pattern getCodeBlockOpeningPattern() {
		String openingRegex = "^" + getCodeBlockFence() + " (\\S+)$";
		return Pattern.compile(openingRegex);
	}
}
