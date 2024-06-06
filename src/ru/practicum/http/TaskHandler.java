package ru.practicum.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.enums.Endpoint;
import ru.practicum.model.Task;
import ru.practicum.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(),
                exchange.getRequestMethod(),
                exchange.getRequestURI().getQuery());
        switch (endpoint) {
            case POST_TASK -> handlePostRequest(exchange);
            case GET_TASK -> handleGetRequest(exchange);
            case DELETE_TASK -> handleDeleteRequest(exchange);
            case DELETE_TASKS -> handleDeleteAllRequest(exchange);
            case GET_TASKS -> handleGetAllRequest(exchange);
            default -> writeResponse(exchange, "Такого эндпоинта не существует", HttpStatus.SC_NOT_FOUND);
        }
    }

    @Override
    public Endpoint getEndpoint(String path, String method, String query) {
        return switch (method) {
            case "GET" -> (query == null) ? Endpoint.GET_TASKS : Endpoint.GET_TASK;
            case "POST" -> Endpoint.POST_TASK;
            case "DELETE" -> (query == null) ? Endpoint.DELETE_TASKS : Endpoint.DELETE_TASK;
            default -> Endpoint.UNKNOWN;
        };
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        if (body.isEmpty()) {
            writeResponse(exchange, "Необходимо заполнить все поля задачи", HttpStatus.SC_BAD_REQUEST);
            return;
        }
        try {
            Task task = gson.fromJson(body, Task.class);
            if (task.getId() == 0) {
                Task savedTask = taskManager.createTask(task);
                writeResponse(exchange, gson.toJson(savedTask), HttpStatus.SC_CREATED);
            } else {
                taskManager.updateTask(task);
                writeResponse(exchange, "Задача обновлена", HttpStatus.SC_CREATED);
            }
        } catch (JsonSyntaxException e) {
            writeResponse(exchange, "Получен некорректный JSON", HttpStatus.SC_BAD_REQUEST);
        }
    }

    private void handleGetRequest(HttpExchange exchange) throws IOException {
        int id = HttpTaskServer.getId(exchange.getRequestURI().getQuery());
        if (id == -1) {
            writeResponse(exchange, "Некорректный id", HttpStatus.SC_BAD_REQUEST);
            return;
        }
        Task task = taskManager.getTaskById(id);
        if (task != null) {
            writeResponse(exchange, gson.toJson(task), HttpStatus.SC_OK);
        } else {
            writeResponse(exchange, "Задача с id " + id + " не найдена", HttpStatus.SC_NOT_FOUND);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        int id = HttpTaskServer.getId(exchange.getRequestURI().getQuery());
        if (id == -1) {
            writeResponse(exchange, "Некорректный id", HttpStatus.SC_BAD_REQUEST);
            return;
        }
        if (taskManager.getTaskById(id) != null) {
            taskManager.deleteTaskById(id);
            writeResponse(exchange, "Задача удалена", HttpStatus.SC_OK);
        } else {
            writeResponse(exchange, "Задача с id " + id + " не найдена", HttpStatus.SC_NOT_FOUND);
        }
    }

    private void handleDeleteAllRequest(HttpExchange exchange) throws IOException {
        taskManager.deleteAllTasks();
        writeResponse(exchange, "Все задачи удалены", HttpStatus.SC_OK);
    }

    private void handleGetAllRequest(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(taskManager.getAllTasks()), HttpStatus.SC_OK);
    }
}
