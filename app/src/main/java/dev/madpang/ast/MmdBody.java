/**
 * @file: MmdBody.java
 * @brief: Represents the body of a MMD document, which contains the main content.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-17
 */

package dev.madpang.ast;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;
	
public class MmdBody {
	public List<MmdSection> sections = new ArrayList<>();

	public static MmdBody parse(BufferedReader reader, String firstLine) throws IOException {
		MmdBody body = new MmdBody();
		System.out.println("Parsing MmdBody...");
		System.out.println("First non-empty line: " + firstLine);
		// body.sections = MmdSection.parseSections(reader);
		return body;
	}
}
