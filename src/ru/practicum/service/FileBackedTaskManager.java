package ru.practicum.service;

import ru.practicum.enums.TaskStatus;
import ru.practicum.enums.TaskType;
import ru.practicum.exceptions.ManagerLoadException;
import ru.practicum.exceptions.ManagerSaveException;
import ru.practicum.model.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void loadFromFile(File file) throws ManagerLoadException {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try {
            String content = Files.readString(file.toPath(), StandardCharsets.UTF_8);
            String[] lines = content.split("\n");
            int maxIdInFile = getMaxIdInFile(lines);
            fileBackedTaskManager.setStartGenerateTaskId(maxIdInFile + 1);
            boolean isHistory = false;
            for (int i = 1; i < lines.length; i++) {
                if (lines[i].isBlank()) {
                    isHistory = true;
                    continue;
                }
                if (!isHistory) {
                    fromString(lines[i]);
                } else {
                    List<Integer> historyList = historyFromString(lines[i]);
                    for (Integer taskId : historyList) {
                        if (tasks.containsKey(taskId)) {
                            historyManager.add(tasks.get(taskId));
                        } else if (epics.containsKey(taskId)) {
                            historyManager.add(epics.get(taskId));
                        } else if (subtasks.containsKey(taskId)) {
                            historyManager.add(subtasks.get(taskId));
                        }
                    }
                }
            }
            for (Subtask subtask : subtasks.values()) {
                int epicId = subtask.getEpicId();
                Epic epic = epics.get(epicId);
                if (epic != null) {
                    epic.addSubtask(subtask);
                }
            }
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

    private static String historyToString(List<Task> history) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < history.size(); i++) {
            builder.append(history.get(i).getId());
            if (i < history.size() - 1) {
                builder.append(", ");
            }
        }
        return builder.toString();
    }

    private static List<Integer> historyFromString(String value) {
        List<Integer> historyIds = new ArrayList<>();
        String[] idTasks = value.split(", ");
        for (String id : idTasks) {
            historyIds.add(Integer.parseInt(id));
        }
        return historyIds;
    }

    private void save() {
        List<Task> allTask = new ArrayList<>();
        allTask.addAll(getAllTasks());
        allTask.addAll(getAllEpics());
        allTask.addAll(getAllSubtasks());
        allTask.sort(Comparator.comparingInt(Task::getId));
        try (FileWriter fileWriter = new FileWriter(file.toString(), StandardCharsets.UTF_8)) {
            fileWriter.write("id,type,name,status,description,epic\n");
            for (Task task : allTask) {
                fileWriter.write(String.format("%d,%s,%s,%s,%s,%s" + "\n",
                        task.getId(),
                        getTaskType(task),
                        task.getName(),
                        task.getStatus(),
                        task.getDescription(),
                        task instanceof Subtask ? ((Subtask) task).getEpicId() : "")
                );
            }
            if (!getHistory().isEmpty()) {
                fileWriter.write("\n" + historyToString(getHistory()));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Произошла ошибка во время записи в файл.", e);
        }
    }

    private void fromString(String value) {
        try {
            String[] parts = value.split(",");
            if (parts.length < 4) {
                throw new IllegalArgumentException("Неверный формат задачи");
            }
            int id = Integer.parseInt(parts[0]);
            String type = parts[1];
            String name = parts[2];
            TaskStatus status = TaskStatus.valueOf(parts[3]);
            String description = parts.length > 4 ? parts[4] : "";
            switch (type) {
                case "TASK" -> {
                    Task task = createTask(new Task(name, description, TaskStatus.NEW));
                    tasks.put(id, task);
                }
                case "EPIC" -> {
                    Epic epic = createEpic(new Epic(name, description));
                    epic.setStatus(status);
                    epics.put(id, epic);
                }
                case "SUBTASK" -> {
                    int epicId = parts.length > 5 ? Integer.parseInt(parts[5]) : 0;
                    Subtask subtask = createSubtask(new Subtask(name, description, epics.get(epicId)));
                    subtasks.put(id, subtask);
                }
                default -> throw new IllegalArgumentException("Тип не существует: " + type);
            }
        } catch (Exception e) {
            throw new ManagerLoadException("Произошла ошибка во время парсинга строки из файла.");
        }
    }

    private int getMaxIdInFile(String[] lines) {
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

    private Object getTaskType(Task task) {
        if (task instanceof Epic) {
            return TaskType.EPIC;
        } else if (task instanceof Subtask) {
            return TaskType.SUBTASK;
        } else {
            return TaskType.TASK;
        }
    }
}