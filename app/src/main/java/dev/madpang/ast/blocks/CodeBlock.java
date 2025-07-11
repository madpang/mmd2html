/**
 * @file: CodeBlock.java
 * @brief: Represents a code block in the document.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-07-11
 */

package dev.madpang.ast.blocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Optional;

import dev.madpang.util.CommonUtil;

/**
 * Represents a *fenced* code block.
 */
public final class CodeBlock implements IBlock {
	/**
	 * Self-identifying
	 */
	public static final String BLOCK_TYPE = "code";
	/**
	 * @note:
	 * A code block is started with `+++ <non-empty-string>`;
	 * and it ends with with EXACTLY `+++`.
	 */
	public static final String FENCE_LINE = "+++";
	// Code type
	private final String codeType; // e.g. "java", "python", etc.
	// Real content of the code block.
	private List<String> codeLines;

	/* constructor (accessible only within the same package, for test) ------ */
	CodeBlock(List<String> contentLines, String fenceTag) {
		this.codeType = fenceTag;
		this.codeLines = contentLines;
	}

	/* interface behavior --------------------------------------------------- */
	@Override
	public String getType() {
		return BLOCK_TYPE;
	}

	@Override
	public List<String> toHTML() throws IOException {
		if (codeLines.isEmpty()) {
			throw new IOException("[WARNING] CodeBlock contains no lines to convert to HTML.");
		}
		List<String> htmlLines = new ArrayList<>();
		htmlLines.add("<pre>");
		for (String line : codeLines) {
			htmlLines.add(CommonUtil.escapeHTML(line));
		}
		htmlLines.add("</pre>");
		return htmlLines;
	}

	/* instance behavior --------------------------------------------------- */
	String getCodeType() {
		return codeType;
	}

	/* static factory / parser --------------------------------------------- */
	public static Optional<IBlock> parse(BufferedReader reader, String firstLine) throws IOException {
		Pattern codeBlockStart = Pattern.compile("^\\+{3} (\\S.*)$");
		Matcher matcher = codeBlockStart.matcher(firstLine);
		if (!matcher.matches()) {
			return Optional.empty();
		}
		// Retrieve the code type
		String tag = matcher.group(1).trim();
		// Collect the actual code block content
		List<String> content = new ArrayList<>();
		String currentLine = reader.readLine();
		while ((currentLine != null && !currentLine.equals(FENCE_LINE))) {
			content.add(currentLine);
			currentLine = reader.readLine();
		}
		if (content.isEmpty()) {
			throw new IOException("[ERROR] Empty code block is not allowed!");
		}
		if (currentLine == null) {
			throw new IOException("[ERROR] Unterminated code fence!");
		}
		return Optional.of(new CodeBlock(content, tag));
	}

	/* static parser object to register globally --------------------------- */
	public static final BlockParser PARSER = CodeBlock::parse;
}
