package ru.practicum.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.enums.Endpoint;
import ru.practicum.model.Subtask;
import ru.practicum.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SubtaskHandler extends BaseHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public SubtaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod(), exchange.getRequestURI().getQuery());
        switch (endpoint) {
            case POST_SUBTASK -> handlePostRequest(exchange);
            case GET_SUBTASK -> handleGetRequest(exchange);
            case DELETE_SUBTASK -> handleDeleteRequest(exchange);
            case DELETE_SUBTASKS -> handleDeleteAllRequest(exchange);
            case GET_SUBTASKS -> handleGetAllRequest(exchange);
            case GET_SUBTASKS_EPIC -> handleGetSubtasksByEpicIdRequest(exchange);
            default -> writeResponse(exchange, "Такого эндпоинта не существует", HttpStatus.SC_NOT_FOUND);
        }
    }

    @Override
    public Endpoint getEndpoint(String path, String method, String query) {
        return switch (method) {
            case "GET" -> (query == null) ? Endpoint.GET_SUBTASKS : Endpoint.GET_SUBTASK;
            case "POST" -> Endpoint.POST_SUBTASK;
            case "DELETE" -> (query == null) ? Endpoint.DELETE_SUBTASKS : Endpoint.DELETE_SUBTASK;
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
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (subtask.getId() == 0) {
                taskManager.createSubtask(subtask);
                writeResponse(exchange, "Подзадача добавлена", HttpStatus.SC_CREATED);
            } else {
                taskManager.updateSubtask(subtask);
                writeResponse(exchange, "Подзадача обновлена", HttpStatus.SC_CREATED);
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
        Subtask subtask = taskManager.getSubtaskById(id);
        if (subtask != null) {
            writeResponse(exchange, gson.toJson(subtask), HttpStatus.SC_OK);
        } else {
            writeResponse(exchange, "Подзадача с id " + id + " не найдена", HttpStatus.SC_NOT_FOUND);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        int id = HttpTaskServer.getId(exchange.getRequestURI().getQuery());
        if (id == -1) {
            writeResponse(exchange, "Некорректный id", HttpStatus.SC_BAD_REQUEST);
            return;
        }
        if (taskManager.getSubtaskById(id) != null) {
            taskManager.deleteSubtaskById(id);
            writeResponse(exchange, "Подзадача удалена", HttpStatus.SC_OK);
        } else {
            writeResponse(exchange, "Подзадача с id " + id + " не найдена", HttpStatus.SC_NOT_FOUND);
        }
    }

    private void handleDeleteAllRequest(HttpExchange exchange) throws IOException {
        taskManager.deleteAllSubtasks();
        writeResponse(exchange, "Все подзадачи удалены", HttpStatus.SC_OK);
    }

    private void handleGetAllRequest(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(taskManager.getAllSubtasks()), HttpStatus.SC_OK);
    }

    private void handleGetSubtasksByEpicIdRequest(HttpExchange exchange) throws IOException {
        int id = HttpTaskServer.getId(exchange.getRequestURI().getQuery());
        if (id == -1) {
            writeResponse(exchange, "Некорректный id", HttpStatus.SC_BAD_REQUEST);
            return;
        }
        if (taskManager.getEpicById(id) != null) {
            writeResponse(exchange, gson.toJson(taskManager.getSubtasksByEpicId(id)), HttpStatus.SC_OK);
        } else {
            writeResponse(exchange, "Эпик с id " + id + " не найден", HttpStatus.SC_NOT_FOUND);
        }
    }
}
