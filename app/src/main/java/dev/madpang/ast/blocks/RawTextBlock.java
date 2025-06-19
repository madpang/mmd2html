package dev.madpang.ast.blocks;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
/**
 * Represents a raw text block (for lines not yet parsed into richer block types).
 */
public class RawTextBlock {
	public List<String> lines = new ArrayList<>();
	
	public static RawTextBlock parse(BufferedReader reader, String firstLine) throws IOException {
		RawTextBlock block = new RawTextBlock();
		try {
			String currentLine = (firstLine != null) ? firstLine : reader.readLine();
			if (currentLine != null) {
				block.lines.add(currentLine);
				while ((currentLine = reader.readLine()) != null) {
					block.lines.add(currentLine);
				}
			}
		} catch (IOException e) {
			throw e; // Re-throw the original exception
		}
		return block;
	}

	/**
	 * @brief: An overloaded `parse` method, with a single argument.
	 */
	 public static RawTextBlock parse(BufferedReader reader) throws IOException {
		return parse(reader, null);
	}

	/**
	 * @brief: A print method to display the raw text block.
	 */
	public void print() {
		if (lines.isEmpty()) {
			System.out.println("RawTextBlock is empty.");
			return;
		} else {
			for (String line : lines) {
				System.out.println(line);
			}
		}
	}
}
