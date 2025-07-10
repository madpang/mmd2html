/**
 * @file: IBlock.java
 * @brief: This interface represents a block of text in the document's abstract syntax tree (AST).
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-07-11
 */

package dev.madpang.ast.blocks;

import java.io.IOException;
import java.util.List;

/**
 * Contract for a Markdown AST block that can render itself to HTML.
 */
public interface IBlock {
	// Get the type of this block, e.g., "paragraph", "code", etc.
	String getType();
	// Get the delimiter line that ends this block, if applicable.
	String getTerminalLine() throws IOException;
	// Render this block as a sequence of HTML lines.
	List<String> toHTML() throws IOException;	
}
