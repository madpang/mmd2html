/**
 * @file Converter.java
 * @brief Main class for the MMD to HTML conversion app.
 * @author [madpang, t-tang-rfc]
 * @date [created: 2025-06-09, updated: 2026-10-05]
 * 
 * @details This class serves as the entry point for the MMD to HTML conversion application.
 * @details It is a wrapper which calls MmdDocSection to parse the MMD document and converts it to HTML.
 * @details It also provides an interactive mode if no arguments are provided.
 * 
**/

package mmd2html;

import java.util.Scanner;

import mmd2html.ast.MmdDocSection;

public class Converter {
	public static void main(String[] args) throws Exception {
		/// Argument parsing
		String inputFilePath = null;
		String outputFilePath = null;
	
		if (args.length < 1) {
			// Interactive mode: prompt for file paths
			try (Scanner scanner = new Scanner(System.in)) {
				// Print welcome message
				System.out.println("////////////////////////////////");
				System.out.println("///   MMD to HTML Converter  ///");
				System.out.println("////////////////////////////////");
				// Debug information
				System.out.println("[DEBUG] Working Directory:" + System.lineSeparator() + System.getProperty("user.dir"));
				System.out.println("--------------------------------");
				// Get input file path
				do {
					System.out.println("> Enter path to input file: ");
					inputFilePath = scanner.nextLine();
				} while (inputFilePath.trim().isEmpty());
				// Get output file path
				System.out.println("> Enter path to output file: ");
				outputFilePath = scanner.nextLine();
			}
		} else if (args.length == 2) {
			inputFilePath = args[0];
			outputFilePath = args[1];
		} else {
			// Just print error message and exit
			System.err.println("Usage: java Converter <input-file> [<output-file>]");
			System.exit(1);
		}

		/// Call the converter
		MmdDocSection.parse(inputFilePath).toHTML(outputFilePath);
	}
}
