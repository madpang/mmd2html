/**
 * @file: MetaMatter.java
 * @brief: Represents the metadata of the document.
 * @author: madpang
 * @date:
 * - created on 2025-06-09
 * - updated on 2025-06-09
 */

package dev.madpang.ast;

import java.util.*;

public class MetaMatter {
	public String file;
	public String brief;
	public String title;
	public String author;
	public List<String> date = new ArrayList<>();
	public String version;
}
