package dev.madpang.util;

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
