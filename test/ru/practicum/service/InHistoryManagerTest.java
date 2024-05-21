package ru.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Task;
import ru.practicum.enums.TaskStatus;
import ru.practicum.utils.CustomLinkedList;
import ru.practicum.utils.Node;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class InHistoryManagerTest {

    Task task1;
    Task task2;
    Task task3;
    CustomLinkedList customLinkedList;
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        TaskManager taskManager = Managers.getDefault();
        task1 = new Task("1", "1", TaskStatus.NEW);
        taskManager.createTask(task1);
        task2 = new Task("2", "2", TaskStatus.IN_PROGRESS);
        taskManager.createTask(task2);
        task3 = new Task("3", "3", TaskStatus.DONE);
        taskManager.createTask(task3);
        customLinkedList = new CustomLinkedList();
        historyManager = Managers.getDefaultHistory();
    }

    @Test
    void addAndGetHistory() {
        historyManager.add(task1);
        assertFalse(historyManager.getHistory().isEmpty(), "История не должна быть пустой после добавления задачи.");
        assertEquals(task1, historyManager.getHistory().getFirst(), "Добавленная задача должна быть такой же, как и в истории.");
    }

    @Test
    void shouldReturnPrevAndNextItems() {
        customLinkedList.linkLast(task1);
        customLinkedList.linkLast(task2);
        customLinkedList.linkLast(task3);
        Node<Task> testTask1 = customLinkedList.getHashMap().get(task1.getId());
        Node<Task> testTask2 = customLinkedList.getHashMap().get(task3.getId());
        assertEquals(task2, testTask1.getNext().getValue());
        assertEquals(task2, testTask2.getPrev().getValue());
    }

    @Test
    void checkOfDuplicatesAndHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);
        List<Task> testList = List.of(task2, task1);
        assertEquals(testList, historyManager.getHistory());
    }

    @Test
    void checkUnlinkMethod() {
        customLinkedList.linkLast(task1);
        customLinkedList.linkLast(task2);
        customLinkedList.linkLast(task3);
        customLinkedList.removeNode(2);
        assertEquals(task1, customLinkedList.getNode(3).getPrev().getValue());
        assertEquals(task3, customLinkedList.getNode(1).getNext().getValue());
    }

    @Test
    void shouldRemoveTask() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        assertEquals(3, historyManager.getHistory().size());
        historyManager.remove(task2.getId());
        assertEquals(2, historyManager.getHistory().size());
    }
}