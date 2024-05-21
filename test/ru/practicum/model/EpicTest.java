package ru.practicum.model;

import org.junit.jupiter.api.Test;
import ru.practicum.enums.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EpicTest {
    @Test
    void epicCreation() {
        int id = 1;
        String title = "Title Epic";
        String description = "Description";
        TaskStatus status = TaskStatus.NEW;

        Epic epic = new Epic(title, description);
        epic.setId(id);

        assertEquals(id, epic.getId(), "Id Epic должен соответствовать: " + id);
        assertEquals(title, epic.getName(), "Название Epic должен соответствовать: " + title);
        assertEquals(description, epic.getDescription(), "Описание Epic должен соответствовать: " + description);
        assertEquals(status, epic.getStatus(), "Статус Epic должен соответствовать:" + status);
    }

    @Test
    void epicCreationAndEquality() {
        Epic epic1 = new Epic("Epic 1", "Description 1");
        Epic epic2 = new Epic("Epic 1", "Description 1");
        epic1.setId(1);
        epic2.setId(1);
        assertEquals(epic1, epic2, "Epics должны быть равными, когда их дети равны.");
    }
}
