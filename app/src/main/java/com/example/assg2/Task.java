package com.example.assg2;

public class Task {
    private String id;
    private String description;
    private String priority;

    public Task() {}

    public Task(String id, String description, String priority) {
        this.id = id;
        this.description = description;
        this.priority = priority;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getPriority() {
        return priority;
    }
}
