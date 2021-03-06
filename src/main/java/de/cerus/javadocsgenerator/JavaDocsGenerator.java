package de.cerus.javadocsgenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class JavaDocsGenerator {

    // Entry point
    public static final void main(String[] args) {
        // Map the program arguments into a list
        List<String> argsList = Arrays.asList(args);

        // Parse files
        List<File> fileList = argsList.stream()
                .filter(arg -> arg.startsWith("--files="))
                .flatMap(arg -> Arrays.stream(arg.substring(8).split(",")))
                .flatMap(path -> new FileWrapper(path).getFiles().stream())
                .filter(File::exists)
                .collect(Collectors.toList());
        if (fileList.isEmpty()) {
            // No files found
            System.err.println("No files provided. Use --files=Filename.java,Filename2.java");
            return;
        }

        System.out.println("Found " + fileList.size() + " files (" + fileList.stream()
                .map(File::getName).collect(Collectors.joining(", ")) + ")");

        // Parsing output file
        // If no output file is specified a default one will be used
        File outputFile = argsList.stream()
                .filter(arg -> arg.startsWith("--output="))
                .map(arg -> new File(arg.substring(9)))
                .findAny()
                .orElse(new File("./JAVA_DOC.md"));
        if (!outputFile.exists()) {
            // Try to create the output file
            try {
                outputFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("A severe error occurred while trying to create the output file.");
                return;
            }
        }

        System.out.println("Starting generation.. Writing to " + outputFile.getAbsolutePath());

        StringBuilder markdownPageBuilder = new StringBuilder("# Class documentation\n" +
                "*Automatically generated by JavaDocsGenerator*\n\n");

        int index = 1;
        int max = fileList.size();

        for (File file : fileList) {
            System.out.println("Scanning " + file.getName() + "... [" + (index++) + "/" + max + "]");

            try {
                // Trying to instantiate a new FileScanner object
                FileScanner fileScanner = new FileScanner(file);

                // Parsing the file contents
                List<Documentation> documentations = fileScanner.scan();
                if (documentations == null) {
                    System.err.println("Found no documentations for file " + file.getName());
                    continue;
                }

                // Appending the file name to the StringBuilder
                markdownPageBuilder.append("## ").append(file.getName().replace(".java", "")).append("\n");
                if (documentations.isEmpty()) {
                    markdownPageBuilder.append("*No methods found*\n\n");
                    continue;
                }

                // Appending the parsed documentation to the StringBuilder
                for (Documentation documentation : documentations) {
                    System.out.println("Found " + documentation.toString());
                    markdownPageBuilder.append(documentation.toMarkdownString()).append("\n\n");
                }
            } catch (IOException e) {
                System.err.println("Failed to scan file " + file.getName() + " for documentation, skipping");
            }
        }

        // Trying to write the parsed documentation to the output file
        try {
            FileWriter fileWriter = new FileWriter(outputFile);
            fileWriter.write(markdownPageBuilder.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Failed to write to output file.");
        }
    }

}
