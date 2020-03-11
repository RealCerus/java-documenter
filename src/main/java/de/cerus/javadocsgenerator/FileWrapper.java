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
        File file = new File(path);
        Paths paths = new Paths(file.getParentFile() != null ? file.getParentFile().getPath() : ".", file.getName());
        return paths.getFiles();
    }

    public String getPath() {
        return path;
    }
}
