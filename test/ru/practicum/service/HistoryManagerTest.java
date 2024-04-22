package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Task;
import ru.practicum.model.TaskStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class HistoryManagerTest {

    private Task task;
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        task = new Task("Test Task", "Description", TaskStatus.NEW);
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void addAndGetHistory() {
        historyManager.add(task);
        assertFalse(historyManager.getHistory().isEmpty(), "История не должна быть пустой после добавления задачи.");
        assertEquals(task, historyManager.getHistory().get(0), "Добавленная задача должна быть такой же, как и в истории.");
    }
}