package de.cerus.javadocsgenerator;

import com.google.gson.JsonObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

public class JavaDocsGenerator {

    // Entry point
    public static final void main(String[] args) {
        // Map the program arguments into a list
        List<String> argsList = Arrays.asList(args);

        // Parse files
        List<File> fileList = argsList.contains("--files=*") ? new ArrayList<>()
                : argsList.stream()
                .filter(arg -> arg.startsWith("--files="))
                .flatMap(arg -> Arrays.stream(arg.substring(8).split(",")))
                .filter(fileName -> fileName.endsWith(".java"))
                .map(File::new)
                .filter(File::exists)
                .collect(Collectors.toList());
        if (argsList.contains("--files=*")) {
            File ignoredFolders = new File("JAVADOCSGEN_IGNORED.txt");
            if (!ignoredFolders.exists()) {
                try {
                    ignoredFolders.createNewFile();
                    Files.write(ignoredFolders.toPath(), Arrays.asList("java-documenter"), StandardOpenOption.WRITE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            List<String> ignoredFoldersList;
            try {
                ignoredFoldersList = Files.readAllLines(ignoredFolders.toPath()).stream()
                        .filter(s -> !s.equals(""))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }

            File file = new File(".");
            visitAndAddFiles(file, fileList, ignoredFoldersList);
        }
        if (fileList.isEmpty()) {
            // No files found
            System.err.println("No files provided. Use --files=Filename.java,Filename2.java");
            return;
        }

        System.out.println("Found " + fileList.size() + " files");

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

        // Commit the generated docs to a repository
        if (argsList.contains("--commit")) {
            String githubToken = System.getenv("GITHUB_TOKEN");
            String repo = System.getenv("GITHUB_REPOSITORY");

            if (githubToken == null || repo == null) {
                System.err.println("Cannot commit: Secret / Repo env is null");
                return;
            }

            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://api.github.com/repos/"
                        + repo + "/contents/" + outputFile.getName()).openConnection();

                connection.setRequestMethod("PUT");
                connection.setRequestProperty("Authorization", "token " + githubToken);
                connection.setRequestProperty("user-agent", "JavaDocsGen");
                connection.setRequestProperty("accept", "application/vnd.github.v3+json");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("message", "Add generated docs");
                jsonObject.addProperty("content", Base64.getEncoder()
                        .encodeToString(markdownPageBuilder.toString().getBytes(StandardCharsets.UTF_8)));

                connection.setDoOutput(true);

                OutputStream outputStream = connection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream);
                outputStreamWriter.write(jsonObject.toString());
                outputStreamWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void visitAndAddFiles(File file, List<File> fileList, List<String> ignoredFoldersList) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File file1 : files) {
                if (file1.isDirectory()) {
                    if (ignoredFoldersList.contains(file1.getName())) continue;
                    visitAndAddFiles(file1, fileList, ignoredFoldersList);
                    continue;
                }

                if (file1.getName().endsWith(".java")) {
                    fileList.add(file1);
                }
            }
        }
    }

}
