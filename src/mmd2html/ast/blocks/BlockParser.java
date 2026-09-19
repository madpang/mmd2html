/**
 * @file: BlockParser.java
 * @brief: Functional interface for parsing blocks from MMD text.
 * @author: madpang
 * @date: [created: 2025-07-11, updated: 2025-07-11]
 * @see IBlock.java
 */

package mmd2html.ast.blocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

@FunctionalInterface
public interface BlockParser {
	/**
	 * @brief: Try to parse a block starting with firstLine. Return Optional.empty() if not applicable.
	 */
	Optional<IBlock> tryParse(BufferedReader reader, String firstLine) throws IOException;
}
