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
			Document doc = Mmd2AstParser.parse(in);
			printAST(doc);
		}
	}

	private static void printAST(Document doc) {
		System.out.println("=== MetaMatter ===");
		System.out.println("file: " + doc.metaMatter.file);
		System.out.println("brief: " + doc.metaMatter.brief);
		System.out.println("title: " + doc.metaMatter.title);
		System.out.println("author: " + doc.metaMatter.author);
		System.out.println("date: " + doc.metaMatter.date);
		System.out.println("version: " + doc.metaMatter.version);
		System.out.println();
		System.out.println("=== Sections ===");
		for (Section sec : doc.sectionList) {
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
