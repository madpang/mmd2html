/**
 * @file: IBlockTest.java
 * @brief: Unit tests for IBlock interface contract
 * @author: Claude Sonnet 4 (AI)
 */

package dev.madpang.ast.blocks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class IBlockTest {

    /**
     * Simple test implementation of IBlock
     */
    private static class TestBlock implements IBlock {
        private final String type;
        private final List<String> htmlContent;
        private final boolean shouldThrowException;

        public TestBlock(String type, List<String> htmlContent) {
            this(type, htmlContent, false);
        }

        public TestBlock(String type, List<String> htmlContent, boolean shouldThrowException) {
            this.type = type;
            this.htmlContent = htmlContent;
            this.shouldThrowException = shouldThrowException;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public List<String> toHTML() throws IOException {
            if (shouldThrowException) {
                throw new IOException("Test exception");
            }
            return new ArrayList<>(htmlContent);
        }
    }

    @Test
    @DisplayName("Should implement getType method correctly")
    public void testGetTypeImplementation() {
        List<String> content = List.of("<p>", "Test content", "</p>");
        TestBlock block = new TestBlock("test", content);
        
        assertEquals("test", block.getType());
    }

    @Test
    @DisplayName("Should implement toHTML method correctly")
    public void testToHTMLImplementation() throws IOException {
        List<String> expectedContent = List.of("<div>", "Test HTML content", "</div>");
        TestBlock block = new TestBlock("test", expectedContent);
        
        List<String> result = block.toHTML();
        
        assertEquals(3, result.size());
        assertEquals("<div>", result.get(0));
        assertEquals("Test HTML content", result.get(1));
        assertEquals("</div>", result.get(2));
    }

    @Test
    @DisplayName("Should handle empty HTML content")
    public void testEmptyHTMLContent() throws IOException {
        List<String> emptyContent = new ArrayList<>();
        TestBlock block = new TestBlock("empty", emptyContent);
        
        List<String> result = block.toHTML();
        
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle single line HTML content")
    public void testSingleLineHTMLContent() throws IOException {
        List<String> singleLineContent = List.of("<span>Single line</span>");
        TestBlock block = new TestBlock("single", singleLineContent);
        
        List<String> result = block.toHTML();
        
        assertEquals(1, result.size());
        assertEquals("<span>Single line</span>", result.get(0));
    }

    @Test
    @DisplayName("Should handle multi-line HTML content")
    public void testMultiLineHTMLContent() throws IOException {
        List<String> multiLineContent = List.of(
            "<article>",
            "  <h1>Title</h1>",
            "  <p>Paragraph content</p>",
            "  <footer>Footer</footer>",
            "</article>"
        );
        TestBlock block = new TestBlock("article", multiLineContent);
        
        List<String> result = block.toHTML();
        
        assertEquals(5, result.size());
        assertEquals("<article>", result.get(0));
        assertEquals("  <h1>Title</h1>", result.get(1));
        assertEquals("  <p>Paragraph content</p>", result.get(2));
        assertEquals("  <footer>Footer</footer>", result.get(3));
        assertEquals("</article>", result.get(4));
    }

    @Test
    @DisplayName("Should propagate IOException from toHTML")
    public void testToHTMLThrowsException() {
        List<String> content = List.of("<p>", "Content", "</p>");
        TestBlock block = new TestBlock("error", content, true);
        
        IOException exception = assertThrows(IOException.class, () -> {
            block.toHTML();
        });
        
        assertEquals("Test exception", exception.getMessage());
    }

    @Test
    @DisplayName("Should verify interface contract with real implementations")
    public void testRealImplementations() throws IOException {
        // Test CodeBlock implements IBlock correctly
        String firstLine = "+++ java";
        String content = "System.out.println(\"Hello\");\n+++";
        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.StringReader(content));
        
        java.util.Optional<IBlock> codeBlockOpt = CodeBlock.parse(reader, firstLine);
        assertTrue(codeBlockOpt.isPresent());
        
        IBlock codeBlock = codeBlockOpt.get();
        assertEquals("code", codeBlock.getType());
        
        List<String> codeHTML = codeBlock.toHTML();
        assertFalse(codeHTML.isEmpty());
        assertTrue(codeHTML.get(0).contains("<pre>"));
        
        // Test ParagraphBlock implements IBlock correctly
        ParagraphBlock paragraphBlock = new ParagraphBlock();
        paragraphBlock.addLine("Test paragraph content");
        
        IBlock paragraphAsIBlock = paragraphBlock;
        assertEquals("paragraph", paragraphAsIBlock.getType());
        
        List<String> paragraphHTML = paragraphAsIBlock.toHTML();
        assertFalse(paragraphHTML.isEmpty());
        assertTrue(paragraphHTML.get(0).contains("<p>"));
    }

    @Test
    @DisplayName("Should handle null return values gracefully")
    public void testNullReturnValues() {
        IBlock nullTypeBlock = new IBlock() {
            @Override
            public String getType() {
                return null;
            }

            @Override
            public List<String> toHTML() throws IOException {
                return null;
            }
        };
        
        assertNull(nullTypeBlock.getType());
        assertThrows(IOException.class, () -> {
            // This would typically cause a NullPointerException in real usage
            List<String> result = nullTypeBlock.toHTML();
            if (result == null) {
                throw new IOException("toHTML returned null");
            }
        });
    }

    @Test
    @DisplayName("Should support polymorphic behavior")
    public void testPolymorphicBehavior() throws IOException {
        // Create a list of different IBlock implementations
        List<IBlock> blocks = new ArrayList<>();
        
        // Add a TestBlock
        blocks.add(new TestBlock("test", List.of("<div>Test</div>")));
        
        // Add a ParagraphBlock
        ParagraphBlock paragraph = new ParagraphBlock();
        paragraph.addLine("Paragraph content");
        blocks.add(paragraph);
        
        // Process all blocks polymorphically
        for (IBlock block : blocks) {
            assertNotNull(block.getType());
            List<String> html = block.toHTML();
            assertNotNull(html);
            assertFalse(html.isEmpty());
        }
        
        // Verify specific behavior
        assertEquals("test", blocks.get(0).getType());
        assertEquals("paragraph", blocks.get(1).getType());
    }

    @Test
    @DisplayName("Should handle HTML content with special characters")
    public void testHTMLContentWithSpecialCharacters() throws IOException {
        List<String> specialContent = List.of(
            "<pre>",
            "Code with &lt;tags&gt; &amp; special chars",
            "</pre>"
        );
        TestBlock block = new TestBlock("special", specialContent);
        
        List<String> result = block.toHTML();
        
        assertEquals(3, result.size());
        assertTrue(result.get(1).contains("&lt;"));
        assertTrue(result.get(1).contains("&gt;"));
        assertTrue(result.get(1).contains("&amp;"));
    }

    @Test
    @DisplayName("Should maintain immutability of returned HTML content")
    public void testHTMLContentImmutability() throws IOException {
        List<String> originalContent = new ArrayList<>();
        originalContent.add("<p>Original content</p>");
        
        TestBlock block = new TestBlock("immutable", originalContent);
        
        List<String> result1 = block.toHTML();
        List<String> result2 = block.toHTML();
        
        // Modify one result
        result1.add("<p>Modified</p>");
        
        // Verify the other result is unaffected
        assertEquals(1, result2.size());
        assertEquals("<p>Original content</p>", result2.get(0));
    }
}
