package ru.practicum.service;

import ru.practicum.model.Task;
import ru.practicum.utils.CustomLinkedList;

import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final CustomLinkedList myLinkedList = new CustomLinkedList();

    @Override
    public void add(Task task) {
        myLinkedList.linkLast(task);
    }

    @Override
    public List<Task> getHistory() {
        return myLinkedList.getTasks();
    }

    @Override
    public void remove(int id) {
        myLinkedList.removeNode(id);
    }
}
