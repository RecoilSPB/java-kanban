package ru.practicum.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import ru.practicum.enums.Endpoint;
import ru.practicum.model.Epic;
import ru.practicum.service.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class EpicHandler extends BaseHandler {

    private final TaskManager taskManager;
    private final Gson gson;

    public EpicHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
        this.gson = HttpTaskServer.gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(),
                exchange.getRequestMethod(),
                exchange.getRequestURI().getQuery());
        switch (endpoint) {
            case POST_EPIC -> handlePostRequest(exchange);
            case GET_EPIC -> handleGetRequest(exchange);
            case DELETE_EPIC -> handleDeleteRequest(exchange);
            case DELETE_EPICS -> handleDeleteAllRequest(exchange);
            case GET_EPICS -> handleGetAllRequest(exchange);
            case GET_SUBTASKS_EPIC -> handleGetEpicSubtasksRequest(exchange);
            default -> writeResponse(exchange, "Такого эндпоинта не существует", HttpStatus.SC_NOT_FOUND);
        }
    }

    @Override
    public Endpoint getEndpoint(String path, String method, String query) {
        if ("GET".equals(method) && path.equals("/epics/") && query != null && query.contains("subtasks")) {
            return Endpoint.GET_SUBTASKS_EPIC;
        }

        return switch (method) {
            case "GET" -> (query == null) ? Endpoint.GET_EPICS : Endpoint.GET_EPIC;
            case "POST" -> Endpoint.POST_EPIC;
            case "DELETE" -> (query == null) ? Endpoint.DELETE_EPICS : Endpoint.DELETE_EPIC;
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
            Epic epic = gson.fromJson(body, Epic.class);
            if (epic.getId() == 0) {
                taskManager.createEpic(epic);
                writeResponse(exchange, "Эпик добавлен", HttpStatus.SC_CREATED);
            } else {
                taskManager.updateEpic(epic);
                writeResponse(exchange, "Эпик обновлен", HttpStatus.SC_CREATED);
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
        Epic epic = taskManager.getEpicById(id);
        if (epic != null) {
            writeResponse(exchange, gson.toJson(epic), HttpStatus.SC_OK);
        } else {
            writeResponse(exchange, "Эпик с id " + id + " не найден", HttpStatus.SC_NOT_FOUND);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange) throws IOException {
        int id = HttpTaskServer.getId(exchange.getRequestURI().getQuery());
        if (id == -1) {
            writeResponse(exchange, "Некорректный id", HttpStatus.SC_BAD_REQUEST);
            return;
        }
        if (taskManager.getEpicById(id) != null) {
            taskManager.deleteEpicById(id);
            writeResponse(exchange, "Эпик удален", HttpStatus.SC_OK);
        } else {
            writeResponse(exchange, "Эпик с id " + id + " не найден", HttpStatus.SC_NOT_FOUND);
        }
    }

    private void handleDeleteAllRequest(HttpExchange exchange) throws IOException {
        taskManager.deleteAllEpics();
        writeResponse(exchange, "Все эпики удалены", HttpStatus.SC_OK);
    }

    private void handleGetAllRequest(HttpExchange exchange) throws IOException {
        writeResponse(exchange, gson.toJson(taskManager.getAllEpics()), HttpStatus.SC_OK);
    }

    private void handleGetEpicSubtasksRequest(HttpExchange exchange) throws IOException {
        int id = HttpTaskServer.getId(exchange.getRequestURI().getQuery());
        if (id == -1) {
            writeResponse(exchange, "Некорректный id", HttpStatus.SC_BAD_REQUEST);
            return;
        }
        Epic epic = taskManager.getEpicById(id);
        if (epic != null) {
            writeResponse(exchange, gson.toJson(taskManager.getSubtasksByEpicId(id)), HttpStatus.SC_OK);
        } else {
            writeResponse(exchange, "Эпик с id " + id + " не найден", HttpStatus.SC_NOT_FOUND);
        }
    }
}
