/**
 * @file: BlockParser.java
 */

package dev.madpang.ast.blocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;

/**
 * Functional interface for parsing a block from a reader.
 */
@FunctionalInterface
public interface BlockParser {
	/**
	 * Try to parse a block starting with firstLine. Return Optional.empty() if not applicable.
	 */
	Optional<IBlock> tryParse(BufferedReader reader, String firstLine) throws IOException;
}
