package ru.practicum.service;

import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.List;

public interface TaskManager {
    // Методы для ru.practicum.model.Task
    List<Task> getAllTasks();

    void deleteAllTasks();

    Task getTaskById(int taskId);

    void createTask(Task task);

    void updateTask(Task updatedTask);

    void deleteTaskById(int taskId);

    //Методы для ru.practicum.model.Epic
    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int taskId);

    void createEpic(Epic epic);

    void updateEpic(Epic epic);

    void deleteEpicById(int epicId);

    // Методы для ru.practicum.model.Subtask
    List<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask getSubtaskById(int subtaskId);

    void createSubtask(Subtask subtask);

    void updateSubtask(Subtask subtask);

    void deleteSubtaskById(int subtaskId);

    // Получение списка всех подзадач определённого эпика
    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();
}
