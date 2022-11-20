Convert AsciiDoc document to AST using the IntelliJ AsciiDoc Plugin parser

## Build

* Clone the repository using `--recurse-submodules`
* Make sure that you have Java 11 installed (you can use [SDKMAN!](https://sdkman.io/usage))
* Build a jar

```
./gradlew app:shadowJar
```

## Usage

```
java --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED -jar app/build/libs/app-all.jar foo.adoc
```

## OS Package

NOTE: `jpackage` is only available on Java >= 17.

Create a .deb/.rpm/.msi/.dmg (depending on the OS you are running...):

```
jpackage -n asciidoc-ast --main-class net.pdp7.asciidoc.App --main-jar app-all.jar  --input app/build/libs/ --java-options "--add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.lang=ALL-UNNAMED"
```

You can also use --type app-image to create a self-contained directory with a binary.
