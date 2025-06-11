/**
 * @file: Mmd2AstParser.java
 * @brief: AST parser for custom plain text markup.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-09
 * @details: Parse Document into MetaMatter and top-level Sections (level 1 headings).
 */

package dev.madpang;

import dev.madpang.ast.*;
import dev.madpang.ast.blocks.RawTextBlock;
import java.io.*;
import java.util.*;

public class Mmd2AstParser {
	/**
	 * @brief: Parses the input stream and returns a Document object.
	 * @param[in]: InputStream containing the MMD content.
	 * @return: Parsed Document object.
	 * @throws: IOException If an error occurs during parsing.
	 */
	public static Document parse(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		
		Document doc = new Document();
		MetaMatter metaMatter = new MetaMatter();
		List<Section> sectionList = new ArrayList<>();
		
		String line;
		Section currentSection = null;

		boolean inHeader = false;
		/// --- [1] Parse MetaMatter
		/// @note: a Document MUST has a header block, starting at the first line
		if ((line = reader.readLine()) != null && line.trim().equals("+++ header")) {
			inHeader = true;
			while ((line = reader.readLine()) != null) {
				if (line.trim().equals("+++") && inHeader) {
					inHeader = false;
					break;
				}
				parseMetaLine(metaMatter, line);
			}
		} else {
			throw new IOException("Post must provide a header block, starting with '+++ header'");
		}
		
		if (inHeader) {
			throw new IOException("Header block is not closed properly, expecting '+++' to end the header.");
		}

		doc.metaMatter = metaMatter;

		// Step 2: Parse Sections (level 1 headings)
		while ((line = reader.readLine()) != null) {
			if (line.startsWith("# ")) {
				if (currentSection != null) {
					sectionList.add(currentSection);
				}
				Heading heading = new Heading(1, line.substring(2).trim());
				currentSection = new Section(heading);
			} else {
				if (currentSection != null) {
					// For now, just add lines as a single SemanticParagraph
					if (currentSection.paragraphs.isEmpty()) {
						currentSection.paragraphs.add(new SemanticParagraph());
					}
					// Add as a raw block (to be refined later)
					currentSection.paragraphs.get(0).blocks.add(new RawTextBlock(line));
				}
			}
		}
		if (currentSection != null) {
			sectionList.add(currentSection);
		}
		doc.sectionList = sectionList;
		return doc;
	}

	private static void parseMetaLine(MetaMatter metaMatter, String line) {
		String trimmed = line.trim();
		if (trimmed.startsWith("@file:")) {
			metaMatter.file = trimmed.substring(6).trim();
		} else if (trimmed.startsWith("@brief:")) {
			metaMatter.brief = trimmed.substring(7).trim();
		} else if (trimmed.startsWith("@title:")) {
			metaMatter.title = trimmed.substring(7).trim();
		} else if (trimmed.startsWith("@author:")) {
			metaMatter.author = trimmed.substring(8).trim();
		} else if (trimmed.startsWith("@date:")) {
			// Dates are listed in the following lines, starting with '-'
			// This will be handled in the main parse loop if needed
		} else if (trimmed.startsWith("- ")) {
			// Date lines (after @date:)
			metaMatter.date.add(trimmed.substring(2).trim());
		} else if (trimmed.startsWith("@version:")) {
			metaMatter.version = trimmed.substring(9).trim();
		}
	}
}
