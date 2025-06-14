package dev.madpang;

import dev.madpang.ast.*;
import dev.madpang.ast.blocks.*;
import java.io.*;

public class ParseDemo {
    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java dev.madpang.ParseDemo <input-file>");
            System.exit(1);
        }
        String file = args[0];
        try (InputStream in = new FileInputStream(file)) {
            Document doc = Add.parse(in);
            printAST(doc);
        }
    }

    private static void printAST(Document doc) {
        System.out.println("=== MetaMatter ===");
        System.out.println("file: " + doc.header.file);
        System.out.println("brief: " + doc.header.brief);
        System.out.println("title: " + doc.header.title);
        System.out.println("author: " + doc.header.author);
        System.out.println("date: " + doc.header.date);
        System.out.println("version: " + doc.header.version);
        System.out.println();
        System.out.println("=== Sections ===");
        for (Section sec : doc.sections) {
            System.out.println("# " + sec.heading.text);
            for (SemanticParagraph para : sec.paragraphs) {
                for (IBlock block : para.blocks) {
                    if (block instanceof RawTextBlock) {
                        System.out.println(((RawTextBlock) block).text);
                    } else {
                        System.out.println(block);
                    }
                }
                System.out.println();
            }
            System.out.println("----------------");
        }
    }
}
