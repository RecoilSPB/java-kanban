package ru.practicum.model;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class SubtaskTest {
    private static Epic epic;

    @BeforeAll
    static void setup(){
        epic = new Epic("Title Epic", "Description", TaskStatus.NEW);
        epic.setId(1);
    }

    @Test
    void addSubtask() {
        int id = 2;
        String title = "Title Subtask";
        String description = "Description";
        TaskStatus status = TaskStatus.NEW;

        Subtask subtask = new Subtask(title, description, status, epic);
        subtask.setId(id);
        assertEquals(id, subtask.getId(), "Id Epic должен соответствовать: " + id);
        assertEquals(title, subtask.getName(), "Название Epic должен соответствовать: " + title);
        assertEquals(description, subtask.getDescription(), "Описание Epic должен соответствовать: " + description);
        assertEquals(status, subtask.getStatus(), "Статус Epic должен соответствовать:" + status);
        assertNotEquals(subtask.getEpicId(), "", "Subtask.getEpicId не должен быть пустым.");
    }

    @Test
    void subtaskCreationAndEquality() {
        Subtask subtask1 = new Subtask("Subtask 1", "Description 1", TaskStatus.NEW, epic);
        Subtask subtask2 = new Subtask("Subtask 1", "Description 1", TaskStatus.NEW, epic);
        subtask1.setId(1);
        subtask2.setId(1);
        assertEquals(subtask1, subtask2, "Subtasks должны быть равными, когда их родители равны.");
    }
}
