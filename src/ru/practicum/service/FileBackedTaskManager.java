package ru.practicum.service;

import ru.practicum.enums.TaskStatus;
import ru.practicum.enums.TaskType;
import ru.practicum.exceptions.ManagerLoadException;
import ru.practicum.exceptions.ManagerSaveException;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.utils.HistoryMapper;
import ru.practicum.utils.TaskMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws ManagerLoadException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            String[] lines = content.split("\n");
            int maxIdInFile = getMaxIdInFile(lines);
            boolean isHistory = false;
            for (int i = 1; i < lines.length; i++) {
                if (lines[i].isBlank()) {
                    isHistory = true;
                    continue;
                }
                if (!isHistory) {
                    Task task = TaskMapper.fromString(lines[i]);
                    switch (TaskMapper.getTaskType(task)) {
                        case TASK -> {
                            fileBackedTaskManager.createTask(task);
                        }
                        case EPIC -> {
                            fileBackedTaskManager.createEpic((Epic) task);
                        }
                        case SUBTASK -> {
                            fileBackedTaskManager.createSubtask((Subtask) task);
                            fileBackedTaskManager.epics.get(((Subtask) task).getEpicId()).addSubtask((Subtask) task);
                        }
                        default -> throw new IllegalArgumentException("Тип не существует: " + TaskMapper.getTaskType(task));
                    }
                } else {
                    List<Integer> historyList = HistoryMapper.historyFromString(lines[i]);
                    for (Integer taskId : historyList) {
                        if (fileBackedTaskManager.tasks.containsKey(taskId)) {
                            historyManager.add(fileBackedTaskManager.tasks.get(taskId));
                        } else if (fileBackedTaskManager.epics.containsKey(taskId)) {
                            historyManager.add(fileBackedTaskManager.epics.get(taskId));
                        } else if (fileBackedTaskManager.subtasks.containsKey(taskId)) {
                            historyManager.add(fileBackedTaskManager.subtasks.get(taskId));
                        }
                    }
                }
            }
            fileBackedTaskManager.setStartGenerateTaskId(maxIdInFile + 1);
            return fileBackedTaskManager;
        } catch (IOException e) {
            throw new ManagerLoadException("Произошла ошибка при чтении файла", e);
        }
    }

    //Удаление всех задач
    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    //Получение по идентификатору
    @Override
    public Task getTaskById(int id) {
        Task task = super.getTaskById(id);
        save();
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = super.getEpicById(id);
        save();
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = super.getSubtaskById(id);
        save();
        return subtask;
    }

    //создание задач
    @Override
    public Task createTask(Task task) {
        Task createdTask = super.createTask(task);
        save();
        return createdTask;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic createdEpic = super.createEpic(epic);
        save();
        return createdEpic;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask createdSubtask = super.createSubtask(subtask);
        save();
        return createdSubtask;
    }

    //Обновление
    @Override
    public boolean updateTask(Task task) {
        boolean isUpdateTask = super.updateTask(task);
        if (isUpdateTask) {
            save();
        }
        return isUpdateTask;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        boolean isUpdateEpic = super.updateEpic(epic);
        if (isUpdateEpic) {
            save();
        }
        return isUpdateEpic;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        boolean isUpdateSubtask = super.updateSubtask(subtask);
        if (isUpdateSubtask) {
            save();
        }
        return isUpdateSubtask;
    }

    //Удаление по идентификатору
    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    private void save() {
        List<Task> allTask = new ArrayList<>();
        allTask.addAll(getAllTasks());
        allTask.addAll(getAllEpics());
        allTask.addAll(getAllSubtasks());
        allTask.sort(Comparator.comparingInt(Task::getId));
        try (FileWriter fileWriter = new FileWriter(file.toString(), StandardCharsets.UTF_8)) {
            if (!allTask.isEmpty()) {
                fileWriter.write(TaskMapper.taskToString(allTask));
            }
            if (!getHistory().isEmpty()) {
                fileWriter.write("\n" + HistoryMapper.historyToString(getHistory()));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи в файл.", e);
        }
    }

    private static int getMaxIdInFile(String[] lines) {
        int maxId = 1;
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].isBlank()) {
                break;
            }
            String[] parts = lines[i].split(",");
            int id = Integer.parseInt(parts[0]);
            maxId = Math.max(maxId, id);
        }
        return maxId;
    }
}