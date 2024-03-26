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

    @Test
    void checkHistoryLimit() {
        for (int i = 0; i < 15; i++) {
            Task newTask = new Task("Task" + i, "Description", TaskStatus.NEW);
            newTask.setId(i);
            historyManager.add(newTask);
        }
        assertEquals(5, historyManager.getHistory().get(0).getId(),
                "Идентификатор задачи в первом элементе должен быть 5");
        assertEquals(14, historyManager.getHistory().get(9).getId(),
                "Идентификатор задачи в крайнем элементе должен быть 15");
        assertEquals(10, historyManager.getHistory().size(),
                "Размер истории не должен превышать 10 задач.");
    }
}