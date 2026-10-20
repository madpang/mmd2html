---
file: README.txt
author: [madpang, t-tang-rfc, codex, claude]
date: [created: 2025-05-17, updated: 2026-10-15]
---

# mmd2html

**mmd2html** converts plain text with custom markup to an HTML body fragment.
It does not add a full HTML page, styles, or rendered header metadata.
This branch supports headings, semantic paragraphs, fenced code blocks, simple lists (two levels at most), em dashes in headings, paragraphs and lists, and opaque MetaInfo chunks.
See `docs/parser-flow.md` for cursor ownership and metadata recognition rules.
The inline code and image features from the separate feature branches are not included here.

## What is MMD

MMD is not an acronym.
However, it might be interpreted as *Modified Markdown Document* or *Minimal Markup Document*.

## Repository structure

The following ASCII graph depicts the structure of this repo.:
``` tree
mmd2html/
|- README.txt                          # *this* doc
|- docs/
|- src/
|  |- mmd2html/
|  |  |- Converter.java
|  |  |- ast/
|  |  |  |- MmdDocSection.java
|  |  |  |- SemanticParagraph.java
|  |  |  |- blocks/
|  |  |  |  |- IBlock.java
|  |  |  |  |- ParagraphBlock.java
|  |  |  |  |- CodeBlock.java
|  |  |  |  |- ListBlock.java
|  |  |  |  |- MetaInfoBlock.java
|  |  |- util/
|  |  |  |  |- LineCursor.java
|  |  |  |  |- CommonUtil.java
|- test/
|  |- mmd2html/
|  |  |- ast/
|  |  |  |- MmdDocSectionTest.java
|  |  |- testcases/...                 # input files and benchmark pages
|  |  |- misc/...                      # template for benchmark pages
|- build/...
|- gradle/...                          # Gradle related stuff
|- ...
|- .devcontainer                       # Development Container config.
```

## Build mmd2html

mmd2html is a simple Java project, which you can build with plain JDK tools.
Or, if you are familiar with [Gradle](https://gradle.org/), you will find that this project is already configured to build with Gradle Wrapper.
The `.devcontainer` folder contains everything necessary to setup a Docker container -- you can also refer to the scripts there to build your own environment.

### Build with the Gradle Wrapper

The following tutorial assumes using the associated Dev Container:
``` terminal
|-[ubuntu@4f0ef90f2eed:/mnt/datastore/cowork/20_repo/mmd2html][]
: ./gradlew build
|-[2026-09-10T15:07:19+09:00][o][]
```
The comand compiles the application, runs the JUnit tests, and creates `build/libs/mmd2html.jar`.
Application distributions are under `build/distributions/` and compiled classes are under `build/classes/java/main/`.

Use `./gradlew test` to run the test task and its prerequisites.
The test report is at `build/reports/tests/test/index.html`.

### Build with plain JDK tools

The following tutorial also assumes using the associated Dev Container:
``` terminal
|-[ubuntu@4f0ef90f2eed:/mnt/datastore/cowork/20_repo/mmd2html][]
: mkdir -p build/plain/classes
: find src -name '*.java' -print > build/plain/sources.txt
: javac --release 21 -encoding UTF-8 -d build/plain/classes @build/plain/sources.txt
: jar --create --file build/plain/mmd2html.jar --main-class mmd2html.Converter -C build/plain/classes .
|-[2026-09-10T15:07:19+09:00][o][]
```
The commands only build the application, without compiling or running the JUnit tests.

## Usage

For Gradle execution, supply both paths relative to the repository root:
``` cmd
./gradlew run --args="examples/example-input.txt examples/example-output.html"
```

Or, after a plain JDK build:
``` cmd
java -jar build/plain/mmd2html.jar examples/example-input.txt examples/example-output.html
```
You can also run the compiled classes directly:
``` cmd
java -cp build/classes/java/main mmd2html.Converter build/sample.txt build/sample.html
```
Note, direct `java` commands resolve paths from the shell's working directory.

The CLI currently accepts either zero arguments or exactly two arguments.
With no arguments, it prompts for the input path and then the output path.

## Test

The tests use JUnit 5 (Jupiter) framework as configured in the project's `build.gradle.kts`.

To run all tests, use the following command from the repository root:
``` bash
./gradlew test
```

`MmdDocSection` is tested with files under `test/mmd2html/testcases/`.
Each `\d{2}-<name>.txt` is one input, and starts with a MetaInfo block (`file`, `brief`, `details`, etc.) that describes the test case.
The actual output goes to `build/test-output/\d{2}-<name>.html`.
The sibling `\d{2}-<name>.html` in `test/mmd2html/testcases` is the benchmark, which *should be written by hand* -- yes, in the sense that *THE TEST* should be treated more seriously.
Since `mmd2html` does not by itself generate a full HTML page, a template file `html-article-template.html` is placed in `test/mmd2html/misc/` to help constructing the test case as a valid page for intuitive visual inspection.
The expected output should be inserted between `<!-- @ARTICLE:BEGIN -->` and `<!-- @ARTICLE:END -->` marks of the template.

## Method (@todo this is early design notes, need formalization)

=== AST approach

We model the document as an Abstract Syntax Tree (AST), capturing header metadata and a hierarchy of sections with content blocks.
An AST is "a tree representation of the abstract syntactic structure" of the text.
Here we define node classes such as **Document**, **MetaMatter**, **Section**, and various block types (Paragraph, List, CodeBlock, QuoteBlock, etc.).
The Document node holds a MetaMatter and a list of Section nodes.
Each Section has a Heading (with level and text) and a list of **SemanticParagraphs**.

=== structure

Now we reconsider the overall structure of the parse to convert mmd into AST.
We already modeled a Document as consists of a MetaMatter and a list of Sections, and the parser is feed in a stream of text, each time a single line.
Instead of constructing a monolithic parser that fills each part of Document, and the sub part down there for each component, I think it will be more natural to "dissolve" the functionality of Mmd2AstParser.java to each component of the Document.
Say, each class should parse the text into its own's components, and call components' parser to parse further.
For example, when a stream of text comes in, `Document` uses its parse text into `MetaMatter` and `Section`(s), and `MetaMatter` should call its own parse method to pass the passed in text into fields of its own; While `Section` should use its method to further pase the content into Subsection(s) etc. (for further implementation).
Can you revise the code to orient to this direction?

=== Canonical form check


{H1, H2, H2, ...}

but note, {H1, div, H4, H3, H2, H2 ...} is ~~also valid~~.

{H2, H3, H3, ...}

{H3, div, div, div ...}

{div, ... ...}

In the most simple case

{div, p, p, p ...}


--- Given the structure being created, need to record the *content*

The relation between <p> and <s-para>
