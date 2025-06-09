/**
 * @file: ListBlock.java
 * @brief: Represents a list block in the document.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-09
 */

package dev.madpang.ast.blocks;

import java.util.*;

public class ListBlock implements IBlock {
	public boolean ordered;
	public List<String> items = new ArrayList<>();

	public ListBlock(boolean ordered) {
		this.ordered = ordered;
	}
}
