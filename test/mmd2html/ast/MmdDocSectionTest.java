/**
 * @file MmdDocSectionTest.java
 * @brief Unit behavior test of MmdDocSection: parse each input file, render it, compare with the expected page.
 * @author [claude, t-tang-rfc]
 * @date [created: 2026-10-13, updated: 2026-10-15]
 *
 * @details Every `\d{2}-<name>.txt` under `test/mmd2html/testcases/` is one test case.
 * @details The case starts with a MetaInfo block that describes it -- `file`, `brief`, `details`.
 * @details The sibling `\d{2}-<name>.html` is the benchmark, written by hand from `test/mmd2html/misc/html-article-template.html`.
 * @details The lines between its `<!-- @ARTICLE:BEGIN -->` and `<!-- @ARTICLE:END -->` marks are the expected output.
 * @details The actual output is written to `build/test-output/NN-<name>.html`, as it is.
**/

package mmd2html.ast;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.Assertions;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Files;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Stream;

import mmd2html.ast.blocks.IBlock;
import mmd2html.ast.blocks.MetaInfoBlock;
import mmd2html.util.CommonUtil;
import mmd2html.util.LineCursor;

public class MmdDocSectionTest {
	private static final Path TESTCASE_DIR = Path.of("test/mmd2html/testcases");
	private static final Path OUTPUT_DIR = Path.of("build/test-output");
	private static final String ARTICLE_BEGIN = "<!-- @ARTICLE:BEGIN -->";
	private static final String ARTICLE_END = "<!-- @ARTICLE:END -->";

	@TestFactory
	List<DynamicTest> testMmdDocSection() throws IOException {
		List<Path> inputs;
		try (Stream<Path> files = Files.list(TESTCASE_DIR)) {
			inputs = files.filter(p -> p.toString().endsWith(".txt")).sorted().toList();
		}
		Assertions.assertFalse(inputs.isEmpty(), "No test case found under " + TESTCASE_DIR.toAbsolutePath());
		Files.createDirectories(OUTPUT_DIR);

		return inputs.stream().map(input -> {
			Map<String, String> meta = readMeta(input);
			String name = input.getFileName().toString();
			return DynamicTest.dynamicTest(name + " -- " + meta.get("brief"), () -> {
				Assertions.assertEquals(name, meta.get("file"), "MetaInfo `file` must be the file name");
				check(input);
			});
		}).toList();
	}

	/**
	 * @brief Parses and renders one input to the output file, and compares it with the article of the benchmark.
	 */
	private static void check(Path input) throws Exception {
		String base = input.getFileName().toString().replaceFirst("\\.txt$", "");
		Path outputFile = OUTPUT_DIR.resolve(base + ".html");
		MmdDocSection.parse(input.toString()).toHTML(outputFile.toString());

		Path benchmarkFile = input.resolveSibling(base + ".html");
		Assertions.assertTrue(Files.exists(benchmarkFile), "Benchmark missing: " + benchmarkFile + "; check the output in " + outputFile);
		List<String> expected = readArticle(benchmarkFile);
		List<String> actual = Files.readAllLines(outputFile);
		Assertions.assertEquals(String.join("\n", expected), String.join("\n", actual));
	}

	/**
	 * @brief Reads the article of a benchmark file, line by line.
	 *
	 * @details Lines are collected after the begin mark, until the end mark; the marks themselves are left out.
	 */
	private static List<String> readArticle(Path benchmarkFile) throws IOException {
		List<String> article = new ArrayList<>();
		boolean inside = false;
		for (String line : Files.readAllLines(benchmarkFile)) {
			if (!inside) {
				inside = line.equals(ARTICLE_BEGIN);
				continue;
			}
			if (line.equals(ARTICLE_END)) {
				return article;
			}
			article.add(line);
		}
		throw new IOException("Benchmark has no `" + ARTICLE_BEGIN + "` ... `" + ARTICLE_END + "` marks: " + benchmarkFile);
	}

	/**
	 * @brief Reads the MetaInfo block at the start of a test case file.
	 */
	private static Map<String, String> readMeta(Path input) {
		try (BufferedReader reader = Files.newBufferedReader(input)) {
			IBlock block = MetaInfoBlock.parse(new LineCursor(reader))
				.orElseThrow(() -> new IOException("Test case must start with a MetaInfo block: " + input));
			Map<String, String> meta = new HashMap<>();
			for (String line : ((MetaInfoBlock) block).getRawMetaInfoChunk()) {
				try {
					Map.Entry<String, String> pair = CommonUtil.extractKeyValuePair(line);
					meta.put(pair.getKey(), pair.getValue());
				} catch (IOException e) {
					continue; // a blank line, or any line that is not `key: value`, is skipped
				}
			}
			return meta;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
