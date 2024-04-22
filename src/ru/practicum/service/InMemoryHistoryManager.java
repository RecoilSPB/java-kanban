package ru.practicum.service;

import ru.practicum.model.Task;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final HashMap<Integer, Task> history;

    public InMemoryHistoryManager() {
        this.history = new LinkedHashMap<>();
    }

    @Override
    public void add(Task task) {
        if (this.history.containsKey(task.getId())) {
            remove(task.getId());
        }
        this.history.put(task.getId(), task);
    }

    @Override
    public void remove(int id) {
        this.history.remove(id);
    }

    @Override
    public List<Task> getHistory() {
        return new LinkedList<>(history.values());
    }
}
