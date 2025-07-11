/**
 * @file: ParagraphBlock.java
 * @brief: Represents a paragraph in the document
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-07-11
 * @note:
 * ParagraphBlock is a *special* block in terms of parsing---it is the fallback block type that captures any non-empty content.
 * It does NOT provide a static `parse` method to read from a text stream and return an instance, instead, it should be controlled carefully by its caller---the SemanticParagraph class.
 */

package dev.madpang.ast.blocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dev.madpang.util.CommonUtil;

/**
 * Represents a plain paragraph block -- serves as fallback for any content.
 */
public final class ParagraphBlock implements IBlock {
	/**
	 * Self-identifying
	 */
	public static final String BLOCK_TYPE = "paragraph";
	// Sentence lines of the paragraph
	private List<String> sentenceLines = new ArrayList<>();

	/* default implicit constructor */

	/* interface behavior --------------------------------------------------- */
	@Override
	public String getType() {
		return BLOCK_TYPE;
	}

	@Override
	public List<String> toHTML() throws IOException {
		if (sentenceLines.isEmpty()) {
			throw new IOException("[ERROR] ParagraphBlock contains no lines to convert to HTML.");
		}

		List<String> htmlLines = new ArrayList<>();
		htmlLines.add("<p>");
		for (String line : sentenceLines) {
			// Escape HTML for each line
			htmlLines.add(CommonUtil.escapeHTML(line));
		}
		htmlLines.add("</p>");

		return htmlLines;
	}

	/* instance behavior ---------------------------------------------------- */
	public void addLine(String line) {
		if (line == null || line.trim().isEmpty()) {
			throw new IllegalArgumentException("[ERROR] HTML paragraph cannot contain blank or null lines");
		}
		// Collect sentence line
		this.sentenceLines.add(line);
	}
}
