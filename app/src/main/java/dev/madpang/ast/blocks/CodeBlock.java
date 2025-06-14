/**
 * @file: CodeBlock.java
 * @brief: Represents a code block in the document.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-09
 */

package dev.madpang.ast.blocks;

public class CodeBlock implements IBlock {
	public String text;

	public CodeBlock(String text) {
		this.text = text;
	}
}
