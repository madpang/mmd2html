---
file: parser-flow.md
brief: Forward-only parser ownership and MetaInfo recognition.
---

# Parser flow

The conversion path shares one `LineCursor` between `MmdDocSection`,
`SemanticParagraph`, and registered block parsers. `peek()` does not consume a
line; `next()` consumes it; `nextLineNumber()` reports the 1-based position of
the next line. The file-level caller owns and closes the reader.

Sections consume their heading before delegating their content. A heading that
closes a section remains on the cursor for its caller. Semantic paragraphs
consume their terminating blank line. An unsuccessful block parser consumes
nothing; a successful parser consumes its entire block, including fences,
and leaves the following line unconsumed.

`IBlock` describes parsed content and rendering.
Its nested functional interface `IBlock.Parser` describes the callable parsing operation.
The registry stores direct method references (`MetaInfoBlock::parse`, `CodeBlock::parse`, `ListBlock::parse`), adapting each static factory to that interface.
There is no separate `BlockParser` file or per-block `PARSER` constant.

Ordinary paragraphs retain the existing blank-line grouping.
Code blocks may contain blank lines and heading-like text.
A code block without a closing fence takes everything up to EOF.
Consecutive ordinary lines use the same `ParagraphBlock`, identified with `instanceof` rather than string identity.
Headings following ordinary prose still require a separating blank line.

## List

`ListBlock` supports simple lists, two levels at most, for canonical writing only.

- An item line is `<indent><marker> <text>`; the marker `1.` (any number and a dot) makes an ordered list, `-` an unordered one.
- A level-1 item starts at column one; its marker kind sets the kind of the list.
- An item with leading spaces is a nested item under the last level-1 item; any number of spaces counts as level 2.
- The kind of a nested list is set by its first item.
- The list ends at the first line that is not an item, which is left for the semantic paragraph; a level-1 item of the other kind ends it too, and starts a new list.
- Item numbers are not kept; the list renders as `<ol>` or `<ul>`, each item as `<li>text</li>`, a nested list inside its parent `<li>`.

``` text
Ensure the VM is powered off, then:
1. Open virt-manager,
2. Configure:
   - Driver: virtiofs
   - Access mode: Passthrough
3. Apply changes.
```

This is one semantic paragraph: a paragraph block and a list block, whose second item holds a nested unordered list.

## Em dash

Section headings (in `MmdDocSection`), `ParagraphBlock` and `ListBlock` convert em dashes with `CommonUtil.convertEmDash`, after `escapeHTML`.

- An unspaced `---` (`foo---bar`) and a spaced ` -- ` (`foo -- bar`) both become `&mdash;`, without spaces (`foo&mdash;bar`).
- Two hyphens without spaces (`--all`, `1--2`) stay as they are.
- Code blocks are not converted.
- A line that is exactly `---` never reaches a paragraph: it opens a MetaInfo block.

## MetaInfo

`MetaInfoBlock` implements `IBlock` alongside code and paragraph blocks.
Its parser is first in `SemanticParagraph.REGISTERED`, followed by the code parser.
Both are tried through the same dispatch loop.
Metadata can coexist with visible content inside one semantic paragraph; there is no paragraph-wide metadata flag.

- An exact `---` line opens a metadata block, also after ordinary prose or another block.
- Metadata ends at the next exact `---`, which is consumed.
- Metadata may contain blank lines; they belong to the block, not to the semantic paragraph.
- Without a closing `---`, everything up to EOF is metadata, headings included.
  A canonical doc always closes its blocks, so this is left for later validation.
- Text right after a closing marker continues in the same semantic paragraph, unless a blank line comes first.
- Metadata contents are opaque, including code-like fences, headings, indentation, and arbitrary non-YAML text.
- `MetaInfoBlock.getMetaInfoChunk()` returns the verbatim payload, without the opening and closing markers.
- `CommonUtil.extractKeyValuePair` reads one such line as a `key: value` pair, when a caller wants that; any other line, blank included, throws `IOException`, and the caller decides how to respond.
- `MetaInfoBlock.toHTML()` returns no HTML.
  A paragraph whose blocks render no content emits no empty `<s-paragraph>` wrapper.

Recognition does not validate YAML, interpret keys, impose required fields, assign metadata scope, or enforce canonical document form.

``` text
some text
---
key: value
---
more text

---
other: value

third: value
---

New semantic paragraph.
```

The first semantic paragraph holds a paragraph block, a metadata block, and another paragraph block.
The second holds only metadata, with a blank line inside, so it renders nothing.
The final text begins a third semantic paragraph.
