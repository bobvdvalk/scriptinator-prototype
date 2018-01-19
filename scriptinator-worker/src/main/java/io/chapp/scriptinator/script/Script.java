package io.chapp.scriptinator.script;

public class Script {
    private final String file;
    private final String directory;

    public Script(String file, String directory) {
        this.file = file;
        this.directory = directory;
    }

    public String getFile() {
        return file;
    }

    public String getDirectory() {
        return directory;
    }
}
