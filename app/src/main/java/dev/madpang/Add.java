package dev.madpang;

import dev.madpang.ast.*;
import dev.madpang.ast.blocks.RawTextBlock;
import java.io.*;
import java.util.*;

/**
 * AST parser for custom plain text markup.
 * Parses MetaMatter and top-level Sections (level 1 headings).
 */
public class Add {
    public static Document parse(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Document doc = new Document();
        String line;
        boolean inMeta = false;
        MetaMatter meta = new MetaMatter();
        List<Section> sections = new ArrayList<>();
        Section currentSection = null;

        // Step 1: Parse MetaMatter
        if ((line = reader.readLine()) != null && line.trim().equals("+++ header")) {
            inMeta = true;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("+++") && inMeta) {
                    inMeta = false;
                    break;
                }
                parseMetaLine(meta, line);
            }
        } else {
            throw new IOException("MetaMatter must start with '+++ header'");
        }
        doc.header = meta;

        // Step 2: Parse Sections (level 1 headings)
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("# ")) {
                if (currentSection != null) {
                    sections.add(currentSection);
                }
                Heading heading = new Heading(1, line.substring(2).trim());
                currentSection = new Section(heading);
            } else {
                if (currentSection != null) {
                    // For now, just add lines as a single SemanticParagraph
                    if (currentSection.paragraphs.isEmpty()) {
                        currentSection.paragraphs.add(new SemanticParagraph());
                    }
                    // Add as a raw block (to be refined later)
                    currentSection.paragraphs.get(0).blocks.add(new RawTextBlock(line));
                }
            }
        }
        if (currentSection != null) {
            sections.add(currentSection);
        }
        doc.sections = sections;
        return doc;
    }

    private static void parseMetaLine(MetaMatter meta, String line) {
        String trimmed = line.trim();
        if (trimmed.startsWith("@file:")) {
            meta.file = trimmed.substring(6).trim();
        } else if (trimmed.startsWith("@brief:")) {
            meta.brief = trimmed.substring(7).trim();
        } else if (trimmed.startsWith("@title:")) {
            meta.title = trimmed.substring(7).trim();
        } else if (trimmed.startsWith("@author:")) {
            meta.author = trimmed.substring(8).trim();
        } else if (trimmed.startsWith("@date:")) {
            // Dates are listed in the following lines, starting with '-'
            // This will be handled in the main parse loop if needed
        } else if (trimmed.startsWith("- ")) {
            // Date lines (after @date:)
            meta.date.add(trimmed.substring(2).trim());
        } else if (trimmed.startsWith("@version:")) {
            meta.version = trimmed.substring(9).trim();
        }
    }
}
