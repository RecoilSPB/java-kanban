package ru.practicum.model;

import ru.practicum.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, Epic epic) {
        super(name, description, TaskStatus.NEW);
        this.epicId = epic.getId();
        epic.addSubtask(this);
    }

    public Subtask(String name, String description, Epic epic, LocalDateTime startTime, Duration duration) {
        super(name, description, TaskStatus.NEW, startTime, duration);
        this.epicId = epic.getId();
        epic.addSubtask(this);
    }

    public Subtask(String name, String description, Epic epic, TaskStatus status,
                   LocalDateTime startTime, Duration duration) {
        super(name, description, status, startTime, duration);
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