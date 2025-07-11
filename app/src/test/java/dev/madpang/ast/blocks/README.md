# Unit Tests for dev.madpang.ast.blocks Package

This directory contains comprehensive unit tests for the `dev.madpang.ast.blocks` package.
The test suite covers all classes and interfaces in the package with thorough test coverage.

## Test Files

### 1. CodeBlockTest.java
Tests the `CodeBlock` class which handles fenced code blocks in MMD.

**Test Coverage:**
- Basic parsing of code blocks with different languages
- HTML generation with proper escaping
- Error handling for malformed input
- Edge cases like empty code blocks and unterminated fences
- Static parser functionality

**Key Test Cases:**
- Valid code block parsing (`+++ language` format)
- Multi-line code content
- HTML special character escaping (`<`, `>`, `&`)
- Error conditions (unterminated blocks, invalid fence format)

### 2. ParagraphBlockTest.java
Tests the `ParagraphBlock` class which represents paragraph content.

**Test Coverage:**
- Line addition and validation
- HTML generation
- Error handling for invalid input
- Special character escaping
- Long content handling

**Key Test Cases:**
- Adding single and multiple lines
- Rejecting null, empty, or whitespace-only lines
- HTML structure generation (`<p>` tags)
- Unicode character support
- Maintaining line order

### 3. BlockParserTest.java
Tests the `BlockParser` functional interface.

**Test Coverage:**
- Functional interface implementation
- Parser composition patterns
- Error propagation
- Integration with existing parsers

**Key Test Cases:**
- Custom parser implementations
- Optional return value handling
- BufferedReader interaction
- Exception handling

### 4. IBlockTest.java
Tests the `IBlock` interface contract.

**Test Coverage:**
- Interface contract verification
- Polymorphic behavior
- Error handling consistency
- HTML generation patterns

**Key Test Cases:**
- Type identification
- HTML output structure
- Null handling
- Immutability patterns

### 5. BlocksIntegrationTest.java
Integration tests for the entire blocks package.

**Test Coverage:**
- Multi-block processing
- Parser chain behavior
- HTML structure validation
- Cross-block consistency
- Real-world content scenarios

**Key Test Cases:**
- Processing different block types together
- Parser extensibility
- Error handling consistency
- Complex content scenarios

## Running the Tests

To run all tests:
```bash
./gradlew test
```

To run tests for a specific class:
```bash
./gradlew test --tests "dev.madpang.ast.blocks.CodeBlockTest"
```

To run with detailed output:
```bash
./gradlew test --info
```

## Test Results

- **Total Tests:** 54
- **All tests pass** ✅
- **Code Coverage:** Comprehensive coverage of all public methods and edge cases

## Test Quality Features

1. **Descriptive Test Names:** All tests use `@DisplayName` annotations with clear descriptions
2. **Comprehensive Edge Cases:** Tests cover error conditions, boundary cases, and malformed input
3. **Real-World Scenarios:** Integration tests use realistic content patterns
4. **Error Verification:** Proper exception testing with message validation
5. **HTML Validation:** Verification of proper HTML structure and escaping

## Dependencies

The tests use JUnit 5 (Jupiter) framework as configured in the project's `build.gradle.kts`:
- `testImplementation(libs.junit.jupiter)`
- `testRuntimeOnly("org.junit.platform:junit-platform-launcher")`

The test task is configured to use JUnit Platform:
```kotlin
tasks.named<Test>("test") {
    useJUnitPlatform()
}
```
