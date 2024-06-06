package ru.practicum.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.service.Managers;
import ru.practicum.service.TaskManager;
import ru.practicum.utils.DurationAdapter;
import ru.practicum.utils.LocalDateAdapter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {

    public static final int PORT = 8080;
    public static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
            .registerTypeAdapter(Duration.class, new DurationAdapter())
            .create();
    private final HttpServer httpServer;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/subtasks", new SubtaskHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
    }

    public static void main(String[] args) throws IOException {
        TaskManager manager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();
    }

    public static int getId(String query) {
        if (query == null || !query.contains("id=")) {
            return -1;
        }
        String idString = query.split("id=")[1].split("[&/]")[0];
        try {
            return Integer.parseInt(idString);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту.");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер на порту " + PORT + " остановлен.");
    }
}
