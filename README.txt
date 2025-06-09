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
