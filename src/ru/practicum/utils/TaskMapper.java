package ru.practicum.utils;

import ru.practicum.enums.TaskStatus;
import ru.practicum.enums.TaskType;
import ru.practicum.exceptions.ManagerLoadException;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.service.HistoryManager;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class TaskMapper {

    public static String taskToString(List<Task> allTasks) {
        StringBuilder tasksString = new StringBuilder();
        tasksString.append("id,type,name,status,description,epic,startTime,endTime,duration\n");
        for (Task task : allTasks) {
            tasksString.append(String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s" + "\n",
                    task.getId(),
                    getTaskType(task),
                    task.getName(),
                    task.getStatus(),
                    task.getDescription(),
                    task instanceof Subtask ? ((Subtask) task).getEpicId() : "",
                    task.getStartTime() != null ? task.getStartTime() : "",
                    task.getEndTimeString() != null ? task.getEndTimeString() : "",
                    task.getDuration())
            );
        }
        return tasksString.toString();
    }

    public static Task fromString(String value) {
        try {
            String[] parts = value.split(",");
            if (parts.length < 4) {
                throw new IllegalArgumentException("Неверный формат задачи");
            }
            TaskType type = TaskType.valueOf(parts[1]);
            String name = parts[2];
            TaskStatus status = TaskStatus.valueOf(parts[3]);
            String description = parts.length > 4 ? parts[4] : "";
            LocalDateTime startTime = (parts.length > 6 &&
                    !parts[6].trim().isEmpty()) ? LocalDateTime.parse(parts[6].trim()) : LocalDateTime.now();
            Duration duration = (parts.length > 8 &&
                    !parts[8].trim().isEmpty()) ? Duration.parse(parts[8].trim()) : Duration.ZERO;
            switch (type) {
                case TASK -> {
                    return new Task(name, description, status, startTime, duration);
                }
                case EPIC -> {
                    Epic epic = new Epic(name, description, startTime, duration);
                    epic.setStatus(status);
                    return epic;
                }
                case SUBTASK -> {
                    int epicId = parts.length > 5 ? Integer.parseInt(parts[5]) : 0;
                    Subtask subtask = new Subtask(name, description, epicId, startTime, duration);
                    subtask.setStatus(status);
                    return subtask;
                }
                default -> throw new IllegalArgumentException("Тип не существует: " + type);
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ManagerLoadException("Произошла ошибка во время парсинга строки из файла.");
        }
    }

    public static TaskType getTaskType(Task task) {
        if (task instanceof Epic) {
            return TaskType.EPIC;
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK;
        } else {
            return TaskType.TASK;
        }
    }
}