package ru.practicum.service;

import ru.practicum.model.Task;

import java.util.HashMap;

public interface HistoryManager {
    void add(Task task);
    void remove(int id);
    HashMap<Integer, Task> getHistory();
}
