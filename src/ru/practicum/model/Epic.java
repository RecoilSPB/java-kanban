package ru.practicum.model;

import ru.practicum.enums.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    protected final ArrayList<Subtask> subtasks;
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description, TaskStatus.NEW);
        this.subtasks = new ArrayList<>();
        this.endTime = LocalDateTime.MIN;
    }

    public Epic(String name, String description, LocalDateTime startTime, Duration duration) {
        super(name, description, TaskStatus.NEW, startTime, duration);
        this.subtasks = new ArrayList<>();
        this.endTime = LocalDateTime.MIN;
    }

    public Epic(String name, String description, LocalDateTime startTime, Duration duration, LocalDateTime endTime) {
        super(name, description, TaskStatus.NEW, startTime, duration);
        this.subtasks = new ArrayList<>();
        this.endTime = endTime;
    }

    public void addSubtask(Subtask newSubtask) {
        this.subtasks.add(newSubtask);
        Duration totalDuration = Duration.ZERO;
        LocalDateTime earliestStartTime = LocalDateTime.MAX;
        LocalDateTime latestEndTime = LocalDateTime.MIN;

        for (Subtask subtask : this.subtasks) {
            totalDuration = totalDuration.plus(subtask.getDuration());
            LocalDateTime subtaskStartTime = subtask.getStartTime();
            LocalDateTime subtaskEndTime = subtask.getEndTime();

            if (!subtaskStartTime.equals(LocalDateTime.MIN) && subtaskStartTime.isBefore(earliestStartTime)) {
                earliestStartTime = subtaskStartTime;
            }
            if (subtaskEndTime.isAfter(latestEndTime)) {
                latestEndTime = subtaskEndTime;
            }
        }

        this.duration = totalDuration;
        this.startTime = earliestStartTime.equals(LocalDateTime.MAX) ? null : earliestStartTime;
        this.endTime = latestEndTime.equals(LocalDateTime.MIN) ? null : latestEndTime;
    }


    @Override
    public LocalDateTime getEndTime() {
        return endTime;
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
