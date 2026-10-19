/**
 * @file IBlock.java
 * @brief This interface represents a block of text in the document's abstract syntax tree (AST).
 * @author [madpang, t-tang-rfc]
 * @date [created: 2025-06-09, updated: 2026-10-14]
 */

package mmd2html.ast.blocks;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import mmd2html.util.LineCursor;

/**
 * @brief Contract for a Markdown AST block that can render itself to HTML.
 */
public interface IBlock {
	// Get the type name of this block, e.g., "paragraph", "code", etc.
	String getTypeName();

	// Render this block as a sequence of HTML lines.
	List<String> toHTML() throws IOException;

	/**
	 * @brief Callable adapter for static block factories used by the parser registry.
	 */
	@FunctionalInterface
	interface Parser {
		/**
		 * @details Tries a block at cursor.peek(). A non-match consumes nothing.
		 * @details Success consumes the complete block, including fences when present, and leaves the following line unconsumed.
		 * @details A terminating blank line remains for the semantic paragraph to consume.
		 * @details A malformed matching block may throw.
		 */
		Optional<IBlock> tryParse(LineCursor cursor) throws IOException;
	}
}
