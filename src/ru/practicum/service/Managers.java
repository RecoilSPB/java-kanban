package ru.practicum.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.practicum.utils.DurationAdapter;
import ru.practicum.utils.LocalDateAdapter;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;

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

    public static Gson getGson() {
        return new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
    }
}
