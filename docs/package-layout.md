---
file: package-layout.md
brief: How the folder structure relates to the Java package structure.
author: [claude, t-tang-rfc]
date: [created: 2026-09-13, updated: 2026-10-14]
---

# Folder structure vs. package structure

## Rule

Every Java package in this repo. is prefixed with `mmd2html`.
Otherwise, bare names like `ast` and `util` are too broad.

The folder path of a source file under its source root spells out its package name.
The source root for application code is `src/`, and the source root for tests is `test/`.

| Package | Application folder | Test folder |
|---|---|---|
| `mmd2html` | `src/mmd2html/` | `test/mmd2html/` |
| `mmd2html.ast` | `src/mmd2html/ast/` | `test/mmd2html/ast/` |
| `mmd2html.ast.blocks` | `src/mmd2html/ast/blocks/` | (none now) |
| `mmd2html.util` | `src/mmd2html/util/` | (none yet) |

A test class lives in the same package as the class it tests.
So `test/mmd2html/ast/MmdDocSectionTest.java` declares `package mmd2html.ast;`, the same as `src/mmd2html/ast/MmdDocSection.java`.
Test case files live in `test/mmd2html/testcases/`, and the benchmark template in `test/mmd2html/misc/`; they are plain folders, not packages.

## Why the folders must match

The Java build itself does not strictly need the match.
Gradle passes an explicit list of every `.java` file to `javac`, and `javac` then accepts any folder layout.
The match is still required for these reasons:
1. `javac -sourcepath` and every IDE resolve a class `a.b.C` by looking for `<root>/a/b/C.java`.
   With a mismatch, `javac -sourcepath src ...` fails with `package mmd2html.ast does not exist`.
2. Eclipse JDT, which powers the Java extension in VS Code, reports `The declared package does not match the expected package` as an error on every mismatched file.
3. The compiled class files always land in `build/classes/java/main/mmd2html/ast/...`.
   Keeping sources in the same shape makes it easy to map a class back to its source.

## Consequences for the build script

The source roots are declared in `build.gradle.kts`:

``` kotlin
sourceSets {
    main { java.setSrcDirs(listOf("src")) }
    test { java.setSrcDirs(listOf("test")) }
}
```

The roots stay `src` and `test`.
The `mmd2html/` folder below them is part of the package path, not a source root.
The entry point is referenced by its full name, `mmd2html.Converter`, in both `application.mainClass` and the jar `Main-Class` attribute.

## How to add a new package

1. Pick a name below `mmd2html`, for example `mmd2html.render`.
2. Create `src/mmd2html/render/` and put the sources there with `package mmd2html.render;`.
3. Put the tests in `test/mmd2html/render/` with the same package line.
4. No change to `build.gradle.kts` is needed.
