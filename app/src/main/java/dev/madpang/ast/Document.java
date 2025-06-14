/**
 * @file: Document.java
 * @brief: Represents the entire document structure.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-09
 */

package dev.madpang.ast;

import java.util.*;

public class Document {
	public MetaMatter header = new MetaMatter();
	public List<Section> sections = new ArrayList<>();
}
