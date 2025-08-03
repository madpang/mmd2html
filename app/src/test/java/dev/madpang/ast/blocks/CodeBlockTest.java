/**
 * @file: CodeBlockTest.java
 * @brief: Unit tests for CodeBlock class
 * @author: [Claude Sonnet 4 (AI), madpang]
 * @date: [created: 2025-07-11, updated: 2025-08-03]
 */

package dev.madpang.ast.blocks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class CodeBlockTest {

	@Test
	@DisplayName("Should return correct block type")
	public void testGetType() throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader("System.out.println(\"Hello\");\n```"));
		String firstLine = "``` java";

		Optional<IBlock> result = CodeBlock.parse(reader, firstLine);

		assertTrue(result.isPresent());
		assertEquals("code", result.get().getType());
	}

	@Test
	@DisplayName("Should parse simple code block successfully")
	public void testParseSimpleCodeBlock() throws IOException {
		String firstLine = "``` java";
		String content = "System.out.println(\"Hello World!\");\nint x = 42;\n```";
		BufferedReader reader = new BufferedReader(new StringReader(content));
		
		Optional<IBlock> result = CodeBlock.parse(reader, firstLine);
		
		assertTrue(result.isPresent());
		CodeBlock codeBlock = (CodeBlock) result.get();
		assertEquals("java", codeBlock.getCodeType());
	}

	@Test
	@DisplayName("Should parse code block with multiple lines")
	public void testParseMultiLineCodeBlock() throws IOException {
		String firstLine = "``` python";
		String content = "def hello():\n    print(\"Hello\")\n    return True\n\nif __name__ == '__main__':\n    hello()\n```";
		BufferedReader reader = new BufferedReader(new StringReader(content));
		
		Optional<IBlock> result = CodeBlock.parse(reader, firstLine);
		
		assertTrue(result.isPresent());
		CodeBlock codeBlock = (CodeBlock) result.get();
		assertEquals("python", codeBlock.getCodeType());
	}

	@Test
	@DisplayName("Should throw exception for empty code block")
	public void testParseEmptyCodeBlock() throws IOException {
		String firstLine = "``` bash";
		String content = "```";
		BufferedReader reader = new BufferedReader(new StringReader(content));
		
		IOException exception = assertThrows(IOException.class, () -> {
			CodeBlock.parse(reader, firstLine);
		});
		
		assertTrue(exception.getMessage().contains("Empty code block is not allowed"));
	}

	@Test
	@DisplayName("Should fail to parse invalid fence start")
	public void testParseInvalidFenceStart() throws IOException {
		String firstLine = "`` java"; // Missing one `
		BufferedReader reader = new BufferedReader(new StringReader(""));
		
		Optional<IBlock> result = CodeBlock.parse(reader, firstLine);
		
		assertFalse(result.isPresent());
	}

	@Test
	@DisplayName("Should fail to parse fence without language")
	public void testParseFenceWithoutLanguage() throws IOException {
		String firstLine = "```"; // No language specified
		BufferedReader reader = new BufferedReader(new StringReader(""));
		
		Optional<IBlock> result = CodeBlock.parse(reader, firstLine);
		
		assertFalse(result.isPresent());
	}

	@Test
	@DisplayName("Should fail to parse fence with only whitespace language")
	public void testParseFenceWithWhitespaceLanguage() throws IOException {
		String firstLine = "```   "; // Only whitespace after ```
		BufferedReader reader = new BufferedReader(new StringReader(""));
		
		Optional<IBlock> result = CodeBlock.parse(reader, firstLine);
		
		assertFalse(result.isPresent());
	}

	@Test
	@DisplayName("Should throw exception for unterminated code block")
	public void testParseUnterminatedCodeBlock() throws IOException {
		String firstLine = "``` java";
		String content = "System.out.println(\"Hello\");\nint x = 42;"; // No closing ```
		BufferedReader reader = new BufferedReader(new StringReader(content));
		
		IOException exception = assertThrows(IOException.class, () -> {
			CodeBlock.parse(reader, firstLine);
		});
		
		assertTrue(exception.getMessage().contains("Unterminated code fence"));
	}

	@Test
	@DisplayName("Should generate correct HTML output")
	public void testToHTML() throws IOException {
		String firstLine = "``` java";
		String content = "System.out.println(\"Hello & <World>!\");\n```";
		BufferedReader reader = new BufferedReader(new StringReader(content));
		
		Optional<IBlock> result = CodeBlock.parse(reader, firstLine);
		assertTrue(result.isPresent());
		
		List<String> htmlLines = result.get().toHTML();
		
		assertEquals(3, htmlLines.size());
		assertEquals("<pre>", htmlLines.get(0));
		assertEquals("System.out.println(\"Hello &amp; &lt;World&gt;!\");", htmlLines.get(1));
		assertEquals("</pre>", htmlLines.get(2));
	}

	@Test
	@DisplayName("Should generate HTML for multi-line code block")
	public void testToHTMLMultiLine() throws IOException {
		String firstLine = "``` python";
		String content = "def greet(name):\n    return f\"Hello, {name}!\"\n\nprint(greet(\"World\"))\n```";
		BufferedReader reader = new BufferedReader(new StringReader(content));
		
		Optional<IBlock> result = CodeBlock.parse(reader, firstLine);
		assertTrue(result.isPresent());
		
		List<String> htmlLines = result.get().toHTML();
		
		assertEquals(6, htmlLines.size());
		assertEquals("<pre>", htmlLines.get(0));
		assertEquals("def greet(name):", htmlLines.get(1));
		assertEquals("    return f\"Hello, {name}!\"", htmlLines.get(2));
		assertEquals("", htmlLines.get(3)); // Empty line
		assertEquals("print(greet(\"World\"))", htmlLines.get(4));
		assertEquals("</pre>", htmlLines.get(5));
	}

	@Test
	@DisplayName("Should throw exception when converting empty code block to HTML")
	public void testToHTMLEmptyCodeBlock() throws IOException {
		// Create an empty CodeBlock directly using the constructor to test toHTML() behavior
		List<String> emptyContent = new ArrayList<>();
		CodeBlock emptyCodeBlock = new CodeBlock(emptyContent, "java");
		
		IOException exception = assertThrows(IOException.class, () -> {
			emptyCodeBlock.toHTML();
		});
		
		assertTrue(exception.getMessage().contains("CodeBlock contains no lines to convert to HTML"));
	}

	@Test
	@DisplayName("Should handle code block with special HTML characters")
	public void testToHTMLWithSpecialCharacters() throws IOException {
		String firstLine = "``` html";
		String content = "<div class=\"test\">\n    <p>Hello & goodbye</p>\n</div>\n```";
		BufferedReader reader = new BufferedReader(new StringReader(content));
		
		Optional<IBlock> result = CodeBlock.parse(reader, firstLine);
		assertTrue(result.isPresent());
		
		List<String> htmlLines = result.get().toHTML();
		
		assertEquals(5, htmlLines.size());
		assertEquals("<pre>", htmlLines.get(0));
		assertEquals("&lt;div class=\"test\"&gt;", htmlLines.get(1));
		assertEquals("    &lt;p&gt;Hello &amp; goodbye&lt;/p&gt;", htmlLines.get(2));
		assertEquals("&lt;/div&gt;", htmlLines.get(3));
		assertEquals("</pre>", htmlLines.get(4));
	}

	@Test
	@DisplayName("Should parse code block with language containing spaces")
	public void testParseCodeBlockWithSpacesInLanguage() throws IOException {
		String firstLine = "``` c++ with comments";
		String content = "// This is a C++ comment\nint main() { return 0; }\n```";
		BufferedReader reader = new BufferedReader(new StringReader(content));
		
		Optional<IBlock> result = CodeBlock.parse(reader, firstLine);
		
		assertTrue(result.isPresent());
		CodeBlock codeBlock = (CodeBlock) result.get();
		assertEquals("c++ with comments", codeBlock.getCodeType());
	}

	@Test
	@DisplayName("Should verify PARSER static field functionality")
	public void testStaticParser() throws IOException {
		String firstLine = "``` javascript";
		String content = "console.log('Hello World!');\n```";
		BufferedReader reader = new BufferedReader(new StringReader(content));
		
		Optional<IBlock> result = CodeBlock.PARSER.tryParse(reader, firstLine);
		
		assertTrue(result.isPresent());
		assertEquals("code", result.get().getType());
	}
}
