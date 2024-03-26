package ru.practicum.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    @Test
    void epicCreation() {
        int id = 1;
        String title = "Title Epic";
        String description = "Description";
        TaskStatus status = TaskStatus.NEW;

        Epic epic = new Epic(title, description, status);
        epic.setId(id);

        assertEquals(id, epic.getId(), "Id Epic должен соответствовать: " + id);
        assertEquals(title, epic.getName(), "Название Epic должен соответствовать: " + title);
        assertEquals(description, epic.getDescription(), "Описание Epic должен соответствовать: " + description);
        assertEquals(status, epic.getStatus(), "Статус Epic должен соответствовать:" + status);
    }

    @Test
    void taskCreationAndEquality() {
        Task task1 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        Task task2 = new Task("Task 1", "Description 1", TaskStatus.NEW);
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2, "Задачи должны быть равными, когда их дети равны.");
    }

    @Test
    void taskUpdate() {
        Task task = new Task("Task", "Description", TaskStatus.NEW);
        task.setDescription("Обновление Description");
        assertEquals("Обновление Description", task.getDescription(), "Описание задачи должно быть обновлено.");
    }
}