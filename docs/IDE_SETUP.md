# IDE Setup Guide

This guide helps you configure your IDE for sb-oauth-java development.

## EditorConfig Support

This project uses [EditorConfig](https://editorconfig.org/) to maintain consistent coding styles. Most modern IDEs support it out of the box or via plugins.

### Automatic Configuration

The `.editorconfig` file in the project root automatically configures:
- **Indentation**: Tabs for Java/XML, spaces for YAML/JSON/Markdown
- **Line endings**: LF (Unix-style)
- **Charset**: UTF-8
- **Max line length**: 120 characters for Java
- **Trailing whitespace**: Auto-trim (except Markdown)

## IntelliJ IDEA

### EditorConfig Plugin

EditorConfig is built-in since IDEA 2017.1+. No plugin needed!

### Java Code Style

1. Go to **Settings** → **Editor** → **Code Style** → **Java**
2. Verify settings:
   - **Tabs and Indents**: Use tab character (indent size: 4)
   - **Wrapping and Braces**: Right margin: 120
   - **Imports**: Optimize imports on the fly

### Recommended Plugins

```
- Lombok Plugin (required)
- SonarLint (code quality)
- CheckStyle-IDEA (optional)
- Rainbow Brackets (optional, for readability)
```

### Import Project

```bash
# Open IntelliJ IDEA
File → Open → Select /path/to/sb-oauth-java/pom.xml
```

IntelliJ will automatically detect it as a Maven project and import all modules.

### Run Configuration

**Run Tests:**
```
Right-click on test class → Run 'ClassName'
```

**Maven Commands:**
```
View → Tool Windows → Maven
# Double-click on goals: clean, compile, test, install
```

### Lombok Setup

1. Install Lombok plugin (if not already installed)
2. Enable annotation processing:
   ```
   Settings → Build, Execution, Deployment → Compiler → Annotation Processors
   ✓ Enable annotation processing
   ```

## Eclipse

### EditorConfig Plugin

Install from Eclipse Marketplace:
```
Help → Eclipse Marketplace → Search "EditorConfig"
Install "EditorConfig Eclipse"
Restart Eclipse
```

### Import Project

```bash
File → Import → Maven → Existing Maven Projects
Select /path/to/sb-oauth-java
Finish
```

### Lombok Setup

1. Download `lombok.jar` from https://projectlombok.org/download
2. Run: `java -jar lombok.jar`
3. Select Eclipse installation directory
4. Install/Update
5. Restart Eclipse

### Code Formatter

Import code style:
```
Window → Preferences → Java → Code Style → Formatter
Import → Select eclipse-formatter.xml (if provided)
```

## Visual Studio Code

### Required Extensions

Install from Extensions Marketplace:

```json
{
  "recommendations": [
    "vscjava.vscode-java-pack",
    "vscjava.vscode-maven",
    "EditorConfig.EditorConfig",
    "GabrielBB.vscode-lombok",
    "redhat.java",
    "vscjava.vscode-java-test"
  ]
}
```

### Java Extension Pack

Install the **Extension Pack for Java** which includes:
- Language Support for Java
- Debugging
- Testing
- Maven support
- Project management

### Settings

Create `.vscode/settings.json`:

```json
{
  "java.configuration.updateBuildConfiguration": "automatic",
  "java.compile.nullAnalysis.mode": "automatic",
  "java.format.settings.url": ".editorconfig",
  "editor.formatOnSave": true,
  "files.encoding": "utf8",
  "files.eol": "\n",
  "files.insertFinalNewline": true,
  "files.trimTrailingWhitespace": true,
  "[markdown]": {
    "files.trimTrailingWhitespace": false
  },
  "java.codeGeneration.useBlocks": true,
  "java.saveActions.organizeImports": true
}
```

### Build and Run

```bash
# Build
Ctrl+Shift+B (or Cmd+Shift+B on macOS)

# Run tests
Click on "Run Test" above test methods
```

## Command Line (Any Editor)

If you prefer lightweight editors (Vim, Emacs, Sublime Text, etc.):

### EditorConfig Plugin

Most editors have EditorConfig plugins available:
- **Vim**: `editorconfig/editorconfig-vim`
- **Emacs**: `editorconfig/editorconfig-emacs`
- **Sublime Text**: Install via Package Control

### Build Commands

```bash
# Build project
mvn clean install

# Run tests
mvn test

# Run specific test
mvn test -Dtest=ClassName

# Skip tests
mvn clean install -DskipTests=true

# Format code (if maven-formatter-plugin is configured)
mvn formatter:format

# Check code style (if checkstyle plugin is configured)
mvn checkstyle:check
```

## Verifying Your Setup

### Test EditorConfig

1. Open any `.java` file
2. Add a new line
3. Press `Tab` → Should insert tab character (not spaces)
4. Save file → Should trim trailing whitespace and add final newline

### Test Lombok

1. Open any class with Lombok annotations (`@Getter`, `@Builder`, etc.)
2. IDE should not show errors
3. Auto-completion should work for generated methods

### Test Maven

```bash
# From project root
mvn clean compile

# Should complete without errors
# Should download dependencies (first time)
```

## Common Issues

### Issue: Lombok not working in IntelliJ

**Solution:**
1. Check if Lombok plugin is installed
2. Enable annotation processing (Settings → Compiler → Annotation Processors)
3. Invalidate caches: File → Invalidate Caches / Restart

### Issue: EditorConfig not applied

**Solution:**
1. Restart IDE
2. Check if EditorConfig plugin is enabled
3. Verify `.editorconfig` file is in project root

### Issue: Maven dependencies not resolved

**Solution:**
```bash
# Clean Maven cache
rm -rf ~/.m2/repository/org/scriptonbasestar

# Reimport
mvn clean install -U
```

### Issue: Wrong encoding (한글 깨짐)

**Solution:**
1. Check IDE encoding: Settings → Editor → File Encodings → UTF-8
2. Check Maven encoding: `project.build.sourceEncoding=UTF-8` in pom.xml
3. Set JVM encoding: Add `-Dfile.encoding=UTF-8` to VM options

## Project-Specific Settings

### Java Version

This project requires **Java 17 or higher** (Java 21 recommended).

Verify your IDE is using the correct JDK:

**IntelliJ:**
```
File → Project Structure → Project → Project SDK: 21
File → Project Structure → Modules → Language level: 21
```

**Eclipse:**
```
Window → Preferences → Java → Installed JREs
Project → Properties → Java Build Path → Libraries
```

**VS Code:**
```json
{
  "java.configuration.runtimes": [
    {
      "name": "JavaSE-21",
      "path": "/path/to/jdk-21",
      "default": true
    }
  ]
}
```

### Maven Settings

If you encounter download issues, configure Maven mirror:

`~/.m2/settings.xml`:
```xml
<settings>
  <mirrors>
    <mirror>
      <id>central</id>
      <url>https://repo1.maven.org/maven2</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>
</settings>
```

## Contributing Code Style

When contributing, ensure:
- Code passes `mvn clean install` without errors
- Follow the code style defined in `.editorconfig`
- Use Lombok for boilerplate code
- Write tests for new features
- Update documentation

See [CONTRIBUTING.md](../CONTRIBUTING.md) for detailed guidelines.

## Additional Resources

- [EditorConfig Documentation](https://editorconfig.org/)
- [IntelliJ IDEA Documentation](https://www.jetbrains.com/help/idea/)
- [Eclipse Documentation](https://help.eclipse.org/)
- [VS Code Java Documentation](https://code.visualstudio.com/docs/java/java-tutorial)
- [Maven Documentation](https://maven.apache.org/guides/)
- [Project Lombok](https://projectlombok.org/)
