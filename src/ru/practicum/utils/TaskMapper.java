package ru.practicum.utils;

import ru.practicum.enums.TaskType;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.List;

public class TaskMapper {

    private static final String FILE_HEADER = "id,type,name,status,description,epic,startTime,endTime,duration";

    public static String taskToString(List<Task> allTasks) {
        StringBuilder tasksString = new StringBuilder();
        tasksString.append(FILE_HEADER + "\n");
        for (Task task : allTasks) {
            tasksString.append(String.format("%d,%s,%s,%s,%s,%s,%s,%s,%s" + "\n",
                    task.getId(),
                    getTaskType(task),
                    task.getName(),
                    task.getStatus(),
                    task.getDescription(),
                    task instanceof Subtask ? ((Subtask) task).getEpicId() : "",
                    task.getStartTimeString() != null ? task.getStartTimeString() : "",
                    task.getEndTimeString() != null ? task.getEndTimeString() : "",
                    task.getDuration()));
        }
        return tasksString.toString();
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