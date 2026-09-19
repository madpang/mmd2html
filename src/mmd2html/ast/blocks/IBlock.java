/**
 * @file: IBlock.java
 * @brief: This interface represents a block of text in the document's abstract syntax tree (AST).
 * @author: madpang
 * @date: [created: 2025-06-09, updated: 2025-07-11]
 */

package mmd2html.ast.blocks;

import java.io.IOException;
import java.util.List;

/**
 * @brief: Contract for a Markdown AST block that can render itself to HTML.
 */
public interface IBlock {
	// Get the type of this block, e.g., "paragraph", "code", etc.
	String getType();
	// Render this block as a sequence of HTML lines.
	List<String> toHTML() throws IOException;	
}
