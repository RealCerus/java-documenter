package de.cerus.javadocsgenerator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileScanner {

    private List<String> contents;

    public FileScanner(File file) throws IOException {
        contents = Files.readAllLines(file.toPath());
    }

    // Scans the file contents for any documentation
    public List<Documentation> scan() {
        // Looping through every line
        for (int lineNo = 0; lineNo < contents.size(); lineNo++) {
            String line = contents.get(lineNo);

            // Checks if line is a valid class declaration, returns null if no '@DocumentationScan' annotation is specified
            if (line.matches("(private |public |protected |)(static )?(abstract )?(final )?class [A-Za-z0-9_]+ \\{")) {
                String lineBefore = contents.get(lineNo - 1);
                if (!lineBefore.equalsIgnoreCase("@DocumentationScan")) return null;
            }
        }

        List<Documentation> documentations = new ArrayList<>();

        // Loops through every line
        for (int lineNo = 0; lineNo < contents.size(); lineNo++) {
            String line = contents.get(lineNo);

            // Checks if line is a valid method head
            // If true: Scans the method head
            if (line.trim().matches("(private |public |protected |)((native )?(abstract )?(strictfp )" +
                    "?(synchronized )?(static )?(final )?)[A-Za-z0-9_ <>?]+\\(([A-Za-z0-9_\\[\\]., <>?]+)?\\)( \\{|;)")) {
                scanMethod(lineNo, documentations);
            }
        }

        return documentations;
    }

    // Scans a method head and parses it
    private void scanMethod(int lineNo, List<Documentation> documentations) {
        boolean flag = false;

        // Set flag if method has '@DocumentationScan' annotation
        if (contents.get(lineNo - 1).trim().equalsIgnoreCase("@DocumentationScan")) {
            flag = true;
        }

        List<String> descList = new ArrayList<>();

        // If no '@DocumentationScan' annotation is found check for a valid java doc comment
        if (!flag) {
            int ogLineNo = lineNo;
            String prevLine = contents.get(lineNo - 1);
            while (prevLine.trim().startsWith("@"))
                prevLine = contents.get(--lineNo);
            //lineNo = ogLineNo;

            // Checks if the line is a valid ending of a java doc comment
            if (prevLine.trim().equals("*/")) {
                lineNo -= 2;
                String line;

                // Loops through every line of the java doc comment
                // Adds the line to the description list
                while ((line = contents.get(lineNo)).trim().startsWith("*")) {
                    descList.add(line.substring(line.indexOf("*") + 1).trim());
                    lineNo--;
                }

                // Checks if the line is a valid beginning of a java doc comment
                // If true: Sets flag to true
                if (contents.get(lineNo).trim().equals("/**")) {
                    flag = true;
                    lineNo = ogLineNo;
                    Collections.reverse(descList);
                }
            }
        }

        // Returns if neither a '@DocumentationScan' nor a valid java doc comment is found
        if (!flag) return;

        String line = contents.get(lineNo).trim();

        // Parses the access modifier
        Documentation.AccessModifier accessModifier;
        if (line.contains("public")) {
            accessModifier = Documentation.AccessModifier.PUBLIC;
        } else if (line.contains("private")) {
            accessModifier = Documentation.AccessModifier.PRIVATE;
        } else if (line.contains("protected")) {
            accessModifier = Documentation.AccessModifier.PROTECTED;
        } else {
            accessModifier = Documentation.AccessModifier.PACKAGE_PRIVATE;
        }
        line = line.replace(accessModifier.toString(), "");

        // Parses every other modifier
        boolean abstractModifier = line.contains("abstract");
        if (abstractModifier) line = line.replace("abstract ", "");
        boolean finalModifier = line.contains("final");
        if (finalModifier) line = line.replace("final ", "");
        boolean staticModifier = line.contains("static");
        if (staticModifier) line = line.replace("static ", "");
        boolean syncModifier = line.contains("sync");
        if (syncModifier) line = line.replace("sync ", "");
        boolean strictfpModifier = line.contains("strictfp");
        if (strictfpModifier) line = line.replace("strictfp ", "");
        boolean nativeModifier = line.contains("native");
        if (nativeModifier) line = line.replace("native ", "");

        // Parsed the return type
        String returnType = line.trim().substring(0, line.indexOf(" "));
        line = line.substring(returnType.length() + 1);

        // Parses the method name
        String name = line.substring(0, line.indexOf("("));
        line = line.substring(name.length() + 1, line.indexOf(")"));

        line = line.replaceAll(",\\s+", ",");

        // Parses the method parameters
        String[] rawParams = line.split(",");
        Documentation.Parameter[] parameters = new Documentation.Parameter[rawParams.length];
        for (int i = 0; i < rawParams.length; i++) {
            String rawParam = rawParams[i].split(" ")[0];
            boolean array = rawParam.endsWith("[]") || rawParam.endsWith("...");
            boolean varargs = rawParam.endsWith("...");
            parameters[i] = new Documentation.Parameter(rawParam, array, varargs);
        }

        // Adds the parsed documentation to the documentations list
        documentations.add(new Documentation(accessModifier, finalModifier, staticModifier, abstractModifier,
                syncModifier, nativeModifier, strictfpModifier, returnType, name, parameters,
                descList.isEmpty() ? "" : String.join("\n", descList)));
    }
}
