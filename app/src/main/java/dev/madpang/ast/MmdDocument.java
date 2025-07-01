/**
 * @file: MmdDocument.java
 * @brief: Represents the entire mmd-markup document structure, including the header and body.
 * @author: madpang
 * @details:
 * This class is the overarching structure for this mmd-to-html converter, it parses the mmd document into AST nodes, and reconstructs the HTML from the AST representation.
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-07-01
 */

package dev.madpang.ast;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class MmdDocument {
	public MmdHeader frontMatter = new MmdHeader();
	public MmdSection bodyContent = new MmdSection(); // root section (level-1) of the body

	/**
	 * @brief: Parses a MmdDocument from a BufferedReader, with an optional first line being supplied.
	 *
	 * @param[in]: reader -- The BufferedReader to read the document from.
	 * @param[in]: firstLine -- The first line, or null to read from the reader.
	 * @return: A MmdDocument object containing the parsed header and body.
	 * @throws: IOException If an I/O error occurs while reading the document.
	 * 
	 * @details: The second argument `firstLine` is useful to allow the caller to set some sort of "hook".
	 */
	public static MmdDocument parse(BufferedReader reader, String firstLine) throws IOException {
		MmdDocument doc = new MmdDocument();
		try {
			// [1] If firstLine is not provided, read the first line from the reader
			String currentLine = (firstLine != null) ? firstLine : reader.readLine();
			if (currentLine == null || !currentLine.equals("+++ header")) {
				throw new IOException("MMD DOC MUST PROVIDE A <HEADER>, STARTING WITH '+++ header'");
			}
			// [2] Delegate parsing of the header to MmdHeader
			doc.frontMatter = MmdHeader.parse(reader, currentLine);
			// [3] After parsing the header, skip potential empty lines
			while ((currentLine = reader.readLine()) != null && currentLine.trim().isEmpty()) {
				// Skip empty lines
				continue;
			}
			if (currentLine == null || !currentLine.startsWith("# ")) {
				throw new IOException("MMD DOC MUST HAVE A <BODY>, STARTING WITH A LEVEL-1 HEADING (e.g. '# My Heading').");
			}
			// [4] Delegate parsing of the body to MmdSection
			doc.bodyContent = MmdSection.parse(reader, currentLine);
		} catch (IOException e) {
			throw e; // Re-throw the original exception
		}
		return doc;
	}

	/**
	 * @brief: An overloaded `parse` method, with a a single argument.
	 */
	public static MmdDocument parse(BufferedReader reader) throws IOException {
		return parse(reader, null);
	}

	/**
	 * @brief: An overloaded `parse` method that takes a file path as input.
	 * 
	 * @param[in]: filePath -- The path to the MMD file to parse.
	 */
	public static MmdDocument parse(String filePath) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			return parse(reader);
		}
	}

	/**
	 * Converts the MMD document to HTML.
	 * @return: A list of strings representing the HTML lines.
	 * @throws: IOException If an I/O error occurs while converting to HTML.
	 */
	public List<String> toHTML() throws IOException {
		List<String> htmlLines = new ArrayList<>();
		try {
			// @todo:handle the header info.
			// Write the body content
			htmlLines.addAll(bodyContent.toHTML());
		} catch (IOException e) {
			throw e; // Re-throw the original exception
		}
		return htmlLines;
	}

	/**
	 * @brief: An overloaded `toHTML` method that writes HTML output directly to a file.
	 * 
	 * @param[in]: filePath -- The path to the output HTML file.
	 */
	public void toHTML(String filePath) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
			// Write the body content
			List<String> htmlLines = bodyContent.toHTML();
			for (String line : htmlLines) {
				writer.write(line);
				writer.newLine();
			}
		}
	}
}
