/**
 * @file: SemanticParagraph.java
 * @brief: Represents a semantic paragraph in a section of the document.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-07-11
 */

package dev.madpang.ast;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import dev.madpang.ast.blocks.*;

public class SemanticParagraph {
	private List<IBlock> blocks = new ArrayList<>();
	// @note: A `SemanticParagraph` has an implicit `public String terminalLine` which is a blank line.
	private static final List<BlockParser> REGISTERED = List.of(
		CodeBlock.PARSER
		// @note: DO NOT register ParagraphBlock here, it does not have a static parse method.
	);

	/**
	 * @brief:
	 * A SemanticParagraph is a collection of blocks that may represent various types of content, such as normal paragraphs, code blocks, lists, equations, etc.
	 * 
	 * @details:
	 * In a MMD doc, semantic paragraphs are typically formed by multiple lines of text that are grouped together, separated by blank lines.
	 * This class is created to address the lack of such a structure in Markdown, or in HTML.
	 * In modern writing, images, lists, tables etc. are all linked together to form a semantic context---while Markdown only considers the textual structure.
	 */
	public static SemanticParagraph parse(BufferedReader reader, String firstLine) throws IOException {
		// [1] firstLine can not be null, can not be empty
		if (firstLine == null || firstLine.trim().isEmpty()) {
			throw new IOException("[ERROR] MMD semantic paragraph must start with a non-empty line.");
		}

		SemanticParagraph sp = new SemanticParagraph();

		String currentLine = firstLine;
		while (currentLine != null && !currentLine.trim().isEmpty()) {
			boolean parsed = false;
			// [2] read a line from the reader, try its registered parsers
			for (BlockParser parser : REGISTERED) {
				/**
				 * @note: Parser should guarantee that it will not drain the reader, if the first does not match.
				 */
				Optional<IBlock> block = parser.tryParse(reader, currentLine);
				if (block.isPresent()) {
					// if one of the registered parser succeeds, collect the block
					sp.blocks.add(block.get());
					parsed = true;
					break;
				}
			}

			// [3] if none of registered parser applies, create a ParagraphBlock (if necessary), add that line to the paragraph block
			if (!parsed) {
				boolean needNewParagraph = (sp.blocks.isEmpty() || (sp.blocks.get(sp.blocks.size() - 1).getType() != ParagraphBlock.BLOCK_TYPE));
				ParagraphBlock paragraph = needNewParagraph ? new ParagraphBlock() : (ParagraphBlock) sp.blocks.get(sp.blocks.size() - 1);

				paragraph.addLine(currentLine);
				if (needNewParagraph) {
					sp.blocks.add(paragraph);
				}
			}

			// [4] Continue for a new line
			currentLine = reader.readLine();
		}
		return sp;
	}

	/**
	 * Converts this semantic paragraph to HTML.
	 */
	public List<String> toHTML() throws IOException {
		if (blocks.isEmpty()) {
			throw new IOException("[ERROR] SemanticParagraph contains no blocks to convert to HTML.");
		}
	
		List<String> htmlLines = new ArrayList<>();
		htmlLines.add("<s-paragraph>");
		for (IBlock block : blocks) {
			htmlLines.addAll(block.toHTML());
		}
		htmlLines.add("</s-paragraph>");
		return htmlLines;
	}
}
