/**
 * @file LineCursor.java
 * @brief Forward-only line cursor shared by document parsers.
 * @author [codex, t-tang-rfc]
 * @date [created: 2026-10-05, updated: 2026-10-05]
**/

package mmd2html.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

/**
 * Forward-only, one-line lookahead shared by section, paragraph and block parsers.
 * The caller owns the reader and is responsible for closing it.
 */
public final class LineCursor {
	private final BufferedReader reader;
	private String pendingLine;
	private boolean loaded;
	private int nextLineNumber = 1;

	public LineCursor(BufferedReader reader) {
		this.reader = Objects.requireNonNull(reader, "reader");
	}

	/** Returns the next line, or null at EOF, without logically consuming it. */
	public String peek() throws IOException {
		if (!loaded) {
			pendingLine = reader.readLine();
			loaded = true;
		}
		return pendingLine;
	}

	/** Consumes the next line. Repeated reads at EOF return null. */
	public String next() throws IOException {
		String line = peek();
		if (line != null) {
			pendingLine = null;
			loaded = false;
			nextLineNumber++;
		}
		return line;
	}

	/** 1-based position of the next line; at EOF, one past the last line. */
	public int nextLineNumber() {
		return nextLineNumber;
	}
}
