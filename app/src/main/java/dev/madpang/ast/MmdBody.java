package dev.madpang.ast;

import java.util.*;
import java.io.BufferedReader;
import java.io.IOException;

public class MmdBody {
	public List<MmdSection> sections = new ArrayList<>();

	public static MmdBody parse(BufferedReader reader) throws IOException {
		MmdBody body = new MmdBody();
		// body.sections = MmdSection.parseSections(reader);
		return body;
	}
}
