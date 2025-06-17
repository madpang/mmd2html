/**
 * @file: MmdSection.java
 * @brief: Represents a section in the MMD document.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-18
 */

package dev.madpang.ast;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
	
public class MmdSection {
	public int level;
	public String heading;
	public List<SemanticParagraph> paragraphs = new ArrayList<>(); // can be empty
	public List<MmdSection> subsections = new ArrayList<>();       // can be empty

	/**
	 * @note:
	 * A MMD doc must have a "body" of main content, which is enclosed by one and only one level-1 heading (e.g. "# My Heading") and may optionally contain multiple subheadings (level-2, level-3, etc.).
	 *
	 * @details:
	 * A real MMD "body" looks like
	 * ----------------------------------------------------------------
	 * |                                                              |
	 * | # The ONE AND ONLY Level-1 Heading                           | <- 1st line
	 * |                                                              |
	 * | Some optional content here...                                |
	 * | NOTE, this forms an *anonymous* section.                     |
	 * |                                                              |
	 * | ## Level 2 heading (optional)                                |
	 * |                                                              |
	 * | Some content here...                                         |
	 * |                                                              |
	 * | ### Level 3 heading (optional)                               |
	 * |                                                              |
	 * | More content here...                                         |
	 * |                                                              |
	 * ----------------------------------------------------------------
	 */
	public static MmdSection parse(BufferedReader reader, String firstLine) throws IOException {
		MmdSection section = new MmdSection();
		System.out.println("Parsing MmdSection...");
		System.out.println("First non-empty line: " + firstLine);
		// section.sections = MmdSection.parseSections(reader);
		return section;
	}
}
