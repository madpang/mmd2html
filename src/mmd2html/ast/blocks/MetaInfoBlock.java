/**
 * @file MetaInfoBlock.java
 * @brief Opaque metadata that can coexist with other paragraph blocks.
 * @author [codex, t-tang-rfc]
 * @date [created: 2026-10-05, updated: 2026-10-14]
 * 
 * @note MetaInfoBlock is NOT *visible content* of the to-be-rendered HTML.
 * @note It is intended for storing auxiliary information for the processor to utilize.
**/

package mmd2html.ast.blocks;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

import mmd2html.util.LineCursor;

/** Collects metadata verbatim without interpreting YAML or content blocks. */
public final class MetaInfoBlock implements IBlock {
	// Instance member(s)
	private List<String> metaLines = new ArrayList<>();

	@Override
	public String getTypeName() {
		return "meta";
	}

	@Override
	public List<String> toHTML() {
		return List.of();
	}

	/** 
	 * @brief Returns payload without opening and closing markers; indentation is preserved.
	 */
	public List<String> getRawMetaInfoChunk() {
		return metaLines;
	}

	/**
	 * @brief Parses a MetaInfoBlock from the given line cursor.
	 * 
	 * @details An exact `---` line opens the block; metadata ends at the next exact `---`, which is consumed.
	 * @details Blank lines inside belong to the block.
	 * @details Without a closing `---`, everything up to EOF is metadata.
	 */
	public static Optional<IBlock> parse(LineCursor cursor) throws IOException {
		String fence_line = getMetaInfoBlockFence();
		if (!cursor.peek().equals(fence_line)) {
			return Optional.empty();
		}
		// MetaInfo block opening found
		MetaInfoBlock block = new MetaInfoBlock();
		cursor.next(); // opening marker
		String currentLine = null;
		while ((currentLine = cursor.next()) != null && !currentLine.equals(fence_line)) {
			block.metaLines.add(currentLine);
		}
		return Optional.of(block);
	}

	// --- Private Utility Functions
	private static String getMetaInfoBlockFence() {
		return "---";
	}
}
