/**
 * @file: ParagraphBlockTest.java
 * @brief: Unit tests for ParagraphBlock class
 * @author:
 * - Claude Sonnet 4 (AI)
 * - madpang
 * @date:
 * - created on 2025-07-11
 * - updated on 2025-07-11
 */

package dev.madpang.ast.blocks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;

public class ParagraphBlockTest {

	private ParagraphBlock paragraphBlock;

	@BeforeEach
	public void setUp() {
		paragraphBlock = new ParagraphBlock();
	}

	@Test
	@DisplayName("Should return correct block type")
	public void testGetType() {
		assertEquals("paragraph", paragraphBlock.getType());
	}

	@Test
	@DisplayName("Should add single line successfully")
	public void testAddSingleLine() throws IOException {
		paragraphBlock.addLine("This is a test paragraph.");
		
		List<String> htmlLines = paragraphBlock.toHTML();
		
		assertEquals(3, htmlLines.size());
		assertEquals("<p>", htmlLines.get(0));
		assertEquals("This is a test paragraph.", htmlLines.get(1));
		assertEquals("</p>", htmlLines.get(2));
	}

	@Test
	@DisplayName("Should add multiple lines successfully")
	public void testAddMultipleLines() throws IOException {
		paragraphBlock.addLine("First line of the paragraph.");
		paragraphBlock.addLine("Second line of the paragraph.");
		paragraphBlock.addLine("Third line of the paragraph.");
		
		List<String> htmlLines = paragraphBlock.toHTML();
		
		assertEquals(5, htmlLines.size());
		assertEquals("<p>", htmlLines.get(0));
		assertEquals("First line of the paragraph.", htmlLines.get(1));
		assertEquals("Second line of the paragraph.", htmlLines.get(2));
		assertEquals("Third line of the paragraph.", htmlLines.get(3));
		assertEquals("</p>", htmlLines.get(4));
	}

	@Test
	@DisplayName("Should throw exception when adding null line")
	public void testAddNullLine() {
		IOException exception = assertThrows(IOException.class, () -> {
			paragraphBlock.addLine(null);
		});
		
		assertTrue(exception.getMessage().contains("HTML paragraph cannot contain blank or null lines"));
	}

	@Test
	@DisplayName("Should throw exception when adding empty line")
	public void testAddEmptyLine() {
		IOException exception = assertThrows(IOException.class, () -> {
			paragraphBlock.addLine("");
		});
		
		assertTrue(exception.getMessage().contains("HTML paragraph cannot contain blank or null lines"));
	}

	@Test
	@DisplayName("Should throw exception when adding whitespace-only line")
	public void testAddWhitespaceOnlyLine() {
		IOException exception = assertThrows(IOException.class, () -> {
			paragraphBlock.addLine("   \t  ");
		});
		
		assertTrue(exception.getMessage().contains("HTML paragraph cannot contain blank or null lines"));
	}

	@Test
	@DisplayName("Should throw exception when converting empty paragraph to HTML")
	public void testToHTMLEmptyParagraph() {
		IOException exception = assertThrows(IOException.class, () -> {
			paragraphBlock.toHTML();
		});
		
		assertTrue(exception.getMessage().contains("ParagraphBlock contains no lines to convert to HTML"));
	}

	@Test
	@DisplayName("Should escape HTML special characters")
	public void testToHTMLWithSpecialCharacters() throws IOException {
		paragraphBlock.addLine("This contains <tags> & special characters.");
		paragraphBlock.addLine("Another line with > and < symbols.");
		
		List<String> htmlLines = paragraphBlock.toHTML();
		
		assertEquals(4, htmlLines.size());
		assertEquals("<p>", htmlLines.get(0));
		assertEquals("This contains &lt;tags&gt; &amp; special characters.", htmlLines.get(1));
		assertEquals("Another line with &gt; and &lt; symbols.", htmlLines.get(2));
		assertEquals("</p>", htmlLines.get(3));
	}

	@Test
	@DisplayName("Should handle lines with only valid content")
	public void testAddLineWithValidContent() throws IOException {
		paragraphBlock.addLine("Valid content");
		paragraphBlock.addLine("Another valid line");
		
		List<String> htmlLines = paragraphBlock.toHTML();
		
		assertEquals(4, htmlLines.size());
		assertEquals("<p>", htmlLines.get(0));
		assertEquals("Valid content", htmlLines.get(1));
		assertEquals("Another valid line", htmlLines.get(2));
		assertEquals("</p>", htmlLines.get(3));
	}

	@Test
	@DisplayName("Should handle line with leading and trailing spaces")
	public void testAddLineWithSpaces() throws IOException {
		paragraphBlock.addLine("  Content with spaces  ");
		
		List<String> htmlLines = paragraphBlock.toHTML();
		
		assertEquals(3, htmlLines.size());
		assertEquals("<p>", htmlLines.get(0));
		assertEquals("  Content with spaces  ", htmlLines.get(1));
		assertEquals("</p>", htmlLines.get(2));
	}

	@Test
	@DisplayName("Should handle unicode characters")
	public void testAddLineWithUnicodeCharacters() throws IOException {
		paragraphBlock.addLine("Unicode test: 你好 🌟 café naïve");
		
		List<String> htmlLines = paragraphBlock.toHTML();
		
		assertEquals(3, htmlLines.size());
		assertEquals("<p>", htmlLines.get(0));
		assertEquals("Unicode test: 你好 🌟 café naïve", htmlLines.get(1));
		assertEquals("</p>", htmlLines.get(2));
	}

	@Test
	@DisplayName("Should handle long paragraphs")
	public void testLongParagraph() throws IOException {
		StringBuilder longLine = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			longLine.append("word ");
		}
		
		paragraphBlock.addLine(longLine.toString());
		
		List<String> htmlLines = paragraphBlock.toHTML();
		
		assertEquals(3, htmlLines.size());
		assertEquals("<p>", htmlLines.get(0));
		assertTrue(htmlLines.get(1).length() == 5000); // 1000 words, each 5 characters;
		assertEquals("</p>", htmlLines.get(2));
	}

	@Test
	@DisplayName("Should maintain line order")
	public void testLineOrder() throws IOException {
		paragraphBlock.addLine("First line");
		paragraphBlock.addLine("Second line");
		paragraphBlock.addLine("Third line");
		paragraphBlock.addLine("Fourth line");
		
		List<String> htmlLines = paragraphBlock.toHTML();
		
		assertEquals(6, htmlLines.size());
		assertEquals("<p>", htmlLines.get(0));
		assertEquals("First line", htmlLines.get(1));
		assertEquals("Second line", htmlLines.get(2));
		assertEquals("Third line", htmlLines.get(3));
		assertEquals("Fourth line", htmlLines.get(4));
		assertEquals("</p>", htmlLines.get(5));
	}
}
