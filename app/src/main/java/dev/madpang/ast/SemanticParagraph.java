/**
 * @file: SemanticParagraph.java
 * @brief: Represents a semantic paragraph in a section of the document.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-19
 */

package dev.madpang.ast;

import java.io.BufferedReader;
import java.io.IOException;

import dev.madpang.ast.blocks.RawTextBlock;

public class SemanticParagraph {
	public RawTextBlock block = new RawTextBlock();
	
	/**
	 * @note:
	 * A SemanticParagraph is a collection of blocks that may represent various types of content, such as normal paragraphs, code blocks, lists, equations, etc.
	 * 
	 * @details:
	 * In a MMD doc, semantic paragraphs are typically formed by multiple lines of text that are grouped together, separated by blank lines.
	 * This class is created to address the lack of such a structure in Markdown, or in HTML.
	 * In modern writing, images, lists, tables etc. are all linked together to form a semantic context---while Markdown only considers the textual structure.
	 * 
	 * @todo:
	 * This class should hold a list of blocks, which can be of different types.
	 * But currently, it only holds a RawTextBlock.
	 *
	 */
	public static SemanticParagraph parse(BufferedReader reader, String firstLine) throws IOException {
		SemanticParagraph sp = new SemanticParagraph();
		try {
			String currenLine = (firstLine != null) ? firstLine : reader.readLine();
			if (currenLine == null || currenLine.trim().isEmpty()) {
				throw new IOException("MMD semantic paragraph must start with a non-empty line.");
			}
			/// @todo: Determine the block type delegate the parsing
			sp.block = RawTextBlock.parse(reader, currenLine);
		} catch (IOException e) {
			throw e; // Re-throw the original exception
		}
		return sp;
	}
}
