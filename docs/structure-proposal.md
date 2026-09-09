# Repository structure proposal

Status: proposal only.
No source moves, package changes, or Gradle configuration changes have been made.

## Recommended layout

Make the repository one Gradle project and put the application entry point and package tree at its root.
Keep the existing Java package names so this change is about file layout alone.

```text
mmd2html/
|-- MmdConverter.java
|-- dev/
|   `-- madpang/
|       |-- ast/
|       |   |-- MmdDocument.java
|       |   |-- MmdHeader.java
|       |   |-- MmdSection.java
|       |   |-- SemanticParagraph.java
|       |   `-- blocks/
|       |       |-- IBlock.java
|       |       |-- BlockParser.java
|       |       |-- ParagraphBlock.java
|       |       `-- CodeBlock.java
|       `-- util/
|           `-- CommonUtil.java
|-- test/
|   `-- dev/madpang/ast/
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

Move `app/src/main/java/MmdConverter.java` to the root and `app/src/main/java/dev/` to `dev/`.
Move the contents of `app/src/test/java/` to `test/`, including the test notes.
There are no resource files in the current source tree.
If resources are needed later, add explicit `resources/` and `test-resources/` roots.

This removes `app/src/main/java` while retaining the package path `dev/madpang`.
Renaming packages to top-level `ast` and `util` would be a separate change affecting declarations and imports.
Keeping the names avoids that extra work and preserves the public class names.
A smaller alternative is `src/` and `test/`, but placing the entry point at the root is closer to the requested example.

The supplied [Java structure article](https://medium.com/@setopet/structuring-java-projects-omniscient-main-modules-and-composite-configurations-525bd936b817) provides the visual starting point.
Its main topic is module dependencies and configuration, rather than Gradle source directory settings.
This small application does not need the extra modules described later in that article.

## Gradle changes to make later

Move `app/build.gradle.kts` to the root.
Remove `include("app")` from `settings.gradle.kts` and keep the root project name and toolchain resolver.
Keep the application plugin, Java 21 toolchain, dependency versions, JUnit setup, main class, and JAR manifest unchanged in the first pass.
Add these source sets to the root build script:

```kotlin
sourceSets {
    main {
        java.setSrcDirs(listOf("."))
        java.setIncludes(listOf("MmdConverter.java", "dev/**/*.java"))
        resources.setSrcDirs(emptyList<String>())
    }
    test {
        java.setSrcDirs(listOf("test"))
        resources.setSrcDirs(emptyList<String>())
    }
}
```

The main source include list is required because the source root is now the repository root.
It prevents test files and generated Java files under `build/` from entering the application compilation.
Any new top-level application package would need an explicit include entry.
Gradle documents configurable source sets in its [Java plugin reference](https://docs.gradle.org/8.14.2/userguide/java_plugin.html#sec:java_project_layout).

Keep the wrapper and version catalog where they are.
Guava appears unused, but removing it can be reviewed separately from the layout change.
Delete the empty tracked `app/` hierarchy after the moves.
Old ignored `app/build/` files can be removed from a local checkout after checking that they contain only generated output.

## Commands and consumers affected

| Current | Proposed |
| --- | --- |
| `./gradlew build` | Unchanged |
| `./gradlew :app:test` | `./gradlew test` |
| `app/build/libs/mmd2html.jar` | `build/libs/mmd2html.jar` |
| `app/build/classes/java/main` | `build/classes/java/main` |
| `app/build/reports/tests/test/index.html` | `build/reports/tests/test/index.html` |
| `app/build/distributions/` | `build/distributions/` |
| Gradle run working directory: `app/` | Repository root |

The root project is named `mmd2html`, so application distribution and launcher names may change from `app` to `mmd2html`.
Check consumers of both the JAR and installed launchers.
The repository CI already runs `./gradlew build` from the root, so that command stays valid.
Search scripts, container files, IDE settings, and downstream pd-phronesis and Zettelkasten build scripts for `app/` paths and `:app:` task names before applying the change.
Update any affected consumers together with the layout change.

The proposed Gradle run command becomes:

```sh
./gradlew run --args="build/sample.txt build/sample.html"
```

The plain JDK source list becomes:

```sh
mkdir -p build/plain/classes
printf '%s\n' MmdConverter.java > build/plain/sources.txt
find dev -name '*.java' -print >> build/plain/sources.txt
javac --release 21 -encoding UTF-8 -d build/plain/classes @build/plain/sources.txt
jar --create --file build/plain/mmd2html.jar --main-class MmdConverter -C build/plain/classes .
```

These are proposed commands, not commands for the current tree.

## Implementation and acceptance plan

1. Record the current test count and HTML output from the README sample.
2. Move the files with `git mv` and apply the root Gradle settings above.
3. Update README paths and any consumer paths found in the audit.
4. Run `./gradlew clean build` and confirm that the existing tests pass.
5. Run the revised plain JDK build and compare its HTML with the baseline and Gradle JAR output.
6. Check Gradle run paths, interactive Java execution, standard output, distribution contents, and test reports.
7. Confirm that application JARs contain no test classes and source discovery excludes generated files.
8. Commit the layout change separately from any parser or dependency changes.

Completion means the same parser behavior and tests pass with the flatter tree and all known build consumers use the new paths.
Until this proposal is accepted for implementation, keep the existing source tree and build configuration.
