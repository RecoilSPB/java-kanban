package ru.practicum.model;

import ru.practicum.enums.TaskStatus;

import java.util.ArrayList;

public class Epic extends Task {
    private final ArrayList<Subtask> subtasks;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        subtasks = new ArrayList<>();
    }

    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    public void removeSubtask(Subtask subtask) {
        subtasks.remove(subtask);
    }

    public ArrayList<Subtask> getSubtasks() {
        return subtasks;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasks=" + subtasks +
                '}';
    }
}
