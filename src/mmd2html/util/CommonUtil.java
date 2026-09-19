/**
 * @file: CommonUtil.java
 * @brief: Utility class for the MMD to HTML conversion.
 * @author: madpang
 * @date: [created: 2025-06-29, updated: 2025-06-29]
 */

package mmd2html.util;

import java.io.IOException;

public class CommonUtil {
	/**
	 * @brief: Escapes HTML special characters in the given text.
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
