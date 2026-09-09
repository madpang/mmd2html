# mmd2html

**mmd2html** converts custom plain text markup to an HTML body fragment.
It does not add a full HTML page, styles, or rendered header metadata.
This branch supports headings, paragraphs, and fenced code blocks.
The inline code, list, and image features from the separate feature branches are not included here.

## Build with the Gradle Wrapper

Install JDK 21 and put `java` and `javac` on PATH.
Run the examples from the repository root in a POSIX shell, such as Bash on Linux.
No separate Gradle installation is needed.
The wrapper uses Gradle 8.14.2 and downloads Gradle and dependencies on its first run, so network access is needed.

```sh
./gradlew build
```

This compiles the application, runs the JUnit tests, and creates `app/build/libs/mmd2html.jar`.
Application distributions are under `app/build/distributions/` and compiled classes are under `app/build/classes/java/main/`.
Use `./gradlew test` to run the test task and its prerequisites.
The test report is at `app/build/reports/tests/test/index.html`.
On Windows, use `gradlew.bat build` for the wrapper build.

## Build with plain JDK tools

The application source uses only JDK classes and classes in this repository.
Gradle declares Guava, but the current application does not use it.
With JDK 21, you can compile and package the application without Gradle or downloaded libraries:

```sh
mkdir -p build/plain/classes
find app/src/main/java -name '*.java' -print > build/plain/sources.txt
javac --release 21 -encoding UTF-8 -d build/plain/classes @build/plain/sources.txt
jar --create --file build/plain/mmd2html.jar --main-class MmdConverter -C build/plain/classes .
```

This builds only the application, without compiling or running the JUnit tests.
Use the Gradle build to run the test suite with its test dependencies.
The plain build output is separate from Gradle's output.

## Input and usage

The parser requires a header fenced with three backticks, all fields below, and a body starting with a level-one heading.
Create a `build` directory and save this example as `build/sample.txt`:

````text
``` header
@file: sample.txt
@brief: A small conversion example.
@title: Example
@author: Example author
@date: [created: 2026-09-09, updated: 2026-09-09]
@version: 0.1.0
```

# Example

This is a paragraph.

## Code

``` code
System.out.println("Hello");
```
````

The bundled `example-mmd-doc.txt` uses older `+++` fences and multiline dates that the current parser rejects.
Use the example above with these commands.

After a Gradle build:

```sh
java -jar app/build/libs/mmd2html.jar build/sample.txt build/sample.html
```

After a plain JDK build:

```sh
java -jar build/plain/mmd2html.jar build/sample.txt build/sample.html
```

The output file is replaced if it exists, and its parent directory must already exist.
Pass an empty second argument to write to standard output:

```sh
java -jar app/build/libs/mmd2html.jar build/sample.txt ""
```

The CLI currently accepts either zero arguments or exactly two arguments.
A single input argument fails, even though the built-in usage message marks the output argument as optional.
With no arguments, it prompts for the input path and then the output path.
Press Enter at the output prompt to write HTML to standard output.

```sh
java -jar app/build/libs/mmd2html.jar
```

You can also run the compiled classes directly:

```sh
java -cp app/build/classes/java/main MmdConverter build/sample.txt build/sample.html
```

For Gradle execution, supply both paths relative to `app/`, which is the run task's working directory:

```sh
./gradlew run --args="../build/sample.txt ../build/sample.html"
```

Direct `java` commands resolve paths from the shell's working directory.
Use direct `java` execution for interactive input.

## Structure proposal

See [the structure proposal](docs/structure-proposal.md) for a flatter layout and the needed Gradle changes.
The proposal has not been applied.

## Earlier design notes

The notes below are retained as design discussion, rather than a list of implemented features.

## Method (TODO: need formalization)

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
