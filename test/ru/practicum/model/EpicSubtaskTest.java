package ru.practicum.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class EpicSubtaskTest {

    @Test
    void addAndRemoveSubtaskFromEpic() {
        Epic epic = new Epic("Epic 1", "Description", TaskStatus.NEW);
        Subtask subtask = new Subtask("Subtask 1", "Description", TaskStatus.NEW, epic);
        assertFalse(epic.getSubtasks().isEmpty(), "Epic должен содержать подзадачи после добавления.");
        epic.removeSubtask(subtask);
        assertTrue(epic.getSubtasks().isEmpty(), "Epic не должен содержать подзадач после удаления.");
    }

    @Test
    void checkSubtaskLinkToEpic() {
        Epic epic = new Epic("Epic 1", "Description", TaskStatus.NEW);
        Subtask subtask = new Subtask("Subtask 1", "Description", TaskStatus.NEW, epic);
        ArrayList<Subtask> listSubtask = new ArrayList<>();
        listSubtask.add(subtask);
        assertEquals(epic.getId(), subtask.getEpicId(), "Epic.id должен быть равен Subtast.epicId");
        assertEquals(epic.getSubtasks().get(0), subtask, "Subtask в листе Epic должен быть равен объекту Subtask");
        assertEquals(listSubtask, epic.getSubtasks(), "Лист Subtask должны быть равны");
    }

}