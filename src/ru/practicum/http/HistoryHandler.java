package ru.practicum.http;

import com.sun.net.httpserver.HttpExchange;
import ru.practicum.enums.Endpoint;
import ru.practicum.model.Task;
import ru.practicum.service.TaskManager;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static ru.practicum.http.HttpTaskServer.gson;

public class HistoryHandler extends BaseHandler {
    private final TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(),
                exchange.getRequestMethod(),
                exchange.getRequestURI().getQuery());
        if (Objects.requireNonNull(endpoint) == Endpoint.GET_HISTORY) {
            handleGetHistory(exchange);
        } else {
            writeResponse(exchange, "Такого эндпоинта не существует", HttpStatus.SC_NOT_FOUND);
        }
    }

    @Override
    public Endpoint getEndpoint(String path, String method, String query) {
        if ("GET".equalsIgnoreCase(method) && query == null) {
            return Endpoint.GET_HISTORY;
        }
        return Endpoint.UNKNOWN;
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        List<Task> historyTasks = taskManager.getHistory();
        String jsonResponse = gson.toJson(historyTasks);
        writeResponse(exchange, jsonResponse, HttpStatus.SC_OK);
    }
}
