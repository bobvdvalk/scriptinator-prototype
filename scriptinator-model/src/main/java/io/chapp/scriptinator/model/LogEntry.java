package io.chapp.scriptinator.model;

public class LogEntry {
    private String message;

    public LogEntry() {
    }

    public LogEntry(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
