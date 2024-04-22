package ru.practicum.service;

import ru.practicum.model.Task;
import ru.practicum.utils.CustLinkedList;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {

    CustLinkedList myLinkedList = new CustLinkedList();

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
