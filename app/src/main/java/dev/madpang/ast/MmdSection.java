/**
 * @file: MmdSection.java
 * @brief: Represents a section in the MMD document.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-18
 */

package dev.madpang.ast;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
	
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
     *   |
	 *   V
	 *  1st column, start with no space before '#'
	 */
	public static MmdSection parse(BufferedReader reader, String firstLine) throws IOException {
		MmdSection section = new MmdSection();
		try {
			// [1] If firstLine is not provided, read the first line from the reader
			String headingLine = (firstLine != null) ? firstLine : reader.readLine();
			if (headingLine == null || headingLine.trim().isEmpty()) {
				throw new IOException("MMD section must start with a heading line.");
			}
			// [2] Parse the heading line
			Pattern headingPattern = Pattern.compile("^(#{1,3}) (\\S.*)$");
			Matcher headingMatcher = headingPattern.matcher(headingLine);
			if (!headingMatcher.matches()) {
				throw new IOException("MMD section heading must start with '#', '##', or '###'.");
			}
			section.level = headingMatcher.group(1).length(); // Level-1, 2, or 3 heading
			section.heading = headingMatcher.group(2).trim(); // Get the heading text
			// [3] @todo: Parse paragraphs and subsections
		} catch (IOException e) {
			throw e; // Re-throw the original exception
		}
		return section;
	}

	/**
	 * @brief: A print method to display the section's content.
	 */
	public void print() {
		System.out.println("---     MMD Section Block   ---");
		System.out.println("Section Level: " + level);
		System.out.println("Heading: " + heading);
		System.out.println("Paragraphs: " + paragraphs.size());
		System.out.println("Subsections: " + subsections.size());
		System.out.println("--------------------------------");
	}
}
