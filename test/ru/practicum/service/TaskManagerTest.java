package ru.practicum.service;

import org.junit.jupiter.api.Test;
import ru.practicum.enums.TaskStatus;
import ru.practicum.exceptions.CollisionTaskException;
import ru.practicum.model.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected final LocalDateTime DATE = LocalDateTime.of(2024, 5, 1, 0, 0);
    protected Duration DURATION = Duration.ofMinutes(100);
    protected Task task1;
    protected Epic epic2;
    protected Subtask subtask3;
    protected Subtask subtask4;


    public void initTasks() {
        task1 = new Task("Задача", "description1", TaskStatus.NEW, DATE.plusMinutes(10), DURATION);
        taskManager.createTask(task1);
        epic2 = new Epic("Эпик", "description3");
        taskManager.createEpic(epic2);
        subtask3 = new Subtask("Подзадача1", "description3", epic2, DATE.plusDays(1), DURATION);
        taskManager.createSubtask(subtask3);
        subtask4 = new Subtask("Подзадача2", "description4", epic2, DATE.plusDays(2), DURATION);
        taskManager.createSubtask(subtask4);
        taskManager.getEpicById(epic2.getId());
        taskManager.getSubtaskById(subtask3.getId());
    }

    @Test
    void addTask() {
        Task expectedTask = taskManager.getTaskById(1);
        assertNotNull(expectedTask, "Задача не найдена.");
        assertNotNull(taskManager.getAllTasks(), "Задачи на возвращаются.");
        assertEquals(1, taskManager.getAllTasks().size(), "Неверное количество задач.");
        assertEquals(1, expectedTask.getId(), "Идентификаторы задач не совпадают");
        Task taskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 1)
                .findFirst()
                .orElse(null);
        assertNotNull(taskPriority, "Задача не добавлена в список приоритизации");
        assertEquals(taskPriority, expectedTask, "В список приоритизации добавлена неверная задача");
    }

    @Test
    void addEpic() {
        Epic expectedEpic = taskManager.getEpicById(2);
        assertNotNull(expectedEpic, "Задача не найдена.");
        assertNotNull(taskManager.getAllEpics(), "Задачи на возвращаются.");
        assertEquals(1, taskManager.getAllEpics().size(), "Неверное количество задач.");
        assertNotNull(expectedEpic.getSubtasks(), "Список подзадач не создан.");
        assertEquals(TaskStatus.NEW, expectedEpic.getStatus(), "Статус не NEW");
        assertEquals(2, expectedEpic.getId(), "Идентификаторы задач не совпадают");
    }

    @Test
    void addSubtask() {
        Epic expectedEpicOfSubtask = taskManager.getEpicById(epic2.getId());
        assertNotNull(expectedEpicOfSubtask.getStartTime(), "Время эпика не null");
        Subtask expectedSubtask = taskManager.getSubtaskById(3);
        assertNotNull(expectedSubtask, "Задача не найдена.");
        assertNotNull(taskManager.getAllSubtasks(), "Задачи на возвращаются.");
        assertEquals(2, taskManager.getAllSubtasks().size(), "Неверное количество задач.");
        assertNotNull(expectedEpicOfSubtask, "Эпик подзадачи не найден");
        assertNotNull(taskManager.getSubtasksByEpicId(expectedEpicOfSubtask.getId()), "Список подзадач не обновился");
        assertEquals(DATE.plusDays(1), expectedEpicOfSubtask.getStartTime(), "Время эпика не обновилось");
        assertEquals(TaskStatus.NEW, expectedEpicOfSubtask.getStatus(), "Статус не NEW");
        assertEquals(3, expectedSubtask.getId(), "Идентификаторы задач не совпадают");
        assertEquals(expectedEpicOfSubtask, epic2, "Эпик подзадачи неверный");
        Task subtaskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 3)
                .findFirst()
                .orElse(null);
        assertNotNull(subtaskPriority, "Задача не добавлена в список приоритизации");
        assertEquals(subtaskPriority, expectedSubtask, "В список приоритизации добавлена неверная задача");
        assertNotNull(expectedEpicOfSubtask.getStartTime(), "Время эпика не изменилось");
    }

    @Test
    void checkEpicStatus() {
        Epic expectedEpicOfSubtask = taskManager.getEpicById(epic2.getId());
        Subtask updateSubtask4 = taskManager.getSubtaskById(4);
        updateSubtask4.setStatus(TaskStatus.DONE);
        updateSubtask4.setStartTime(DATE.plusDays(2));
        updateSubtask4.setDuration(DURATION);
        taskManager.updateSubtask(updateSubtask4);
        assertEquals(TaskStatus.IN_PROGRESS, expectedEpicOfSubtask.getStatus(), "Статус не IN_PROGRESS");

        Subtask updateSubtask3 = taskManager.getSubtaskById(3);
        updateSubtask3.setStatus(TaskStatus.DONE);
        updateSubtask3.setStartTime(DATE.plusDays(1));
        updateSubtask3.setDuration(DURATION);
        taskManager.updateSubtask(updateSubtask3);
        assertEquals(TaskStatus.DONE, expectedEpicOfSubtask.getStatus(), "Статус не DONE");

        Subtask update2Subtask3 = taskManager.getSubtaskById(3);
        updateSubtask3.setStatus(TaskStatus.IN_PROGRESS);
        updateSubtask3.setStartTime(DATE.plusDays(1));
        updateSubtask3.setDuration(DURATION);

        Subtask update3Subtask4 = taskManager.getSubtaskById(4);
        updateSubtask4.setStatus(TaskStatus.IN_PROGRESS);
        updateSubtask4.setStartTime(DATE.plusDays(2));
        updateSubtask4.setDuration(DURATION);

        taskManager.updateSubtask(update2Subtask3);
        taskManager.updateSubtask(update3Subtask4);
        assertEquals(TaskStatus.IN_PROGRESS, expectedEpicOfSubtask.getStatus(), "Статус не IN_PROGRESS");
        assertEquals(Duration.ofMinutes(200).toMinutes(),
                expectedEpicOfSubtask.getDuration().toMinutes(),
                "Продолжительность эпика не обновилась");
    }

    @Test
    void getHistory() {
        taskManager.getEpicById(2);
        taskManager.getSubtaskById(4);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);
        taskManager.getTaskById(1);
        List<Task> history = taskManager.getHistory();
        assertEquals(4, history.size(), "Список истории сформирован неверно");
        assertEquals(2, history.get(0).getId(), "Задача 2 не добавлена в список истории");
        assertEquals(4, history.get(1).getId(), "Задача 4 не добавлена в список истории");
        assertEquals(3, history.get(2).getId(), "Задача 3 не добавлена в список истории");
        assertEquals(1, history.get(3).getId(), "Задача 1 не добавлена в список истории");
    }

    @Test
    void getPrioritizedTasks() {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        assertEquals(1, prioritizedTasks.get(0).getId(), "Задача 1 не приоритизирована");
        assertEquals(3, prioritizedTasks.get(1).getId(), "Задача 3 не приоритизирована");
        assertEquals(4, prioritizedTasks.get(2).getId(), "Задача 4 не приоритизирована");
    }

    @Test
    void removeTaskById() {
        assertFalse(taskManager.getAllTasks().isEmpty(), "Список задач не заполнен");
        assertEquals(1, taskManager.getAllTasks().size(), "Неверное количество задач.");
        taskManager.deleteTaskById(1);
        assertNull(taskManager.getTaskById(1), "Задача не удалена");
        Task taskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 1)
                .findFirst()
                .orElse(null);
        assertNull(taskPriority, "Задача не удалена из списка приоритизации");
    }

    @Test
    void removeSubtaskById() {
        assertNotNull(taskManager.getAllSubtasks(), "Список подзадач не заполнен");
        assertEquals(2, taskManager.getAllSubtasks().size(), "Неверное количество задач.");
        taskManager.deleteSubtaskById(3);
        assertNull(taskManager.getSubtaskById(3), "Подзадача не удалена");
        Task subtaskPriority = taskManager.getPrioritizedTasks().stream()
                .filter(task -> task.getId() == 3)
                .findFirst()
                .orElse(null);
        assertNull(subtaskPriority, "Задача не удалена из списка приоритизации");
        assertEquals(DATE.plusDays(2),
                taskManager.getEpicById(epic2.getId()).getStartTime(),
                "Время эпика не изменилось");
    }

    @Test
    void removeEpicById() {
        assertNotNull(taskManager.getAllEpics(), "Список эпиков не заполнен");
        taskManager.deleteEpicById(2);
        assertNull(taskManager.getEpicById(2), "Эпик не удален");
    }

    @Test
    void validate() {
        Task task1 = new Task("Задача1", "description1", TaskStatus.NEW, DATE, DURATION);
        Task task2 = new Task("Задача2", "description2", TaskStatus.NEW, DATE, DURATION);

        CollisionTaskException exception = assertThrows(CollisionTaskException.class,
                () -> {
                    taskManager.createTask(task1);
                    taskManager.createTask(task2);
                });
        assertEquals("Время выполнения задачи пересекается со временем уже существующей " +
                "задачи. Выберите другую дату.", exception.getMessage());
    }
}