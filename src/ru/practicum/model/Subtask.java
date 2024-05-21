package ru.practicum.model;

import ru.practicum.enums.TaskStatus;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, Epic epic) {
        super(name, description, TaskStatus.NEW);
        this.epicId = epic.getId();
        epic.addSubtask(this);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return getClass().getName() + "{" +
                "id='" + getId() + '\'' +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", epicId='" + epicId + '\'' +
                '}';
    }
}