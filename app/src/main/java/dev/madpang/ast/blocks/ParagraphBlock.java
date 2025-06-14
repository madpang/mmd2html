/**
 * @file: ParagraphBlock.java
 * @brief: Represents a paragraph in the document.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-09
 */

package dev.madpang.ast.blocks;

public class ParagraphBlock implements IBlock {
	public String text;

	public ParagraphBlock(String text) {
		this.text = text;
	}
}
