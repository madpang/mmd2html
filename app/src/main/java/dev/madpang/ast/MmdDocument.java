/**
 * @file: MmdDocument.java
 * @brief: Represents the entire mmd-markup document structure, including the header and body.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-18
 */

package dev.madpang.ast;

import java.io.BufferedReader;
import java.io.IOException;

public class MmdDocument {
	public MmdHeader header = new MmdHeader();
	public MmdSection body = new MmdSection(); // root section (level-1) of the body

	/**
	 * @brief: Parses a MmdDocument from a BufferedReader, with an optional first line being supplied.
	 *
	 * @param[in]: reader The BufferedReader to read the document from.
	 * @param[in]: firstLine The first line, or null to read from the reader.
	 * @return: A MmdDocument object containing the parsed header and body.
	 * @throws: IOException If an I/O error occurs while reading the document.
	 * 
	 * @details: The second argument `firstLine` is useful to allow the caller set a hook by knowing start of parsing.
	 */
	public static MmdDocument parse(BufferedReader reader, String firstLine) throws IOException {
		MmdDocument doc = new MmdDocument();
		try {
			// If firstLine is not provided, read the first line from the reader
			String nextLine = (firstLine != null) ? firstLine : reader.readLine();
			if (nextLine == null || !nextLine.equals("+++ header")) {
				throw new IOException("MMD DOC MUST PROVIDE A <HEADER>, STARTING WITH '+++ header'");
			}
			// Delegate parsing of the header to MmdHeader
			doc.header = MmdHeader.parse(reader, nextLine);
			// After parsing the header, skip potential empty lines
			while ((nextLine = reader.readLine()) != null && nextLine.trim().isEmpty()) {
				// Skip empty lines
			}
			if (nextLine == null || !nextLine.startsWith("# ")) {
				throw new IOException("MMD DOC MUST HAVE A <BODY>, STARTING WITH A LEVEL-1 HEADING, e.g. '# My Heading')");
			}
			// Delegate parsing of the body to MmdSection
			doc.body = MmdSection.parse(reader, nextLine);
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
}
