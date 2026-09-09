# Repository structure and migration

Status: implemented using the approved src/ and test/ layout.
This replaces the earlier proposal to put the entry point and the dev/madpang package tree at the repository root.

## Current layout

```text
mmd2html/
|-- src/
|   |-- MmdConverter.java
|   |-- ast/
|   |   |-- MmdDocument.java
|   |   |-- MmdHeader.java
|   |   |-- MmdSection.java
|   |   |-- SemanticParagraph.java
|   |   `-- blocks/
|   |       |-- IBlock.java
|   |       |-- BlockParser.java
|   |       |-- ParagraphBlock.java
|   |       `-- CodeBlock.java
|   `-- util/
|       `-- CommonUtil.java
|-- test/
|   `-- ast/
|       |-- MmdHeaderTest.java
|       `-- blocks/
|           |-- README.md
|           |-- ParagraphBlockTest.java
|           `-- CodeBlockTest.java
|-- docs/
|-- build.gradle.kts
|-- settings.gradle.kts
|-- gradle/
|-- gradlew
|-- gradlew.bat
|-- gradle.properties
|-- README.txt
|-- example-mmd-doc.txt
`-- build/                         (generated and ignored)
```

Application and test package paths start at their respective source roots.
The package names are ast, ast.blocks, and util.
MmdConverter remains in the default package and remains the application entry point.

## Gradle configuration

There is one root project named mmd2html.
The application build script is now at the root and settings no longer include an app subproject.
The wrapper, version catalog, Java 21 toolchain, JUnit setup, and JAR manifest are retained.
Guava remains declared to keep dependency cleanup separate from the layout change.

The root build script selects source directories explicitly:

```kotlin
sourceSets {
    main {
        java.setSrcDirs(listOf("src"))
        resources.setSrcDirs(emptyList<String>())
    }
    test {
        java.setSrcDirs(listOf("test"))
        resources.setSrcDirs(emptyList<String>())
    }
}
```

Tests and generated files under build/ are outside the application source root.
No broad repository scan or package include list is needed.
There are no resources in this version.
Add explicit resource roots if resources are introduced later.

## Migration from the old layout

| Old | Current |
| --- | --- |
| `app/src/main/java/MmdConverter.java` | `src/MmdConverter.java` |
| `app/src/main/java/dev/madpang/` | `src/` |
| `app/src/test/java/dev/madpang/` | `test/` |
| `dev.madpang.ast` | `ast` |
| `dev.madpang.ast.blocks` | `ast.blocks` |
| `dev.madpang.util` | `util` |
| `app/build.gradle.kts` | `build.gradle.kts` |
| `./gradlew :app:test` | `./gradlew test` |
| `app/build/libs/mmd2html.jar` | `build/libs/mmd2html.jar` |
| `app/build/classes/java/main` | `build/classes/java/main` |
| `app/build/reports/tests/test/index.html` | `build/reports/tests/test/index.html` |
| `app/build/distributions/app.*` | `build/distributions/mmd2html.*` |
| `app/build/install/app/bin/app` | `build/install/mmd2html/bin/mmd2html` |
| Gradle run working directory: `app/` | Repository root |

The root commands `./gradlew build` and `./gradlew test` remain valid.
The existing CI uses these commands and needs no path change.
README build and run examples now use the current paths.
Test notes use the shortened package names for test selection.

Code that imports the old packages must update its imports and be recompiled.
Consumers that only invoke MmdConverter keep the same entry point but must use the new artifact path.
Downstream pd-phronesis and Zettelkasten checkouts pinned to older converter commits keep their existing paths until they adopt this commit.
The inspected pd-phronesis develop/build-post.ps1 uses `tools/mmd2html/app/build/libs/mmd2html.jar`.
When adopting this refactor, change that path to `tools/mmd2html/build/libs/mmd2html.jar` and update any other app launcher names or :app: task references.
This refactor does not change their pinned dependencies.

Old ignored app/build files can remain in existing checkouts after switching branches.
They are not part of this build and should not be used to run the refactored converter.
The root clean task removes build/, including any plain JDK output or sample input saved there.
Recreate the README sample after running clean.

## Verification

Use the README commands for both the Gradle Wrapper and plain JDK builds.
Check that the existing 40 tests pass and that both JARs produce the same HTML as before the move.
Also check standard output, interactive input, direct classpath execution, Gradle run, and the installed launcher.
Application JARs should contain ast/ and util/ classes without test classes or the old dev/madpang/ tree.
Parser logic and CLI argument behavior are unchanged by this refactor.
