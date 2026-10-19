/**
 * @file ParagraphBlock.java
 * @brief Represents a plain paragraph block in the document.
 * @author [madpang, t-tang-rfc]
 * @date [created: 2025-06-09, updated: 2025-10-14]
 * 
 * @details ParagraphBlock is a *special* block in terms of parsing -- it is the fallback block type that captures any non-empty content.
 * @details It does NOT provide a static `parse` method that return an instance by reading from a text stream, instead, it should be controlled carefully by its caller -- the SemanticParagraph class.
 */

package mmd2html.ast.blocks;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mmd2html.util.CommonUtil;

public final class ParagraphBlock implements IBlock {
	// Instance member(s)
	private List<String> sentenceLines = new ArrayList<>();

	/* Default implicit constructor */

	/* Interface behavior --------------------------------------------------- */
	@Override
	public String getTypeName() {
		return "paragraph";
	}

	@Override
	public List<String> toHTML() throws IOException {
		List<String> htmlLines = new ArrayList<>();
		htmlLines.add("<p>");
		for (String line : sentenceLines) {
			// Escape HTML for each line, then convert em dashes
			htmlLines.add(CommonUtil.convertEmDash(CommonUtil.escapeHTML(line)));
		}
		htmlLines.add("</p>");
		return htmlLines;
	}

	/* Instance behavior ---------------------------------------------------- */
	public void addLine(String line) throws IOException {
		if (line == null || line.trim().isEmpty()) {
			throw new IOException("[ERROR] HTML paragraph cannot contain blank or null lines");
		}
		// Collect sentence line
		this.sentenceLines.add(line);
	}
}
