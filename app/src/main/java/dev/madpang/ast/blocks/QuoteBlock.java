/**
 * @file: QuoteBlock.java
 * @brief: Represents a quote block in the document.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-09
 */

package dev.madpang.ast.blocks;

public class QuoteBlock implements IBlock {
	public String text;

	public QuoteBlock(String text) {
		this.text = text;
	}
}
