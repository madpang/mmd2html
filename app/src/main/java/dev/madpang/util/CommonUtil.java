/**
 * @file: CommonUtil.java
 * @brief: Utility class for the MMD to HTML conversion.
 * @author: madpang
 * @date:
 * - created on 2025-06-29
 * - updated on 2025-06-29
 */

package dev.madpang.util;

import java.io.IOException;

/**
 * Utility class for parsing and conversion operations.
 */
public class CommonUtil {
	/**
	 * Escapes HTML special characters in the given text.
	 */
	public static String escapeHTML(String text) throws IOException{
		if (text == null) {
			throw new IOException("Input text cannot be null.");
		}
		return text.replace("&", "&amp;")
				   .replace("<", "&lt;")
				   .replace(">", "&gt;");
	}
}
