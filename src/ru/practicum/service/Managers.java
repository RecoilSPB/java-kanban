package ru.practicum.service;

import java.io.File;

public class Managers {

    public static InMemoryTaskManager getDefault() {
        return new InMemoryTaskManager();
    }

    public static FileBackedTaskManager getDefaultFile() {
        return new FileBackedTaskManager(new File("./resources/kanban.csv"));
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
