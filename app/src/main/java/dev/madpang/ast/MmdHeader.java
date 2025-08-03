/**
 * @file: MmdHeader.java
 * @brief: Represents the header of a MMD document, which includes meta info. for the document.
 * @author: madpang
 * @date: [created: 2025-06-09, updated: 2025-08-03]
 */

package dev.madpang.ast;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class MmdHeader {
	/**
	 * @note:
	 * A MMD doc must have a header block for meta information, which has the fields of ["file", "brief", "title", "author", "date-created", "date-updated", and "version"].
	 * 
	 * @details:
	 * A real MMD header block looks like
	 * ----------------------------------------------------------------
	 * |                                                              |
	 * | ``` header                                                   | <- 1st line
	 * | @file: blank-article.txt                                     |
	 * | @brief: A blank article serving as a boilerplate.            |
	 * | @title: A wonderful article                                  |
	 * | @author: madpang                                             |
	 * | @date: [created: 2025-05-11, updated: 2025-05-17]            |
	 * | @version: 0.2.0                                              |
	 * | ```                                                          |
	 * |                                                              |
	 * ----------------------------------------------------------------
	 *   |
	 *   v
	 *  1st column, start with no space before '@'
	 */
	public Map<String, String> metaInfo = new HashMap<>();

	/**
	 * @brief: Parses a MmdHeader block from the reader. Assumes the first line is '``` header'.
	 */
	public static MmdHeader parse(BufferedReader reader, String firstLine) throws IOException {
		MmdHeader header = new MmdHeader();
		try {
			// [1] If firstLine is not provided, read the first line from the reader
			String currentLine = (firstLine != null) ? firstLine : reader.readLine();
			if (currentLine == null || !currentLine.equals("``` header")) {
				throw new IOException("First line must be '``` header'");
			}
			// [2] Start parsing the header block
			boolean inHeader = true;
			Pattern metaPattern = Pattern.compile("^@([a-z]+):(.*)$");
			Matcher metaMatcher;
			String metaLine;
			while ((metaLine = reader.readLine()) != null) {
				if (metaLine.equals("```")) {
					inHeader = false; // End of header block
					break;
				}
				metaMatcher = metaPattern.matcher(metaLine);
				if (metaMatcher.matches()) {
					String tag = metaMatcher.group(1);
					String value = metaMatcher.group(2);
					switch (tag) {
						case "file":
							header.metaInfo.put("file", value.trim());
							break;
						case "brief":
							header.metaInfo.put("brief", value.trim());
							break;
						case "title":
							header.metaInfo.put("title", value.trim());
							break;
						case "author":
							header.metaInfo.put("author", value.trim());
							break;
						case "date":
							// @note: date field now uses format: [created: YYYY-MM-DD, updated: YYYY-MM-DD]
							String dateValue = value.trim();
							Pattern datePattern = Pattern.compile("^\\[created: (\\d{4}-\\d{2}-\\d{2}), updated: (\\d{4}-\\d{2}-\\d{2})\\]$");
							Matcher dateMatcher = datePattern.matcher(dateValue);
							if (dateMatcher.matches()) {
								String createdDate = dateMatcher.group(1);
								String updatedDate = dateMatcher.group(2);
								header.metaInfo.put("date-created", createdDate);
								header.metaInfo.put("date-updated", updatedDate);
							}
							break;
						case "version":
							header.metaInfo.put("version", value.trim());
							break;
						default:
							// Ignore unknown tags
							break;
					}
				}
			}
			if (inHeader) {
				throw new IOException("Header block is not closed properly, expecting '```' to end the header.");
			}
			// [3] Perform self-validation to ensure all required fields are present
			if (!header.selfValidation()) {
				throw new IOException("Header is missing required fields.");
			}
		} catch (IOException e) {
			throw e; // Re-throw the original exception
		}
		return header;
	}

	/**
	 * @brief: An overloaded `parse` method, with a a single argument.
	 */
	public static MmdHeader parse(BufferedReader reader) throws IOException {
		return parse(reader, null);
	}

	/**
	 * @brief: A print method to display the header information.
	 */
	public void print() {
		if (selfValidation()) {
			System.out.println("===     MMD Header Block    ===");
			for (Map.Entry<String, String> entry : metaInfo.entrySet()) {
				System.out.printf("%s: %s%n", entry.getKey(), entry.getValue());
			}
			System.out.println("--------------------------------");
		} else {
			System.out.println("[WARNING] MMD Header Block is NOT valid!");
		}
	}

	/**
	 * @brief: A self-validation method to ensure the header contains all required fields.
	 */
	private Boolean selfValidation() {
		String[] requiredFields = {
			"file", "brief", "title", "author",
			"date-created", "date-updated", "version"
		};
		// Check if all required fields are present
		boolean isValid = true;
		for (String field : requiredFields) {
			if (!metaInfo.containsKey(field) || metaInfo.get(field).isEmpty()) {
				isValid = false; // Validation failed
				break;
			}
		}
		return isValid;
	}
}
