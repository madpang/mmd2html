/**
 * @file: BlocksIntegrationTest.java
 * @brief: Integration tests for the blocks package
 * @author: Claude Sonnet 4 (AI)
 */

package dev.madpang.ast.blocks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

public class BlocksIntegrationTest {

    @Test
    @DisplayName("Should process multiple block types in sequence")
    public void testMultipleBlockTypes() throws IOException {
        List<IBlock> blocks = new ArrayList<>();
        
        // Create a CodeBlock
        String codeContent = "public void hello() {\n    System.out.println(\"Hello\");\n}\n+++";
        BufferedReader codeReader = new BufferedReader(new StringReader(codeContent));
        Optional<IBlock> codeBlock = CodeBlock.parse(codeReader, "+++ java");
        assertTrue(codeBlock.isPresent());
        blocks.add(codeBlock.get());
        
        // Create a ParagraphBlock
        ParagraphBlock paragraph = new ParagraphBlock();
        paragraph.addLine("This is a paragraph that follows the code block.");
        paragraph.addLine("It contains multiple lines of text.");
        blocks.add(paragraph);
        
        // Verify all blocks
        assertEquals(2, blocks.size());
        assertEquals("code", blocks.get(0).getType());
        assertEquals("paragraph", blocks.get(1).getType());
        
        // Generate HTML for all blocks
        List<String> allHTML = new ArrayList<>();
        for (IBlock block : blocks) {
            allHTML.addAll(block.toHTML());
        }
        
        assertTrue(allHTML.size() > 5); // Should have content from both blocks
        assertTrue(allHTML.stream().anyMatch(line -> line.contains("<pre>")));
        assertTrue(allHTML.stream().anyMatch(line -> line.contains("<p>")));
    }

    @Test
    @DisplayName("Should handle parser chain correctly")
    public void testParserChain() throws IOException {
        // Test that parsers can be chained to handle different block types
        List<BlockParser> parsers = List.of(CodeBlock.PARSER);
        
        // Test successful parsing
        String codeContent = "System.out.println(\"test\");\n+++";
        BufferedReader reader1 = new BufferedReader(new StringReader(codeContent));
        
        Optional<IBlock> result = Optional.empty();
        for (BlockParser parser : parsers) {
            result = parser.tryParse(reader1, "+++ java");
            if (result.isPresent()) {
                break;
            }
        }
        
        assertTrue(result.isPresent());
        assertEquals("code", result.get().getType());
        
        // Test unsuccessful parsing (should return empty)
        BufferedReader reader2 = new BufferedReader(new StringReader(""));
        
        Optional<IBlock> result2 = Optional.empty();
        for (BlockParser parser : parsers) {
            result2 = parser.tryParse(reader2, "not a code block");
            if (result2.isPresent()) {
                break;
            }
        }
        
        assertFalse(result2.isPresent());
    }

    @Test
    @DisplayName("Should generate valid HTML structure")
    public void testHTMLStructureValidity() throws IOException {
        // Create various blocks and verify their HTML output structure
        
        // Test CodeBlock HTML structure
        String codeContent = "console.log('test');\n+++";
        BufferedReader codeReader = new BufferedReader(new StringReader(codeContent));
        Optional<IBlock> codeBlock = CodeBlock.parse(codeReader, "+++ javascript");
        assertTrue(codeBlock.isPresent());
        
        List<String> codeHTML = codeBlock.get().toHTML();
        assertEquals("<pre>", codeHTML.get(0));
        assertEquals("</pre>", codeHTML.get(codeHTML.size() - 1));
        
        // Test ParagraphBlock HTML structure
        ParagraphBlock paragraph = new ParagraphBlock();
        paragraph.addLine("Test paragraph content");
        
        List<String> paragraphHTML = paragraph.toHTML();
        assertEquals("<p>", paragraphHTML.get(0));
        assertEquals("</p>", paragraphHTML.get(paragraphHTML.size() - 1));
    }

    @Test
    @DisplayName("Should handle edge cases consistently across blocks")
    public void testEdgeCasesConsistency() throws IOException {
        // Test HTML escaping consistency
        
        // Test CodeBlock with special characters
        String codeContent = "if (x < 5 && y > 3) {\n    print(\"<test>\");\n}\n+++";
        BufferedReader codeReader = new BufferedReader(new StringReader(codeContent));
        Optional<IBlock> codeBlock = CodeBlock.parse(codeReader, "+++ pseudocode");
        assertTrue(codeBlock.isPresent());
        
        List<String> codeHTML = codeBlock.get().toHTML();
        assertTrue(codeHTML.stream().anyMatch(line -> line.contains("&lt;") && line.contains("&gt;")));
        
        // Test ParagraphBlock with special characters
        ParagraphBlock paragraph = new ParagraphBlock();
        paragraph.addLine("This contains <tags> & special characters > like these.");
        
        List<String> paragraphHTML = paragraph.toHTML();
        assertTrue(paragraphHTML.stream().anyMatch(line -> 
            line.contains("&lt;") && line.contains("&gt;") && line.contains("&amp;")));
    }

    @Test
    @DisplayName("Should maintain type consistency")
    public void testTypeConsistency() throws IOException {
        // Verify that block types are consistent with their constants
        assertEquals("code", CodeBlock.BLOCK_TYPE);
        assertEquals("paragraph", ParagraphBlock.BLOCK_TYPE);
        
        // Test actual instances return correct types
        String codeContent = "test code\n+++";
        BufferedReader codeReader = new BufferedReader(new StringReader(codeContent));
        Optional<IBlock> codeBlock = CodeBlock.parse(codeReader, "+++ test");
        assertTrue(codeBlock.isPresent());
        assertEquals(CodeBlock.BLOCK_TYPE, codeBlock.get().getType());
        
        ParagraphBlock paragraph = new ParagraphBlock();
        paragraph.addLine("test paragraph");
        assertEquals(ParagraphBlock.BLOCK_TYPE, paragraph.getType());
    }

    @Test
    @DisplayName("Should handle complex real-world content")
    public void testRealWorldContent() throws IOException {
        // Test with more realistic content that might appear in actual usage
        
        // Complex code block
        String complexCode = 
            "/**\n" +
            " * Complex Java method with annotations\n" +
            " */\n" +
            "@Override\n" +
            "public List<String> processData(String input) throws IOException {\n" +
            "    if (input == null || input.trim().isEmpty()) {\n" +
            "        throw new IllegalArgumentException(\"Input cannot be null or empty\");\n" +
            "    }\n" +
            "    \n" +
            "    List<String> results = new ArrayList<>();\n" +
            "    String[] lines = input.split(\"\\n\");\n" +
            "    \n" +
            "    for (String line : lines) {\n" +
            "        if (line.contains(\"<important>\")) {\n" +
            "            results.add(escapeHTML(line));\n" +
            "        }\n" +
            "    }\n" +
            "    \n" +
            "    return results;\n" +
            "}\n" +
            "+++";
        
        BufferedReader complexCodeReader = new BufferedReader(new StringReader(complexCode));
        Optional<IBlock> complexCodeBlock = CodeBlock.parse(complexCodeReader, "+++ java");
        assertTrue(complexCodeBlock.isPresent());
        
        List<String> complexHTML = complexCodeBlock.get().toHTML();
        assertTrue(complexHTML.size() > 10); // Should have many lines
        assertTrue(complexHTML.stream().anyMatch(line -> line.contains("&lt;important&gt;")));
        
        // Complex paragraph
        ParagraphBlock complexParagraph = new ParagraphBlock();
        complexParagraph.addLine("This is a complex paragraph that discusses various programming concepts.");
        complexParagraph.addLine("It mentions HTML tags like <div>, <span>, and <p> elements.");
        complexParagraph.addLine("It also references code symbols like && (logical AND) and >> (bit shift).");
        complexParagraph.addLine("Finally, it includes some special characters: ©, ®, and ™ symbols.");
        
        List<String> complexParagraphHTML = complexParagraph.toHTML();
        assertEquals(6, complexParagraphHTML.size()); // <p> + 4 lines + </p>
        assertTrue(complexParagraphHTML.get(1).contains("programming concepts"));
        assertTrue(complexParagraphHTML.get(2).contains("&lt;div&gt;"));
    }

    @Test
    @DisplayName("Should handle error conditions gracefully across blocks")
    public void testErrorHandlingConsistency() {
        // Test that all block types handle errors consistently
        
        // Test empty content errors
        ParagraphBlock emptyParagraph = new ParagraphBlock();
        IOException paragraphException = assertThrows(IOException.class, () -> {
            emptyParagraph.toHTML();
        });
        assertTrue(paragraphException.getMessage().contains("no lines"));
        
        // Test invalid input errors
        assertThrows(IOException.class, () -> {
            ParagraphBlock paragraph = new ParagraphBlock();
            paragraph.addLine(null);
        });
        
        assertThrows(IOException.class, () -> {
            ParagraphBlock paragraph = new ParagraphBlock();
            paragraph.addLine("");
        });
    }

    @Test
    @DisplayName("Should support extensibility patterns")
    public void testExtensibilityPatterns() throws IOException {
        // Test that the design supports adding new block types
        
        // Create a custom block type (hypothetical)
        IBlock customBlock = new IBlock() {
            @Override
            public String getType() {
                return "custom";
            }

            @Override
            public List<String> toHTML() throws IOException {
                return List.of("<custom>", "Custom content", "</custom>");
            }
        };
        
        // Verify it works with the existing interface
        assertEquals("custom", customBlock.getType());
        List<String> customHTML = customBlock.toHTML();
        assertEquals(3, customHTML.size());
        assertEquals("<custom>", customHTML.get(0));
        
        // Test that it can be used polymorphically with existing blocks
        List<IBlock> mixedBlocks = new ArrayList<>();
        mixedBlocks.add(customBlock);
        
        ParagraphBlock paragraph = new ParagraphBlock();
        paragraph.addLine("Regular paragraph");
        mixedBlocks.add(paragraph);
        
        // Process all blocks uniformly
        for (IBlock block : mixedBlocks) {
            assertNotNull(block.getType());
            List<String> html = block.toHTML();
            assertFalse(html.isEmpty());
        }
    }
}
