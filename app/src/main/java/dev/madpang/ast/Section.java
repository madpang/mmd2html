/**
 * @file: Section.java
 * @brief: Represents a section in the document.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-09
 */

package dev.madpang.ast;

import java.util.*;

public class Section {
	public Heading heading;
	public List<SemanticParagraph> paragraphs = new ArrayList<>();

	public Section(Heading heading) {
		this.heading = heading;
	}
}
