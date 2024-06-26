package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.enums.TaskStatus;
import ru.practicum.exceptions.InvalidTaskException;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
        initTasks();
    }

    @Test
    void createAndGetTask() {
        Task task = new Task("Sample Task", "Description", TaskStatus.NEW);

        taskManager.createTask(task);
        Task retrievedTask = taskManager.getTaskById(task.getId());

        assertNotNull(retrievedTask);
        assertEquals("Sample Task", retrievedTask.getName());
        assertEquals("Description", retrievedTask.getDescription());
        assertEquals(TaskStatus.NEW, retrievedTask.getStatus());
    }

    @Test
    void updateTask() {
        Task task = new Task("Sample Task", "Description", TaskStatus.NEW);

        taskManager.createTask(task);

        task.setName("Updated Task Title");
        task.setDescription("Updated Task Description");
        task.setStatus(TaskStatus.DONE);

        taskManager.updateTask(task);
        Task updatedTask = taskManager.getTaskById(task.getId());

        assertEquals("Updated Task Title", updatedTask.getName());
        assertEquals("Updated Task Description", updatedTask.getDescription());
        assertEquals(TaskStatus.DONE, updatedTask.getStatus());
    }

    @Test
    void createAndGetEpic() {
        Epic epic = new Epic("Sample Epic", "Description");

        taskManager.createEpic(epic);
        Epic retrievedEpic = taskManager.getEpicById(epic.getId());

        assertNotNull(retrievedEpic);
        assertEquals("Sample Epic", retrievedEpic.getName());
        assertEquals("Description", retrievedEpic.getDescription());
        assertEquals(TaskStatus.NEW, retrievedEpic.getStatus());
    }

    @Test
    void updateEpic() {
        Epic epic = new Epic("Sample Epic", "Description");

        taskManager.createEpic(epic);

        epic.setName("Updated Epic Title");
        epic.setDescription("Updated Epic Description");
        epic.setStatus(TaskStatus.DONE);

        taskManager.updateEpic(epic);
        Epic updatedEpic = taskManager.getEpicById(epic.getId());

        assertEquals("Updated Epic Title", updatedEpic.getName());
        assertEquals("Updated Epic Description", updatedEpic.getDescription());
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus());
    }

    @Test
    void deleteEpic() {
        Epic epic = new Epic("Sample Epic", "Description");

        taskManager.createEpic(epic);
        assertNotNull(taskManager.getEpicById(epic.getId()));

        taskManager.deleteEpicById(epic.getId());
        assertNull(taskManager.getEpicById(epic.getId()));
    }

    @Test
    void createAndGetSubtask() {
        Epic epic = new Epic("Sample Epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Sample Subtask", "Description", epic);

        taskManager.createSubtask(subtask);
        Subtask retrievedSubtask = taskManager.getSubtaskById(subtask.getId());

        assertNotNull(retrievedSubtask);
        assertEquals("Sample Subtask", retrievedSubtask.getName());
        assertEquals("Description", retrievedSubtask.getDescription());
        assertEquals(TaskStatus.NEW, retrievedSubtask.getStatus());
    }

    @Test
    void updateSubtask() {
        Epic epic = new Epic("Sample Epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Sample Subtask", "Description", epic);
        taskManager.createSubtask(subtask);

        subtask.setName("Updated Subtask Title");
        subtask.setDescription("Updated Subtask Description");
        subtask.setStatus(TaskStatus.DONE);

        taskManager.updateSubtask(subtask);
        Subtask updatedSubtask = taskManager.getSubtaskById(subtask.getId());

        assertEquals("Updated Subtask Title", updatedSubtask.getName());
        assertEquals("Updated Subtask Description", updatedSubtask.getDescription());
        assertEquals(TaskStatus.DONE, updatedSubtask.getStatus());
        assertEquals(TaskStatus.DONE, epic.getStatus());
    }

    @Test
    void deleteSubtask() {
        Epic epic = new Epic("Sample Epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Sample Subtask", "Description", epic);
        taskManager.createSubtask(subtask);

        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);

        assertNotNull(taskManager.getEpicById(epic.getId()));
        assertNotNull(taskManager.getSubtaskById(subtask.getId()));
        assertEquals(TaskStatus.DONE, subtask.getStatus());
        assertEquals(TaskStatus.DONE, epic.getStatus());

        taskManager.deleteSubtaskById(subtask.getId());
        assertNotNull(taskManager.getEpicById(epic.getId()));
        assertNull(taskManager.getSubtaskById(subtask.getId()));
        assertEquals(TaskStatus.NEW, epic.getStatus());
    }

    @Test
    void deleteEpicLinkSubtask() {
        Epic epic = new Epic("Sample Epic", "Description");
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask("Sample Subtask", "Description", epic);
        taskManager.createSubtask(subtask);

        assertNotNull(taskManager.getEpicById(epic.getId()));
        assertNotNull(taskManager.getSubtaskById(subtask.getId()));

        taskManager.deleteEpicById(epic.getId());

        assertNull(taskManager.getEpicById(epic.getId()));
        assertNull(taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    void getAllAndDeleteAllTask() {
        for (int i = 0; i < 15; i++) {
            Task task = new Task("Task" + i, "Description", TaskStatus.NEW);
            taskManager.createTask(task);
        }

        assertEquals(16, taskManager.getAllTasks().size());

        taskManager.deleteAllTasks();

        assertEquals(0, taskManager.getAllTasks().size());
    }

    @Test
    void getAllAndDeleteAllEpic() {
        for (int i = 0; i < 15; i++) {
            Epic epic = new Epic("Epic" + i, "Description");
            taskManager.createEpic(epic);
        }

        assertEquals(16, taskManager.getAllEpics().size());

        taskManager.deleteAllEpics();

        assertEquals(0, taskManager.getAllEpics().size());
    }

    @Test
    void getAllAndDeleteAllSubtask() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        for (int i = 0; i < 15; i++) {
            Subtask subtask = new Subtask("Subtask" + i, "Description", epic);
            taskManager.createSubtask(subtask);
        }

        assertEquals(17, taskManager.getAllSubtasks().size());
        assertEquals(15, taskManager.getSubtasksByEpicId(epic.getId()).size());

        taskManager.deleteAllSubtasks();

        assertEquals(0, taskManager.getAllSubtasks().size());
    }

    @Test
    void deleteEpicToAllSubtask() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        for (int i = 0; i < 15; i++) {
            Subtask subtask = new Subtask("Subtask" + i, "Description", epic);
            taskManager.createSubtask(subtask);
        }

        assertEquals(17, taskManager.getAllSubtasks().size());
        assertEquals(15, taskManager.getSubtasksByEpicId(epic.getId()).size());

        taskManager.deleteEpicById(epic.getId());

        assertEquals(2, taskManager.getAllSubtasks().size());
        assertEquals(1, taskManager.getAllEpics().size());
    }

    @Test
    void getHistoryTaskManager() {
        for (int i = 0; i < 15; i++) {
            Epic epic = new Epic("Epic" + i, "Description");
            taskManager.createEpic(epic);
        }

        assertEquals(2, taskManager.getHistory().size());
    }

    @Test
    void getStatusInProgressEpic() {
        Epic epic = new Epic("Epic", "Description");
        taskManager.createEpic(epic);
        Subtask subtask = null;
        for (int i = 0; i < 15; i++) {
            subtask = new Subtask("Subtask" + i, "Description", epic);
            taskManager.createSubtask(subtask);
        }

        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);

        assertEquals(TaskStatus.DONE, taskManager.getSubtaskById(subtask.getId()).getStatus());
        assertEquals(TaskStatus.IN_PROGRESS, taskManager.getEpicById(epic.getId()).getStatus());

    }

    @Test
    void createInvalidEpicAndSubtask() {
        Subtask s1 = new Subtask(
                "Sample Subtask",
                "Description",
                new Epic(
                        "Sample Epic",
                        "Description"
                )
        );
        InvalidTaskException exception = assertThrows(InvalidTaskException.class, () -> taskManager.createSubtask(s1));

        assertEquals("Epic не найден.", exception.getMessage());
    }

    @Test
    void getPrioritizedTasks() {
        Epic epic = new Epic("Sample Epic", "Description");
        Epic e = taskManager.createEpic(epic);

        String start1 = "2024-05-27T22:00:00";
        String start2 = "2024-05-27T22:30:01";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

        LocalDateTime parsedStart1 = LocalDateTime.parse(start1, formatter);
        LocalDateTime parsedStart2 = LocalDateTime.parse(start2, formatter);

        Subtask subtask1 = new Subtask("Sample Subtask 1", "Description 1", e, parsedStart1, Duration.ofMinutes(30));
        Subtask subtask2 = new Subtask("Sample Subtask 2", "Description 2", e, parsedStart2, Duration.ofMinutes(30));

        Subtask s1 = taskManager.createSubtask(subtask1);
        Subtask s2 = taskManager.createSubtask(subtask2);

        String start3 = "2024-05-27T19:30:01";

        s2.setStartTime(LocalDateTime.parse(start3, formatter));

        taskManager.updateSubtask(s2);

        List<Task> pt = taskManager.getPrioritizedTasks();
        int s1Index = pt.indexOf(s1);
        int s2Index = pt.indexOf(s2);
        assertTrue(s1Index > s2Index, "Не верная сортировка приоритета.");
    }
}