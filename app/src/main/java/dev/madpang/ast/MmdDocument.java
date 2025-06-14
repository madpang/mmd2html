/**
 * @file: MmdDocument.java
 * @brief: Represents the entire mmd-markup document structure, including the header and body.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-13
 */

package dev.madpang.ast;

import java.io.BufferedReader;
import java.io.IOException;

public class MmdDocument {
	public MmdHeader header = new MmdHeader();
	public MmdBody body = new MmdBody();

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
			String headerLine = (firstLine != null) ? firstLine : reader.readLine();
			if (headerLine == null || !headerLine.equals("+++ header")) {
				throw new IOException("MMD DOC MUST PROVIDE A HEADER BLOCK, STARTING WITH '+++ header'");
			}
			// Delegate parsing of the header to MmdHeader
			doc.header = MmdHeader.parse(reader, headerLine);
			// After the header parsing, continue delegating to MmdBody
			doc.body = MmdBody.parse(reader);
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
