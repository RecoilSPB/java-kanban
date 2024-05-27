package ru.practicum.service;

import ru.practicum.enums.TaskStatus;
import ru.practicum.exceptions.CollisionTaskException;
import ru.practicum.exceptions.InvalidTaskException;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    protected static final Comparator<Task> COMPARATOR = Comparator
            .comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()))
            .thenComparing(Task::getId);
    protected static HistoryManager historyManager;
    protected HashMap<Integer, Task> tasks;
    protected HashMap<Integer, Epic> epics;
    protected HashMap<Integer, Subtask> subtasks;
    protected Set<Task> prioritizedTasks;
    private int taskIdCounter; // Переменная для генерации уникальных идентификаторов

    public InMemoryTaskManager() {
        taskIdCounter = 1; // Начальное значение счетчика
        tasks = new HashMap<>();
        epics = new HashMap<>();
        subtasks = new HashMap<>();
        historyManager = Managers.getDefaultHistory();
        prioritizedTasks = new TreeSet<>(COMPARATOR);
    }

    // Методы для ru.practicum.model.Task
    @Override
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public Task getTaskById(int taskId) {
        Task task = tasks.get(taskId);
        historyManager.add(task);
        return task;
    }

    @Override
    public Task createTask(Task task) {
        validate(task);
        task.setId(taskIdCounter);
        tasks.put(taskIdCounter, task);
        prioritizedTasks.add(task);
        generateTaskId();
        return task;
    }

    @Override
    public boolean updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
            return true;
        }
        return false;
    }

    @Override
    public void deleteTaskById(int taskId) {
        prioritizedTasks.remove(tasks.get(taskId));
        tasks.remove(taskId);
        historyManager.remove(taskId);
    }

    //Методы для ru.practicum.model.Epic
    @Override
    public List<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public void deleteAllEpics() {
        ArrayList<Integer> epicIds = new ArrayList<>(epics.keySet());
        for (Integer epicId : epicIds) {
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
    public Epic createEpic(Epic epic) {
        validate(epic);
        epic.setId(taskIdCounter);
        epics.put(taskIdCounter, epic);
        setEpicDateTime(epic.getId());
        generateTaskId();
        return epic;
    }

    @Override
    public boolean updateEpic(Epic epic) {
        if (epics.containsKey(epic.getId())) {
            TaskStatus status = calculateEpicStatus(epic);
            epic.setStatus(status);
            epics.put(epic.getId(), epic);
            return true;
        }
        return false;
    }

    @Override
    public void deleteEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        epics.remove(epicId);
        for (Subtask subtask : epic.getSubtasks()) {
            deleteSubtaskById(subtask.getId());
        }
        historyManager.remove(epicId);
    }

    // Методы для ru.practicum.model.Subtask
    @Override
    public List<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllSubtasks() {
        ArrayList<Integer> subtaskIds = new ArrayList<>(subtasks.keySet());
        for (Integer subtaskId : subtaskIds) {
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
    public Subtask createSubtask(Subtask subtask) {
        validate(subtask);
        int epicId = subtask.getEpicId();
        if (epicId == 0 || !epics.containsKey(epicId)){
            throw new InvalidTaskException("Epic не найден.");
        }
        subtask.setId(taskIdCounter);
        subtasks.put(taskIdCounter, subtask);
        prioritizedTasks.add(subtask);
        setEpicDateTime(subtask.getEpicId());
        generateTaskId();
        return subtask;
    }

    @Override
    public boolean updateSubtask(Subtask subtask) {
        if (subtasks.containsKey(subtask.getId())) {
            tasks.put(subtask.getId(), subtask);
            Epic epic = epics.get(subtask.getEpicId());
            TaskStatus status = calculateEpicStatus(epic);
            epic.setStatus(status);
            setEpicDateTime(epic.getId());
            return true;
        }
        return false;
    }

    @Override
    public void deleteSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        prioritizedTasks.remove(subtask);
        subtasks.remove(subtask.getId());
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubtask(subtask);
            updateEpic(epic);
            setEpicDateTime(epic.getId());
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

    protected void setStartGenerateTaskId(int taskIdCounter) {
        this.taskIdCounter = taskIdCounter;
    }

    private void generateTaskId() {
        taskIdCounter++;
    }

    public void setEpicDateTime(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null){
            throw new InvalidTaskException("Epic не найден.");
        }
        List<Subtask> subtasks = epic.getSubtasks();
        if (subtasks.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            epic.setEndTime(null);
            return;
        }
        LocalDateTime epicStartTime = null;
        LocalDateTime epicEndTime = null;
        Duration epicDuration = Duration.ZERO;
        for (Subtask subtask : subtasks) {
            LocalDateTime subtaskStartTime = subtask.getStartTime();
            LocalDateTime subtaskEndTime = subtask.getEndTime();
            if (subtaskStartTime != null) {
                if (epicStartTime == null || subtaskStartTime.isBefore(epicStartTime)) {
                    epicStartTime = subtaskStartTime;
                }
            }
            if (subtaskEndTime != null) {
                if (epicEndTime == null || subtaskEndTime.isAfter(epicEndTime)) {
                    epicEndTime = subtaskEndTime;
                }
            }
            epicDuration = epicDuration.plus(subtask.getDuration());
        }
        epic.setStartTime(epicStartTime);
        epic.setEndTime(epicEndTime);
        epic.setDuration(epicDuration);
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().sorted(COMPARATOR).toList();
    }

    @Override
    public void validate(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getEndTime() == null) {
            return;
        }

        List<Task> prioritizedTasks = getPrioritizedTasks();
        for (Task existTask : prioritizedTasks) {
            if (existTask.getStartTime() == null || existTask.getEndTime() == null) {
                continue;
            }
            if (newTask.getId() == existTask.getId()) {
                continue;
            }

            // Проверка на пересечение задач
            if (newTask.getEndTime().isAfter(existTask.getStartTime()) &&
                    newTask.getStartTime().isBefore(existTask.getEndTime())) {
                throw new CollisionTaskException(
                        "Время выполнения задачи пересекается со временем уже существующей задачи." +
                                " Выберите другую дату."
                );
            }
        }
    }
}
