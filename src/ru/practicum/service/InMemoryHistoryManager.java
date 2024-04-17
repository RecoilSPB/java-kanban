package ru.practicum.service;

import ru.practicum.model.Task;

import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> history;
    private static final int SIZE = 10;

    public InMemoryHistoryManager() {
        this.history = new LinkedList<>();
    }

    @Override
    public void add(Task task) {
        if (this.history.size() >= SIZE) {
            this.history.remove(0);
        }
        this.history.add(task);
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(history);
    }
}
