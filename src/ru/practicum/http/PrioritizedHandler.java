package ru.practicum.http;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.enums.Endpoint;
import ru.practicum.model.Task;
import ru.practicum.service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static ru.practicum.http.HttpTaskServer.gson;

public class PrioritizedHandler extends BaseHandler {
    private final TaskManager taskManager;

    public PrioritizedHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(),
                exchange.getRequestMethod(),
                exchange.getRequestURI().getQuery());
        if (Objects.requireNonNull(endpoint) == Endpoint.GET_PRIORITY) {
            handleGetPrioritized(exchange);
        } else {
            System.err.println("Unknown endpoint: " + exchange.getRequestURI().getPath());
            writeResponse(exchange, "Такого эндпоинта не существует", HttpStatus.SC_NOT_FOUND);
        }
    }

    @Override
    public Endpoint getEndpoint(String path, String method, String query) {
        if ("GET".equalsIgnoreCase(method) && query == null) {
            return Endpoint.GET_PRIORITY;
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetPrioritized(HttpExchange exchange) throws IOException {
        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        String jsonResponse = gson.toJson(prioritizedTasks);
        writeResponse(exchange, jsonResponse, HttpStatus.SC_OK);
    }
}
