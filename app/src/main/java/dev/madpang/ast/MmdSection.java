/**
 * @file: Section.java
 * @brief: Represents a section in the document.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-09
 */

package dev.madpang.ast;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class MmdSection {
	public Heading heading;
	public List<SemanticParagraph> paragraphs = new ArrayList<>();

	public MmdSection(Heading heading) {
		this.heading = heading;
	}

	/**
	 * Parses all sections from the reader until EOF.
	 */
	public static List<MmdSection> parseSections(BufferedReader reader) throws IOException {
		List<MmdSection> sectionList = new ArrayList<>();
		String line;
		MmdSection currentSection = null;
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("# ")) {
				if (currentSection != null) {
					sectionList.add(currentSection);
				}
				Heading heading = new Heading(1, line.substring(2).trim());
				currentSection = new MmdSection(heading);
			} else {
				if (currentSection != null) {
					if (currentSection.paragraphs.isEmpty()) {
						currentSection.paragraphs.add(new SemanticParagraph());
					}
					currentSection.paragraphs.get(0).blocks.add(new dev.madpang.ast.blocks.RawTextBlock(line));
				}
			}
		}
		if (currentSection != null) {
			sectionList.add(currentSection);
		}
		return sectionList;
	}
}
