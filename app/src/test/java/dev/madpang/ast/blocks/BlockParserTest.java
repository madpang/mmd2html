/**
 * @file: BlockParserTest.java
 * @brief: Unit tests for BlockParser functional interface
 * @author: Claude Sonnet 4 (AI)
 */

package dev.madpang.ast.blocks;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

public class BlockParserTest {

    /**
     * Mock implementation of IBlock for testing
     */
    private static class MockBlock implements IBlock {
        private final String type;
        private final String content;

        public MockBlock(String type, String content) {
            this.type = type;
            this.content = content;
        }

        @Override
        public String getType() {
            return type;
        }

        @Override
        public List<String> toHTML() throws IOException {
            List<String> result = new ArrayList<>();
            result.add("<" + type + ">");
            result.add(content);
            result.add("</" + type + ">");
            return result;
        }
    }

    @Test
    @DisplayName("Should create functional interface implementation successfully")
    public void testFunctionalInterfaceImplementation() throws IOException {
        // Create a simple parser that always returns a mock block
        BlockParser parser = (reader, firstLine) -> {
            if (firstLine.startsWith("MOCK")) {
                return Optional.of(new MockBlock("mock", "test content"));
            }
            return Optional.empty();
        };

        BufferedReader reader = new BufferedReader(new StringReader(""));
        Optional<IBlock> result = parser.tryParse(reader, "MOCK test");
        
        assertTrue(result.isPresent());
        assertEquals("mock", result.get().getType());
    }

    @Test
    @DisplayName("Should return empty optional when parser doesn't match")
    public void testParserNotMatching() throws IOException {
        BlockParser parser = (reader, firstLine) -> {
            if (firstLine.startsWith("MOCK")) {
                return Optional.of(new MockBlock("mock", "test content"));
            }
            return Optional.empty();
        };

        BufferedReader reader = new BufferedReader(new StringReader(""));
        Optional<IBlock> result = parser.tryParse(reader, "NOT_MOCK test");
        
        assertFalse(result.isPresent());
    }

    @Test
    @DisplayName("Should handle parser that throws IOException")
    public void testParserThrowingException() {
        BlockParser parser = (reader, firstLine) -> {
            throw new IOException("Test exception");
        };

        BufferedReader reader = new BufferedReader(new StringReader(""));
        
        IOException exception = assertThrows(IOException.class, () -> {
            parser.tryParse(reader, "test");
        });
        
        assertEquals("Test exception", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle parser that reads from BufferedReader")
    public void testParserReadingFromReader() throws IOException {
        BlockParser parser = (reader, firstLine) -> {
            if (firstLine.equals("READ_NEXT")) {
                String nextLine = reader.readLine();
                return Optional.of(new MockBlock("reader", nextLine != null ? nextLine : "empty"));
            }
            return Optional.empty();
        };

        BufferedReader reader = new BufferedReader(new StringReader("content from reader\n"));
        Optional<IBlock> result = parser.tryParse(reader, "READ_NEXT");
        
        assertTrue(result.isPresent());
        assertEquals("reader", result.get().getType());
        
        List<String> html = result.get().toHTML();
        assertEquals("content from reader", html.get(1));
    }

    @Test
    @DisplayName("Should handle parser that processes multi-line content")
    public void testParserWithMultiLineContent() throws IOException {
        BlockParser parser = (reader, firstLine) -> {
            if (firstLine.equals("MULTI_LINE")) {
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null && !line.equals("END")) {
                    content.append(line).append(" ");
                }
                return Optional.of(new MockBlock("multiline", content.toString().trim()));
            }
            return Optional.empty();
        };

        String input = "line 1\nline 2\nline 3\nEND\n";
        BufferedReader reader = new BufferedReader(new StringReader(input));
        Optional<IBlock> result = parser.tryParse(reader, "MULTI_LINE");
        
        assertTrue(result.isPresent());
        assertEquals("multiline", result.get().getType());
        
        List<String> html = result.get().toHTML();
        assertEquals("line 1 line 2 line 3", html.get(1));
    }

    @Test
    @DisplayName("Should verify CodeBlock.PARSER implements BlockParser")
    public void testCodeBlockParserImplementation() throws IOException {
        assertTrue(CodeBlock.PARSER instanceof BlockParser);
        
        String content = "System.out.println(\"test\");\n+++";
        BufferedReader reader = new BufferedReader(new StringReader(content));
        Optional<IBlock> result = CodeBlock.PARSER.tryParse(reader, "+++ java");
        
        assertTrue(result.isPresent());
        assertEquals("code", result.get().getType());
    }

    @Test
    @DisplayName("Should handle parser composition")
    public void testParserComposition() throws IOException {
        // Create a composite parser that tries multiple parsers
        BlockParser compositeParser = (reader, firstLine) -> {
            // Try first parser
            Optional<IBlock> result = CodeBlock.PARSER.tryParse(reader, firstLine);
            if (result.isPresent()) {
                return result;
            }
            
            // Reset reader for second attempt (in real scenario, this would be handled differently)
            if (firstLine.startsWith("FALLBACK")) {
                return Optional.of(new MockBlock("fallback", "fallback content"));
            }
            
            return Optional.empty();
        };

        // Test with code block
        BufferedReader reader1 = new BufferedReader(new StringReader("test code\n+++"));
        Optional<IBlock> result1 = compositeParser.tryParse(reader1, "+++ java");
        assertTrue(result1.isPresent());
        assertEquals("code", result1.get().getType());
        
        // Test with fallback
        BufferedReader reader2 = new BufferedReader(new StringReader(""));
        Optional<IBlock> result2 = compositeParser.tryParse(reader2, "FALLBACK test");
        assertTrue(result2.isPresent());
        assertEquals("fallback", result2.get().getType());
    }

    @Test
    @DisplayName("Should handle null parameters gracefully")
    public void testParserWithNullParameters() throws IOException {
        BlockParser parser = (reader, firstLine) -> {
            if (firstLine == null) {
                throw new IllegalArgumentException("firstLine cannot be null");
            }
            if (reader == null) {
                throw new IllegalArgumentException("reader cannot be null");
            }
            return Optional.empty();
        };

        // Test with null firstLine
        BufferedReader reader = new BufferedReader(new StringReader(""));
        assertThrows(IllegalArgumentException.class, () -> {
            parser.tryParse(reader, null);
        });

        // Test with null reader
        assertThrows(IllegalArgumentException.class, () -> {
            parser.tryParse(null, "test");
        });
    }
}
