/**
 * @file ListBlock.java
 * @brief Represents a simple list, ordered or unordered, with at most two levels.
 * @author [claude]
 * @date [created: 2026-10-15, updated: 2026-10-15]
 *
 * @details An item line is `<indent><marker> <text>`, where the marker is a number followed by a dot (e.g. `1.`) for an ordered list, or a dash `-` for an unordered list.
 * @details A level-1 item starts at column one, i.e., with no leading space.
 * @details A level-2 (nested) item has leading spaces; any number of them counts as level 2, it belongs to the last level-1 item.
 * @details The list ends at the first line that is not an item, or at a level-1 item of the other kind (which starts a new list).
 * @details The kind of a nested list is set by its first item.
 */

package mmd2html.ast.blocks;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Optional;

import mmd2html.util.CommonUtil;
import mmd2html.util.LineCursor;

public final class ListBlock implements IBlock {
	// Instance member(s)
	private boolean ordered = false;
	private List<String> items = new ArrayList<>();       // item texts
	private List<ListBlock> subLists = new ArrayList<>(); // aligned with items: the nested list of each item, or null

	@Override
	public String getTypeName() {
		return "list";
	}

	@Override
	public List<String> toHTML() throws IOException {
		String tag = ordered ? "ol" : "ul";
		List<String> htmlLines = new ArrayList<>();
		htmlLines.add("<" + tag + ">");
		for (int i = 0; i < items.size(); i++) {
			String text = CommonUtil.convertEmDash(CommonUtil.escapeHTML(items.get(i)));
			ListBlock subList = subLists.get(i);
			if (subList == null) {
				htmlLines.add("<li>" + text + "</li>");
			} else {
				htmlLines.add("<li>" + text);
				htmlLines.addAll(subList.toHTML());
				htmlLines.add("</li>");
			}
		}
		htmlLines.add("</" + tag + ">");
		return htmlLines;
	}

	/**
	 * @brief Static factory / parser
	 * @details A non-match leaves the cursor untouched; a match needs a level-1 item at the cursor.
	 * @details Success consumes every item line, and leaves the first non-item line unconsumed.
	 */
	public static Optional<IBlock> parse(LineCursor cursor) throws IOException {
		Pattern itemPattern = getItemPattern();
		Matcher matcher = itemPattern.matcher(cursor.peek());
		if (!matcher.matches() || !matcher.group(1).isEmpty()) {
			return Optional.empty();
		}
		// Level-1 item found, it sets the kind of the list
		ListBlock block = new ListBlock();
		block.ordered = isOrderedMarker(matcher.group(2));
		String currentLine = null;
		while ((currentLine = cursor.peek()) != null) {
			matcher = itemPattern.matcher(currentLine);
			if (!matcher.matches()) {
				break;
			}
			boolean orderedItem = isOrderedMarker(matcher.group(2));
			if (matcher.group(1).isEmpty()) { // level-1 item
				if (orderedItem != block.ordered) {
					break; // a list of the other kind begins
				}
				block.items.add(matcher.group(3));
				block.subLists.add(null);
			} else { // level-2 item, under the last level-1 item
				int last = block.items.size() - 1;
				if (block.subLists.get(last) == null) {
					ListBlock subList = new ListBlock();
					subList.ordered = orderedItem;
					block.subLists.set(last, subList);
				}
				ListBlock subList = block.subLists.get(last);
				subList.items.add(matcher.group(3));
				subList.subLists.add(null);
			}
			cursor.next();
		}
		return Optional.of(block);
	}

	// --- Private Utility Functions
	/**
	 * @brief Returns the pattern of an item line.
	 * @details Groups: (1) leading spaces, (2) marker, (3) item text.
	 */
	private static Pattern getItemPattern() {
		return Pattern.compile("^( *)(\\d+\\.|-) (\\S.*)$");
	}

	private static boolean isOrderedMarker(String marker) {
		return marker.matches("\\d+\\.");
	}
}
