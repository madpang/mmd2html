/**
 * @file: MmdConverter.java
 * @brief: Main application class for the Mmd to HTML conversion.
 * @details:
 * This class serves as the entry point for the MMD to HTML conversion application.
 * It is a wrapper which calls the MmdDocument to parse the MMD document and converts it to HTML.
 * It also provides an interactive mode if no arguments are provided.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-07-01
 */

import java.util.Scanner;

import dev.madpang.ast.MmdDocument;

public class MmdConverter {
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
				System.out.println("> Enter path to output file (or press Enter for stdout): ");
				outputFilePath = scanner.nextLine();
			}
		} else if (args.length == 2) {
			inputFilePath = args[0];
			outputFilePath = args[1];
		} else {
			// Just print error message and exit
			System.err.println("Usage: java MmdConverter <input-file> [<output-file>]");
			System.exit(1);
		}

		/// Call the converter
		if (outputFilePath != null && !outputFilePath.trim().isEmpty()) {
			MmdDocument.parse(inputFilePath).toHTML(outputFilePath);
		} else {
			MmdDocument.parse(inputFilePath).toHTML().forEach(System.out::println);
		}
	}
}
