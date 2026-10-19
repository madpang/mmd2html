/**
 * @file MmdDocSection.java
 * @brief This class represents a section of an MMD document.
 * @author [t-tang-rfc, claude, codex]
 * @date [created: 2026-09-21, updated: 2026-10-15]
 * 
 * @details A real mmd doc looks like the following,
 * ///////////////////////////////||\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
 * | ---                                                          |<-\ 1st line
 * | file: how-to-parse-mmd.txt                                   |  |
 * | brief: White paper on mmd parsing                            |  |
 * | author: t-tang-rfc                                           |  }=> the MetaInfo
 * | date: [created: 2026-09-21, updated: 2026-09-22]             |  |
 * | version: 0.1.0                                               |  |
 * | ---                                                          |--/
 * |                                                              |
 * | # The Level-1 Heading                                        | <- one and only for a valid mmd doc
 * |                                                              |
 * | Semantic Paragraphs start here ...                           |
 * | This line together with the previous one belong to the same \|
 * | semantic paragraph.                                          |
 * |                                                              |
 * | MetaInfo in the beginning is required for a valid mmd doc.   |
 * |                                                              |
 * | ## Level-2 Heading                                           |
 * |                                                              |
 * | Content under Level-2 Heading starts here ...                |
 * |                                                              |
 * | ### Level-3 Heading                                          |
 * |                                                              |
 * | Content under Level-3 Heading starts here ...                |
 * |                                                              |
 * | ## Yet Another Level-2 Heading                               |
 * |                                                              |
 * | Note, a block surrounded by opening and closing triple dash  \| 
 * | is treated as a MetaInfo block.                              |
 * | It is basically a Semantic Paragraph.                        |
 * | But it is *invisible* in the rendered HTML.                  |
 * | When putting key-value pairs inside a MetaInfo block, they  \|
 * | could be used like a YAML block.                             |
 * |                                                              |
 * | ---                                                          |--\
 * | key1: value1                                                 |  |
 * | key2: value2                                                 |  }=> another MetaInfo
 * | key3: value3                                                 |  |
 * | ---                                                          |--/
 * |                                                              |
 * | MetaInfo block could be anywhere, but it is the parser that \|
 * | decides how to handle it.                                    |
 * |                                                              |
 * | ## Summary                                                   |
 * |                                                              |
 * | NOTE, the whole doc could be regarded as a *Level-0* section.|
 * |                                                              |<- EOF
 * \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\||///////////////////////////////
 *   |
 *   v
 * 1st col.
 * @details The whole document is considered as a Level-0 section.
 * @details A valid MMD document should have one and only one Level-1 section.
 * @details A valid MMD document should only use canonical section nesting, i.e., a Level-N section should only appear under a Level-(N-1) section.
 * 
**/

package mmd2html.ast;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import mmd2html.util.CommonUtil;
import mmd2html.util.LineCursor;

public final class MmdDocSection {
	private int sectionLevel = 0; // can be 0 ~ 5
	private String sectionHeading = null;
	private int fuzzyLevel = 0; // 0: determined, 1: ===, 2: ---
	private List<SemanticParagraph> sParagraphs = new ArrayList<>(); // can be empty
	private List<MmdDocSection> subSections = new ArrayList<>();     // can be empty

	/**
	 * @brief An overloaded `toHTML` method that writes the HTML lines to a file.
	 * 
	 * @param[in] filepath -- The path to the output HTML file.
	 */
	public void toHTML(String filepath) throws Exception {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filepath))) {
			for (String line : toHTML()) {
				writer.write(line);
				writer.newLine();
			}
		}
	}

	/**
	 * @brief Parses a mmd document from a file, always as a H0.
	 * 
	 * @return A MmdDocSection object of level-0
	 * 
	 * @param[in] filepath -- The path to the mmd file to parse.
	 */
	public static MmdDocSection parse(String filepath) throws Exception {
		try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
			return parse(0, null, new LineCursor(reader));
		}
	}

	/**
	 * @brief Converts this section to HTML, recursively.
	 * 
	 * @return A list of strings representing the HTML lines.
	 * 
	 * @details A level-0 section has no heading line; any other section writes `<hN>` with N its section level, fuzzy ones included.
	 */
	private List<String> toHTML() throws Exception {
		List<String> htmlLines = new ArrayList<>();
		// Write the section heading
		if (sectionLevel != 0) {
			htmlLines.add(String.format("<h%d>%s</h%d>", sectionLevel, CommonUtil.convertEmDash(CommonUtil.escapeHTML(sectionHeading)), sectionLevel));
		}
		// Write paragraphs
		for (SemanticParagraph para : sParagraphs) {
			htmlLines.addAll(para.toHTML());
		}
		// Write subsections recursively
		for (MmdDocSection sub : subSections) {
			htmlLines.addAll(sub.toHTML());
		}
		return htmlLines;
	}

	/**
	 * @brief Parses a mmd document section from the given reader, w/ optionally the first line provided.
	 * 
	 * @return A MmdDocSection object
	 * 
	 * @param[in] sectionLevel -- The level of the section (0 ~ 5).
	 * @param[in] headingLine -- The heading line of the section.
	 * @param[in] cursor -- Shared cursor positioned at the next unconsumed line.
	 * 
	 * @details For explicit section, `headingLine` contains all the info.;
	 * @details However, sectionLevel needs to be specified for fuzzy sections.
	 * 
	 * @note The cursor is positioned immediately after the heading. A closing heading is left unconsumed for the caller.
	 */
	private static MmdDocSection parse(int sectionLevel, String headingLine, LineCursor cursor) throws Exception {
		// [0] Parameter validation
		if (sectionLevel < 0 || sectionLevel > 5) {
			throw new IllegalArgumentException("Section level out of range: " + sectionLevel);
		}
		if (sectionLevel == 0 && headingLine != null) {
			throw new IllegalArgumentException("Level-0 section should not have a heading line: " + headingLine);
		}
		if (sectionLevel != 0 && headingLine == null) {
			throw new IllegalArgumentException("Non-level-0 section must have a heading line.");
		}

		int myFuzzyLevel = 0;
		String myHeading = null;
		Pattern headingPattern = getHeadingRegex();
		if (sectionLevel != 0) { // Cross-validate headingLine and expected level
			Matcher myHeadingMatcher = headingPattern.matcher(headingLine);
			if (!myHeadingMatcher.matches()) {
				throw new IllegalArgumentException("Invalid heading line: " + headingLine);
			}
			String myHeadingMarker = myHeadingMatcher.group(1);
			if (myHeadingMarker.startsWith("#")) {
				if (myHeadingMarker.length() != sectionLevel) {
					throw new IllegalArgumentException("Heading level does not match the expected section level: " + headingLine);
				}
				myHeading = myHeadingMatcher.group(2).trim();
			} else if (myHeadingMarker.startsWith("=")) {
				myFuzzyLevel = 1;
				myHeading = myHeadingMatcher.group(2).trim();
			} else { // myHeadingMarker.equals("---")) {
				myFuzzyLevel = 2;
				myHeading = myHeadingMatcher.group(2).trim();
			}
		}

		// [1] Initialize the MmdDocSection object.
		MmdDocSection mmdDocSec = new MmdDocSection();
		mmdDocSec.sectionLevel = sectionLevel;
		mmdDocSec.sectionHeading = myHeading;
		mmdDocSec.fuzzyLevel = myFuzzyLevel;

		// [2] Parse paragraphs and subsections
		String currentLine = null;
		while ((currentLine = cursor.peek()) != null) {
			// Skip empty lines
			if (currentLine.isEmpty()) {
				cursor.next();
				continue;
			}
			// Delegate parsing to semantic paragraphs UNTIL a new heading is found
			Matcher nextHeadingMatcher = headingPattern.matcher(currentLine);
			if (!nextHeadingMatcher.matches()) {
				mmdDocSec.sParagraphs.add(SemanticParagraph.parse(cursor));
				continue;
			}
			/**
			 * @brief Decides whether a heading line opens a subsection of the current section.
			 *
			 * @return The level of the subsection to open, or -1 if the heading closes the current section instead.
			 *
			 * @param[in] currentSectionLevel -- The level of the current section.
			 * @param[in] currentFuzzyLevel -- Fuzzy level of the current section: 0 for determined, 1 for `===`, 2 for `---`.
			 * @param[in] nextSectionMarker -- The marker of the heading line just read.
			 *
			 * @details The rules, where "explicit" means `#`, `##`, `###`, and level-0 counts as explicit:
			 * @details An explicit heading under an explicit section opens a subsection if it is deeper than the current level.
			 * @details An explicit heading under a fuzzy section always pops out, fuzzy sections come *after* an explicit heading.
			 * @details `===` under an explicit section opens a subsection at level + 1; under a fuzzy section it pops out.
			 * @details `---` under an explicit section opens a subsection at level + 2; under `===` at level + 1; under `---` it pops out.
			 */
			int nextLevel = -1;
			String nextHeadingMarker = nextHeadingMatcher.group(1);
			if (nextHeadingMarker.startsWith(String.valueOf(getExplicitHeadingSymbol()))) {
				int tmpCount = nextHeadingMarker.length();
				nextLevel = (mmdDocSec.fuzzyLevel == 0) && (tmpCount > mmdDocSec.sectionLevel) ? tmpCount : -1;
			} else if (nextHeadingMarker.equals(getHeadingPrefixPattern(1))) {
				nextLevel = (mmdDocSec.fuzzyLevel == 0) ? (mmdDocSec.sectionLevel + 1) : -1;
			} else { // nextHeadingMarker.equals(getHeadingPrefixPattern(2))
				nextLevel = (mmdDocSec.fuzzyLevel == 0) ? (mmdDocSec.sectionLevel + 2) : ((mmdDocSec.fuzzyLevel == 1) ? (mmdDocSec.sectionLevel + 1) : -1);
			}
			// If subsection heading is found, parse it recursively
			if (nextLevel > 0) {
				// Consume the heading; its subsection owns the following lines.
				cursor.next();
				mmdDocSec.subSections.add(MmdDocSection.parse(nextLevel, currentLine, cursor));
				continue;
			}
			// The heading closes this section; leave it on the cursor for the caller.
			break;
		}

		return mmdDocSec;
	}

	// --- Private Utility Functions
	// @note The following utility functions take the role of private constant members, since I want the whole things work w/o instance creation.

	/**
	 * @brief Returns the symbol used to mark explicit headings.
	 *
	 * @return The explicit heading symbol as a character.
	 */
	private static char getExplicitHeadingSymbol() {
		return '#';
	}

	/**
	 * @brief Returns the heading prefix pattern for the given fuzzy level.
	 * 
	 * @param[in] fuzzyLevel
	 * 
	 * @return Literal string representing the heading prefix pattern for the given fuzzy level.
	 */
	private static String getHeadingPrefixPattern(int fuzzyLevel) {
		switch (fuzzyLevel) {
			case 0: return String.format("%c{1,3}", getExplicitHeadingSymbol());
			case 1: return "===";
			case 2: return "---";
			default: throw new IllegalArgumentException("Invalid fuzzy level: " + fuzzyLevel);
		}
	}

	/**
	 * @brief Returns the pattern used to match heading lines.
	 *
	 * @return A compiled regular expression pattern.
	 * 
	 * @details The pattern matches either explicit headings (`#`, `##`, `###`) or fuzzy headings (`===`, `---`) followed by the heading text.
	 */
	private static Pattern getHeadingRegex() {
		String headingRegex = "^" + "(" 
			+ getHeadingPrefixPattern(0) + "|"
			+ getHeadingPrefixPattern(1) + "|"
			+ getHeadingPrefixPattern(2) 
			+ ") (\\S.*)$";
		return Pattern.compile(headingRegex);
	}

}
