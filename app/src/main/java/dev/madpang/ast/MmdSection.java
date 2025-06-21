/**
 * @file: MmdSection.java
 * @brief: Represents a section in the MMD document.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-21
 */

package dev.madpang.ast;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MmdSection {
	public int sectionLevel;
	public String headLine;
	public List<SemanticParagraph> sParagraphs = new ArrayList<>(); // can be empty
	public List<MmdSection> subSections = new ArrayList<>();        // can be empty
	public String terminalLine; // the line that terminates this section

	/**
	 * @note:
	 * A MMD doc must have a "body" of main content, which is enclosed by one and only one level-1 heading (e.g. "# My Heading") and may optionally contain multiple subheadings (level-2, level-3, etc.).
	 * Semantic paragraphs always come BEFORE any sub-sections, and they are separated by blank lines.
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
	 *   v
	 *  1st column, start with no space before '#'
	 */
	public static MmdSection parse(BufferedReader reader, String firstLine) throws IOException {
		MmdSection section = new MmdSection();
		try {
			// [1] If firstLine is not provided, read the first line from the reader
			String currentLine = (firstLine != null) ? firstLine : reader.readLine();
			if (currentLine == null || currentLine.trim().isEmpty()) {
				throw new IOException("MMD section must start with a heading line.");
			}
			// [2] Parse the heading line
			Pattern headingPattern = Pattern.compile("^(#{1,3}) (\\S.*)$");
			Matcher headingMatcher = headingPattern.matcher(currentLine);
			if (!headingMatcher.matches()) {
				throw new IOException("MMD section heading must start with '#', '##', or '###'.");
			}
			section.sectionLevel = headingMatcher.group(1).length();
			section.headLine = headingMatcher.group(2).trim();
			// [3] Parse paragraphs and subsections
			currentLine = reader.readLine(); // Read the next line
			while(currentLine != null) {
				// Skip empty lines
				if (currentLine.trim().isEmpty()) {
					currentLine = reader.readLine();
					continue;
				}
				// Delegate parsing to semantic paragraphs UNTIL a new heading is found
				Matcher subheadingMatcher = headingPattern.matcher(currentLine);
				if (!subheadingMatcher.matches()) {
					section.sParagraphs.add(SemanticParagraph.parse(reader, currentLine));
					currentLine = reader.readLine(); // Read the next line
					continue;
				}
				// If subsection heading is found, parse it recursively
				int nextLevel = subheadingMatcher.group(1).length();
				if (nextLevel > section.sectionLevel) {
					section.subSections.add(MmdSection.parse(reader, currentLine));
					// If there already some children sub-sections exist, set current line to the last one's terminalLine; only try read new line when the children list is empty
					currentLine = section.subSections.isEmpty() ? reader.readLine() : section.subSections.get(section.subSections.size() - 1).terminalLine;
					continue;
				}
				// If same/higher-level heading is found, mark the terminal line and hand back the control to the parent section
				section.terminalLine = currentLine;
				break;
			}
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
		System.out.println("Section Level: " + sectionLevel);
		System.out.println("Heading: " + headLine);
		System.out.println("Paragraphs: " + sParagraphs.size());
		System.out.println("Subsections: " + subSections.size());
		System.out.println("Terminal Line: " + (terminalLine == null ? "EOF" : terminalLine));
		System.out.println("--------------------------------");
		// Call print on all subsections recursively
		for (MmdSection subSection : subSections) {
			subSection.print();
		}
	}
}
