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

    Task createTask(Task task);

    boolean updateTask(Task updatedTask);

    void deleteTaskById(int taskId);

    //Методы для ru.practicum.model.Epic
    List<Epic> getAllEpics();

    void deleteAllEpics();

    Epic getEpicById(int taskId);

    Epic createEpic(Epic epic);

    boolean updateEpic(Epic epic);

    void deleteEpicById(int epicId);

    // Методы для ru.practicum.model.Subtask
    List<Subtask> getAllSubtasks();

    void deleteAllSubtasks();

    Subtask getSubtaskById(int subtaskId);

    Subtask createSubtask(Subtask subtask);

    boolean updateSubtask(Subtask subtask);

    void deleteSubtaskById(int subtaskId);

    // Получение списка всех подзадач определённого эпика
    List<Subtask> getSubtasksByEpicId(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();

    void validate(Task newTask);
}
