package de.cerus.javadocsgenerator;

import com.esotericsoftware.wildcard.Paths;

import java.io.File;
import java.util.List;

public class FileWrapper {

    private String path;

    public FileWrapper(String path) {
        this.path = path;
    }

    public List<File> getFiles() {
        Paths paths = new Paths(path);
        return paths.getFiles();
    }

    public String getPath() {
        return path;
    }
}
