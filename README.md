[![GitHub](https://img.shields.io/github/license/RealCerus/java-documenter)](https://github.com/RealCerus/java-documenter/blob/master/LICENSE) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/098ee136153a4f44bcb68000aecadcf9)](https://www.codacy.com/manual/RealCerus/java-documenter?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=RealCerus/java-documenter&amp;utm_campaign=Badge_Grade) ![Discord](https://img.shields.io/discord/405752989182197760)<!-- ALL-CONTRIBUTORS-BADGE:START - Do not remove or modify this section --> [![All Contributors](https://img.shields.io/badge/all_contributors-1-orange.svg?style=flat)](#contributors-) <!-- ALL-CONTRIBUTORS-BADGE:END -->

# java-documenter
Generates Java documentation in form of a Markdown file.
> NOTE: This software does only work for java source files (.java) and **not** for compiled java files (.class)

## Installation
**From source:**\
`Prerequisites: Git, Maven`
1. Clone this repository (`git clone https://github.com/RealCerus/java-documenter`)
2. Build the jar (`mvn package`)
3. The final jar is in the `target` folder

**Releases:**\
[![GitHub Releases](https://img.shields.io/github/downloads/RealCerus/java-documenter/latest/total)](https://github.com/RealCerus/java-documenter/releases/latest)

## Usage
`java -jar javadocsgenerator-VERSION.jar --files=FILENAME.java,ANOTHER_FILENAME.java,... --output=JAVA_DOCS.md`
> NOTE: To use a class for documentation generation add a '@DocumentationScan' annotation over the class declaration and over every method which you want to use. Alternatively you can use java doc comments for methods.

Arguments:\
`--files`: Required. Specifies the files that should be used for documentation generation.\
`--output`: Optional. Will default to `./JAVA_DOC.md`. Specifies the file in which the generated documentation should be saved.

## Examples
[src/main/java/example](https://github.com/RealCerus/java-documenter/tree/master/src/main/java/example)

## Todo
-   ~~Add wildcard support for file names~~
-   ~~Implement some sort of annotation system to replace the '//DOC' comment (It's ugly)~~
-   Parse stuff like '@since 1.0.0' or '@param xy' from java doc comments
-   Make a GitHub Action
-   Make a Maven plugin
-   Implement feature requests

## Contributors ✨

Thanks goes to these wonderful people ([emoji key](https://allcontributors.org/docs/en/emoji-key)):

<!-- ALL-CONTRIBUTORS-LIST:START - Do not remove or modify this section -->
<!-- prettier-ignore-start -->
<!-- markdownlint-disable -->
<table>
  <tr>
    <td align="center"><a href="https://cerus-dev.de"><img src="https://avatars3.githubusercontent.com/u/46848982?v=4" width="100px;" alt=""/><br /><sub><b>Maximilian D.</b></sub></a><br /><a href="https://github.com/RealCerus/java-documenter/commits?author=RealCerus" title="Code">💻</a> <a href="https://github.com/RealCerus/java-documenter/commits?author=RealCerus" title="Documentation">📖</a> <a href="#example-RealCerus" title="Examples">💡</a></td>
  </tr>
</table>

<!-- markdownlint-enable -->
<!-- prettier-ignore-end -->
<!-- ALL-CONTRIBUTORS-LIST:END -->

This project follows the [all-contributors](https://github.com/all-contributors/all-contributors) specification. Contributions of any kind welcome!

## License
This project is licensed under the [GPLv3](https://github.com/RealCerus/java-documenter/blob/master/LICENSE).
