# java-documenter
Generates a Markdown file which contains documentation for specific java classes and methods.
> NOTE: This software does only work for java source files (.java) and **not** for compiled java files (.class)

## Installation
**From source:**\
`Prerequisites: Git, Maven`
1. Clone this repository (`git clone https://github.com/RealCerus/java-documenter`)
2. Build the jar (`mvn package`)
3. The final jar is in the `target` folder


**Releases:**\
There are currently no releases.

## Usage
`java -jar javadocsgenerator-VERSION.jar --files=FILENAME.java,ANOTHER_FILENAME.java,... --output=JAVA_DOCS.md`

Arguments:\
`--files`: Required. Specifies the files that should be used for documentation generation.\
`--output`: Optional. Will default to `./JAVA_DOC.md`. Specifies the file in which the generated documentation should be saved.

## Todo
- Make a GitHub Action
- Implement feature requests

## License
This project is licensed under the GPLv3.