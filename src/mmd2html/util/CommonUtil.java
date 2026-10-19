/**
 * @file: CommonUtil.java
 * @brief: Utility class for the MMD to HTML conversion.
 * @author: [madpang, claude]
 * @date: [created: 2025-06-29, updated: 2026-10-15]
 */

package mmd2html.util;

import java.io.IOException;
import java.util.Map;

public final class CommonUtil {
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

	/**
	 * @brief Converts em dashes in the given text to `&mdash;`.
	 *
	 * @details An unspaced `---` (e.g. `foo---bar`) and a spaced ` -- ` (e.g. `foo -- bar`) both become an em dash, without spaces (e.g. `foo&mdash;bar`).
	 * @details Two hyphens without spaces (e.g. `--all`, `1--2`) stay as they are.
	 * @note Call it after `escapeHTML`, so the `&` of `&mdash;` is not escaped.
	 */
	public static String convertEmDash(String text) {
		return text.replace("---", "&mdash;").replace(" -- ", "&mdash;");
	}

	/**
	 * @brief Extracts a `key: value` pair from one line.
	 *
	 * @return The pair.
	 *
	 * @details The key is everything before the first `:`, the value is the rest, both trimmed.
	 * @details This is NOT YAML: no nesting, no lists, no quoting -- `[a, b]` stays the plain text value `[a, b]`.
	 * @details A line that is not `key: value` -- blank, without `:`, or with an empty key -- throws; the caller decides how to respond.
	 */
	public static Map.Entry<String, String> extractKeyValuePair(String line) throws IOException {
		int colon = line.indexOf(':');
		if (colon < 0) {
			throw new IOException("[ERROR] Line is not `key: value`: " + line);
		}
		String key = line.substring(0, colon).trim();
		if (key.isEmpty()) {
			throw new IOException("[ERROR] Line has an empty key: " + line);
		}
		return Map.entry(key, line.substring(colon + 1).trim());
	}
}
