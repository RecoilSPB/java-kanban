package ru.practicum.service;

import java.io.File;

public class Managers {

    public static FileBackedTaskManager getDefault() {
        return new FileBackedTaskManager(new File("kanban.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
