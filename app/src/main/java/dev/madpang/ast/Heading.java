/**
 * @file: Heading.java
 * @brief: Represents a heading in a section of the document.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-09
 */

package dev.madpang.ast;

public class Heading {
	public int level;
	public String text;

	public Heading(int level, String text) {
		this.level = level;
		this.text = text;
	}
}
