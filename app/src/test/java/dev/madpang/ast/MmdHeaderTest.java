/**
 * @file: MmdHeaderTest.java
 * @brief: Unit tests for MmdHeader class
 * @author:
 * - GitHub Copilot (AI)
 * - madpang
 * @date: [created: 2025-08-03, updated: 2025-08-03]
 */

package dev.madpang.ast;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;

public class MmdHeaderTest {

	@Test
	@DisplayName("Should parse valid header successfully")
	public void testParseValidHeader() throws IOException {
		String headerContent = "``` header\n" +
			"@file: test-article.txt\n" +
			"@brief: A test article for unit testing\n" +
			"@title: Test Article Title\n" +
			"@author: madpang\n" +
			"@date: [created: 2025-01-01, updated: 2025-01-02]\n" +
			"@version: 1.0.0\n" +
			"```";
		
		BufferedReader reader = new BufferedReader(new StringReader(headerContent));
		
		MmdHeader header = MmdHeader.parse(reader);
		
		assertEquals("test-article.txt", header.metaInfo.get("file"));
		assertEquals("A test article for unit testing", header.metaInfo.get("brief"));
		assertEquals("Test Article Title", header.metaInfo.get("title"));
		assertEquals("madpang", header.metaInfo.get("author"));
		assertEquals("2025-01-01", header.metaInfo.get("date-created"));
		assertEquals("2025-01-02", header.metaInfo.get("date-updated"));
		assertEquals("1.0.0", header.metaInfo.get("version"));
	}

	@Test
	@DisplayName("Should parse header with firstLine parameter")
	public void testParseWithFirstLine() throws IOException {
		String headerContent = "@file: example.txt\n" +
			"@brief: An example document\n" +
			"@title: Example Title\n" +
			"@author: test-author\n" +
			"@date: [created: 2025-05-11, updated: 2025-05-17]\n" +
			"@version: 0.2.0\n" +
			"```";
		
		BufferedReader reader = new BufferedReader(new StringReader(headerContent));
		String firstLine = "``` header";
		
		MmdHeader header = MmdHeader.parse(reader, firstLine);
		
		assertEquals("example.txt", header.metaInfo.get("file"));
		assertEquals("An example document", header.metaInfo.get("brief"));
		assertEquals("Example Title", header.metaInfo.get("title"));
		assertEquals("test-author", header.metaInfo.get("author"));
		assertEquals("2025-05-11", header.metaInfo.get("date-created"));
		assertEquals("2025-05-17", header.metaInfo.get("date-updated"));
		assertEquals("0.2.0", header.metaInfo.get("version"));
	}

	@Test
	@DisplayName("Should throw exception for invalid first line")
	public void testParseInvalidFirstLine() throws IOException {
		String headerContent = "invalid header\n" +
			"@file: test.txt\n" +
			"```";
		
		BufferedReader reader = new BufferedReader(new StringReader(headerContent));
		
		IOException exception = assertThrows(IOException.class, () -> {
			MmdHeader.parse(reader);
		});
		
		assertTrue(exception.getMessage().contains("First line must be '``` header'"));
	}

	@Test
	@DisplayName("Should throw exception for unterminated header block")
	public void testParseUnterminatedHeader() throws IOException {
		String headerContent = "``` header\n" +
			"@file: test.txt\n" +
			"@brief: Test document\n" +
			"@title: Test Title\n" +
			"@author: test-author\n" +
			"@date: [created: 2025-01-01, updated: 2025-01-02]\n" +
			"@version: 1.0.0";
		// Missing closing ```
		
		BufferedReader reader = new BufferedReader(new StringReader(headerContent));
		
		IOException exception = assertThrows(IOException.class, () -> {
			MmdHeader.parse(reader);
		});
		
		assertTrue(exception.getMessage().contains("Header block is not closed properly, expecting '```' to end the header"));
	}

	@Test
	@DisplayName("Should throw exception for missing required fields")
	public void testParseMissingRequiredFields() throws IOException {
		String headerContent = "``` header\n" +
			"@file: test.txt\n" +
			"@brief: Test document\n" +
			"@title: Test Title\n" +
			// Missing @author, @date, and @version
			"```";
		
		BufferedReader reader = new BufferedReader(new StringReader(headerContent));
		
		IOException exception = assertThrows(IOException.class, () -> {
			MmdHeader.parse(reader);
		});
		
		assertTrue(exception.getMessage().contains("Header is missing required fields"));
	}

	@Test
	@DisplayName("Should parse date field with strict regex format")
	public void testParseDateFieldStrictFormat() throws IOException {
		String headerContent = "``` header\n" +
			"@file: date-test.txt\n" +
			"@brief: Testing date parsing\n" +
			"@title: Date Test\n" +
			"@author: tester\n" +
			"@date: [created: 2023-12-25, updated: 2024-01-15]\n" +
			"@version: 2.1.3\n" +
			"```";
		
		BufferedReader reader = new BufferedReader(new StringReader(headerContent));
		
		MmdHeader header = MmdHeader.parse(reader);
		
		assertEquals("2023-12-25", header.metaInfo.get("date-created"));
		assertEquals("2024-01-15", header.metaInfo.get("date-updated"));
	}

	@Test
	@DisplayName("Should ignore invalid date format")
	public void testParseInvalidDateFormat() throws IOException {
		String headerContent = "``` header\n" +
			"@file: invalid-date.txt\n" +
			"@brief: Testing invalid date\n" +
			"@title: Invalid Date Test\n" +
			"@author: tester\n" +
			"@date: [created: 2025-1-1, updated: 2025-1-2]\n" + // Invalid format (missing leading zeros)
			"@version: 1.0.0\n" +
			"```";
		
		BufferedReader reader = new BufferedReader(new StringReader(headerContent));
		
		IOException exception = assertThrows(IOException.class, () -> {
			MmdHeader.parse(reader);
		});
		
		assertTrue(exception.getMessage().contains("Header is missing required fields"));
	}

	@Test
	@DisplayName("Should ignore unknown tags")
	public void testParseIgnoreUnknownTags() throws IOException {
		String headerContent = "``` header\n" +
			"@file: unknown-tags.txt\n" +
			"@brief: Testing unknown tags\n" +
			"@title: Unknown Tags Test\n" +
			"@author: tester\n" +
			"@date: [created: 2025-01-01, updated: 2025-01-02]\n" +
			"@version: 1.0.0\n" +
			"@unknown: this should be ignored\n" +
			"@category: this too\n" +
			"```";
		
		BufferedReader reader = new BufferedReader(new StringReader(headerContent));
		
		MmdHeader header = MmdHeader.parse(reader);
		
		// Should parse successfully and ignore unknown tags
		assertEquals("unknown-tags.txt", header.metaInfo.get("file"));
		assertNull(header.metaInfo.get("unknown"));
		assertNull(header.metaInfo.get("category"));
	}

	@Test
	@DisplayName("Should handle whitespace in field values")
	public void testParseWithWhitespace() throws IOException {
		String headerContent = "``` header\n" +
			"@file:   spaced-file.txt   \n" +
			"@brief:   A document with extra spaces   \n" +
			"@title:   Spaced Title   \n" +
			"@author:   spaced author   \n" +
			"@date:   [created: 2025-01-01, updated: 2025-01-02]   \n" +
			"@version:   1.0.0   \n" +
			"```";
		
		BufferedReader reader = new BufferedReader(new StringReader(headerContent));
		
		MmdHeader header = MmdHeader.parse(reader);
		
		assertEquals("spaced-file.txt", header.metaInfo.get("file"));
		assertEquals("A document with extra spaces", header.metaInfo.get("brief"));
		assertEquals("Spaced Title", header.metaInfo.get("title"));
		assertEquals("spaced author", header.metaInfo.get("author"));
		assertEquals("2025-01-01", header.metaInfo.get("date-created"));
		assertEquals("2025-01-02", header.metaInfo.get("date-updated"));
		assertEquals("1.0.0", header.metaInfo.get("version"));
	}

	@Test
	@DisplayName("Should handle empty field values")
	public void testParseEmptyFieldValues() throws IOException {
		String headerContent = "``` header\n" +
			"@file:\n" + // Empty value
			"@brief: Testing empty fields\n" +
			"@title: Empty Field Test\n" +
			"@author: tester\n" +
			"@date: [created: 2025-01-01, updated: 2025-01-02]\n" +
			"@version: 1.0.0\n" +
			"```";
		
		BufferedReader reader = new BufferedReader(new StringReader(headerContent));
		
		IOException exception = assertThrows(IOException.class, () -> {
			MmdHeader.parse(reader);
		});
		
		assertTrue(exception.getMessage().contains("Header is missing required fields"));
	}

	@Test
	@DisplayName("Should handle malformed meta lines")
	public void testParseMalformedMetaLines() throws IOException {
		String headerContent = "``` header\n" +
			"@file: good-file.txt\n" +
			"@brief: Good brief\n" +
			"@title: Good title\n" +
			"@author: good-author\n" +
			"malformed line without colon\n" + // This line should be ignored
			"@date: [created: 2025-01-01, updated: 2025-01-02]\n" +
			"@version: 1.0.0\n" +
			"```";
		
		BufferedReader reader = new BufferedReader(new StringReader(headerContent));
		
		MmdHeader header = MmdHeader.parse(reader);
		
		// Should parse successfully, ignoring malformed lines
		assertEquals("good-file.txt", header.metaInfo.get("file"));
		assertEquals("Good brief", header.metaInfo.get("brief"));
	}

	@Test
	@DisplayName("Should validate date format with wrong bracket format")
	public void testParseWrongDateBracketFormat() throws IOException {
		String headerContent = "``` header\n" +
			"@file: bracket-test.txt\n" +
			"@brief: Testing bracket format\n" +
			"@title: Bracket Test\n" +
			"@author: tester\n" +
			"@date: (created: 2025-01-01, updated: 2025-01-02)\n" + // Wrong brackets
			"@version: 1.0.0\n" +
			"```";
		
		BufferedReader reader = new BufferedReader(new StringReader(headerContent));
		
		IOException exception = assertThrows(IOException.class, () -> {
			MmdHeader.parse(reader);
		});
		
		assertTrue(exception.getMessage().contains("Header is missing required fields"));
	}

	@Test
	@DisplayName("Should validate date format with wrong separator")
	public void testParseWrongDateSeparator() throws IOException {
		String headerContent = "``` header\n" +
			"@file: separator-test.txt\n" +
			"@brief: Testing separator format\n" +
			"@title: Separator Test\n" +
			"@author: tester\n" +
			"@date: [created: 2025-01-01; updated: 2025-01-02]\n" + // Wrong separator (semicolon)
			"@version: 1.0.0\n" +
			"```";
		
		BufferedReader reader = new BufferedReader(new StringReader(headerContent));
		
		IOException exception = assertThrows(IOException.class, () -> {
			MmdHeader.parse(reader);
		});
		
		assertTrue(exception.getMessage().contains("Header is missing required fields"));
	}
}
