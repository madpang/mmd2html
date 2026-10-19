/**
 * @file SemanticParagraph.java
 * @brief This class represents a semantic paragraph in a section of the document.
 * @author [madpang, codex, t-tang-rfc]
 * @date [created: 2025-06-09, updated: 2026-10-15]
 * 
 * @details A SemanticParagraph is a collection of blocks that may represent various types of content, such as normal paragraphs, code blocks, lists, equations, etc.
 * @details In a MMD doc, semantic paragraphs are typically formed by multiple lines of text that are grouped together, separated by blank lines.
 * @details This class is created to address the lack of such a structure in Markdown, or in HTML.
 * @details In modern writing, images, lists, tables etc. are all linked together to form a semantic context -- while Markdown only considers the textual structure.
 * 
**/

package mmd2html.ast;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import mmd2html.ast.blocks.*;
import mmd2html.util.LineCursor;

/**
 * Groups related text, metadata, code, and list blocks within a semantic paragraph.
 */
public final class SemanticParagraph {
	// Class member(s)
	private static final List<IBlock.Parser> REGISTERED = List.of(
		MetaInfoBlock::parse,
		CodeBlock::parse,
		ListBlock::parse
	);
	// Instance member(s)
	private List<IBlock> blocks = new ArrayList<>();

	/**
	 * @brief Converts the semantic paragraph to its HTML representation.
	 */
	public List<String> toHTML() throws IOException {
		if (blocks.isEmpty()) {
			throw new IOException("[ERROR] SemanticParagraph contains no blocks to convert to HTML.");
		}
		List<String> htmlLines = new ArrayList<>();
		for (IBlock block : blocks) {
			htmlLines.addAll(block.toHTML());
		}
		if (htmlLines.isEmpty()) {
			return List.of(); // do not emit a wrapper for invisible content
		}
		htmlLines.add(0, "<s-paragraph>");
		htmlLines.add("</s-paragraph>");
		return htmlLines;
	}	

	/**
	 * @brief Consumes one paragraph and its terminating blank line, if present.
	 */
	public static SemanticParagraph parse(LineCursor cursor) throws IOException {
		String firstLine = cursor.peek();
		if (firstLine == null || firstLine.trim().isEmpty()) {
			throw new IOException("[ERROR] MMD semantic paragraph must start with a non-empty line at line "
				+ cursor.nextLineNumber());
		}

		SemanticParagraph sp = new SemanticParagraph();
		String currentLine;
		while ((currentLine = cursor.peek()) != null && !currentLine.trim().isEmpty()) {
			boolean parsed = false;
			for (IBlock.Parser parser : REGISTERED) {
				Optional<IBlock> block = parser.tryParse(cursor);
				if (block.isPresent()) {
					sp.blocks.add(block.get());
					parsed = true;
					break;
				}
			}
			if (!parsed) {
				sp.addTextLine(cursor.next());
			}
		}
		if (currentLine != null) {
			cursor.next(); // consume the terminating blank line
		}
		return sp;
	}

	// --- Private Utility Functions
	private void addTextLine(String line) throws IOException {
		ParagraphBlock paragraph;
		if (!blocks.isEmpty() && blocks.get(blocks.size() - 1) instanceof ParagraphBlock existing) {
			paragraph = existing;
		} else {
			paragraph = new ParagraphBlock();
			blocks.add(paragraph);
		}
		paragraph.addLine(line);
	}
}
