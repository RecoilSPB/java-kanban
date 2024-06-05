package ru.practicum.service;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.enums.TaskStatus;
import ru.practicum.http.HttpTaskServer;
import ru.practicum.model.Task;
import ru.practicum.utils.DurationAdapter;
import ru.practicum.utils.LocalDateAdapter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;


class HttpTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    private HttpTaskServer server;
    static Gson gson;
    private static final String URL = "http://localhost:"+ HttpTaskServer.PORT;
    HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();
        taskManager = Managers.getDefault();
        taskManager.deleteAllTasks();
        server = new HttpTaskServer(taskManager);
        server.start();
        initTasks();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void addTask() {
        Task newTask = new Task("Test Task", "Test Description", TaskStatus.NEW);
//        newTask.setId(10);
        String taskJson = gson.toJson(newTask);

        URI urlAdd = URI.create(URL + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlAdd)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();
        HttpResponse<String> response = send(request);
        int actual = response.statusCode();
        String responseBody = response.body();
        Task savedTask = gson.fromJson(responseBody, Task.class);
        assertEquals(201, actual, "Статус добавление Task не 201, получено: " + actual);
        assertEquals(taskManager.getTaskById(5), savedTask, "Вернулся не верный Task");
    }

    private HttpResponse<String> send(HttpRequest request) {
        try {
            return client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}