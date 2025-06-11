/**
 * @file: App.java
 * @brief: Main application class for the MMD to HTML parser.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-09
 */

package dev.madpang;

import java.io.FileInputStream;
import java.util.Scanner;
import dev.madpang.ast.*;
import dev.madpang.ast.blocks.*;

public class App {
	public static void main(String[] args) throws Exception {
		/// Print welcome message
		System.out.print("MMD to HTML parser started.\n");
		/// Get post file path
		String filePath;
		if (args.length < 1) {
			try (Scanner scanner = new Scanner(System.in)) {
				System.out.print("Enter path to input file: ");
				filePath = scanner.nextLine();
			}
		} else {
			filePath = args[0];
		}
		/// Parse the file
		try (FileInputStream in = new java.io.FileInputStream(filePath)) {
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
