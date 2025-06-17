/**
 * @file: App.java
 * @brief: Main application class for the MMD to HTML parser.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-14
 */

package dev.madpang;

import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Scanner;
import dev.madpang.ast.MmdDocument;

public class App {
	public static void main(String[] args) throws Exception {
		/// Argument handling
		String filePath;
		if (args.length < 1) {
			// Interactive mode: prompt for file path
			try (Scanner scanner = new Scanner(System.in)) {
				// Print welcome message
				System.out.println("////////////////////////////////");
				System.out.println("// MMD to HTML parser started. /");
				System.out.println("////////////////////////////////");
				// Debug information
				System.out.println("Working directory: " + System.getProperty("user.dir"));
				// Get post file path				
				System.out.print("Enter path to input file: ");
				filePath = scanner.nextLine();
			}
		} else {
			filePath = args[0];
		}
		/// Parse the file
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			MmdDocument doc = MmdDocument.parse(reader);
			// Print the AST
			doc.header.print();
			doc.body.print();
		} catch (Exception e) {
			System.err.println("Error parsing file: " + e.getMessage());
			e.printStackTrace();
		}
	}
}
