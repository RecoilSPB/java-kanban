package ru.practicum.service;

import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.model.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int taskIdCounter; // Переменная для генерации уникальных идентификаторов
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;
    private final HistoryManager historyManager;

    public InMemoryTaskManager() {
        taskIdCounter = 1; // Начальное значение счетчика
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    // Методы для ru.practicum.model.Task
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        ArrayList<Integer> taskIds = new ArrayList<>(tasks.keySet());
        for (Integer taskId :taskIds) {
            deleteTaskById(taskId);
        }
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public void createTask(Task task) {
        task.setId(taskIdCounter);
        tasks.put(taskIdCounter, task);
        taskIdCounter++;
    }

    @Override
    public void updateTask(Task updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);
    }

    @Override
    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    //Методы для ru.practicum.model.Epic
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        ArrayList<Integer> epicIds = new ArrayList<>(epics.keySet());
        for (Integer epicId : epicIds){
            deleteEpicById(epicId);
        }
    }

    @Override
    public Epic getEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        historyManager.add(epic);
        return epic;
    }

    @Override
    public void createEpic(Epic epic) {
        epic.setId(taskIdCounter);
        epics.put(taskIdCounter, epic);
        taskIdCounter++;
    }

    @Override
    public void updateEpic(Epic epic) {
        TaskStatus status = calculateEpicStatus(epic);
        epic.setStatus(status);
        tasks.put(epic.getId(), epic);
    }

    @Override
    public void deleteEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        epics.remove(epicId);
        for (Subtask subtask : epic.getSubtasks()){
            deleteSubtaskById(subtask.getId());
        }
    }

    // Методы для ru.practicum.model.Subtask
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        ArrayList<Integer> subtaskIds = new ArrayList<>(subtasks.keySet());
        for (Integer subtaskId : subtaskIds){
            deleteSubtaskById(subtaskId);
        }
    }

    @Override
    public Subtask getSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void createSubtask(Subtask subtask) {
        subtask.setId(taskIdCounter);
        subtasks.put(taskIdCounter, subtask);
        taskIdCounter++;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        tasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        TaskStatus status = calculateEpicStatus(epic);
        epic.setStatus(status);
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        subtasks.remove(subtask.getId());
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubtask(subtask);
            updateEpic(epic);
        }
    }

    // Получение списка всех подзадач определённого эпика
    @Override
    public List<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        return epic.getSubtasks();
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    // Управление статусами
    private TaskStatus calculateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasks = epic.getSubtasks();
        boolean allSubtasksDone = true;
        boolean allSubtasksNew = true;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != TaskStatus.DONE) {
                allSubtasksDone = false;
            }
            if (subtask.getStatus() != TaskStatus.NEW) {
                allSubtasksNew = false;
            }
        }

        if (subtasks.isEmpty() || allSubtasksNew) {
            return TaskStatus.NEW;
        } else if (allSubtasksDone) {
            return TaskStatus.DONE;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }

}
