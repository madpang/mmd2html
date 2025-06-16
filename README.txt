Convert custom plain text markup to HTML.

--- Step 0

Wrap raw text in <pre> tags---Yes, from text to HTML!

--- Step 1

Parse the header of the content file.

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

=== Run

For the interactive execution
```
java -cp app/build/classes/java/main dev.madpang.App
# Then supply the path to the file, e.g. `example-mmd-doc.txt`
```
Note, the working directory is where you run the command.

For gradle Run
```
./gradlew run --args="../example-mmd-doc.txt"
```
Note, in this case, the working directory is `./app`.
