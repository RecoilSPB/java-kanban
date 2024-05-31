package ru.practicum.http;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.service.Managers;
import ru.practicum.service.TaskManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class HttpTaskServer {

    private static final int PORT = 8080;

    private final HttpServer httpServer;
    private final TaskManager taskManager;
    private final Gson gson = Managers.getGson();

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        httpServer.createContext("/tasks", new TasksHandler());
    }

    public void start() {
        httpServer.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту.");
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("HTTP-сервер на порту " + PORT + " остановлен.");
    }

    class TasksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Началась обработка /tasks запроса от клиента.");

            String query = exchange.getRequestURI().getQuery();
            int id;

            Endpoint endpoint = getEndpoint(exchange.getRequestURI().getPath(), exchange.getRequestMethod(), query);

            switch (endpoint) {
                case POST_TASK:
                    InputStream inputStreamTask = exchange.getRequestBody();
                    String bodyTask = new String(inputStreamTask.readAllBytes(), StandardCharsets.UTF_8);
                    if (bodyTask.isEmpty()) {
                        writeResponse(exchange, "Необходимо заполнить все поля задачи", HttpStatus.SC_BAD_REQUEST);
                        return;
                    }
                    try {
                        Task task = gson.fromJson(bodyTask, Task.class);
                        if (task.getId() == 0) {
                            taskManager.createTask(task);
                            writeResponse(exchange, "Задача добавлена", HttpStatus.SC_CREATED);
                        } else {
                            taskManager.updateTask(task);
                            writeResponse(exchange, "Задача обновлена", HttpStatus.SC_CREATED);
                        }
                    } catch (JsonSyntaxException e) {
                        writeResponse(exchange, "Получен некорректный JSON", HttpStatus.SC_BAD_REQUEST);
                    }
                    break;
                case POST_EPIC:
                    InputStream inputStreamEpic = exchange.getRequestBody();
                    String bodyEpic = new String(inputStreamEpic.readAllBytes(), StandardCharsets.UTF_8);
                    if (bodyEpic.isEmpty()) {
                        writeResponse(exchange, "Необходимо заполнить все поля задачи", HttpStatus.SC_BAD_REQUEST);
                        return;
                    }
                    try {
                        Epic epic = gson.fromJson(bodyEpic, Epic.class);
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
                    break;
                case POST_SUBTASK:
                    InputStream inputStreamSubtask = exchange.getRequestBody();
                    String bodySubtask = new String(inputStreamSubtask.readAllBytes(), StandardCharsets.UTF_8);
                    if (bodySubtask.isEmpty()) {
                        writeResponse(exchange, "Необходимо заполнить все поля задачи", HttpStatus.SC_BAD_REQUEST);
                        return;
                    }
                    try {
                        Subtask subtask = gson.fromJson(bodySubtask, Subtask.class);
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
                case GET_TASK:
                    id = getId(query);
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
                    break;
                case GET_EPIC:
                    id = getId(query);
                    if (id == -1) {
                        writeResponse(exchange, "Некорректный id", HttpStatus.SC_BAD_REQUEST);
                        return;
                    }
                    Epic epic = taskManager.getEpicById(id);
                    if (epic.getId() != 0) {
                        writeResponse(exchange, gson.toJson(epic), HttpStatus.SC_OK);
                    } else {
                        writeResponse(exchange, "Эпик с id " + id + " не найден", HttpStatus.SC_NOT_FOUND);
                    }
                    break;
                case GET_SUBTASK:
                    id = getId(query);
                    if (id == -1) {
                        writeResponse(exchange, "Некорректный id", HttpStatus.SC_BAD_REQUEST);
                        return;
                    }
                    Subtask subtask = taskManager.getSubtaskById(id);
                    if (subtask.getId() != 0) {
                        writeResponse(exchange, gson.toJson(subtask), HttpStatus.SC_OK);
                    } else {
                        writeResponse(exchange, "Подзадача с id " + id + " не найдена", HttpStatus.SC_NOT_FOUND);
                    }
                    break;
                case GET_SUBTASKS_EPIC:
                    id = getId(query);
                    if (id == -1) {
                        writeResponse(exchange, "Некорректный id", HttpStatus.SC_BAD_REQUEST);
                        return;
                    }
                    if (taskManager.getEpicById(id).getId() != 0) {
                        writeResponse(exchange, gson.toJson(taskManager.getSubtasksByEpicId(id)), HttpStatus.SC_OK);
                    } else {
                        writeResponse(exchange, "Эпик с id " + id + " не найден", HttpStatus.SC_NOT_FOUND);
                    }
                    break;
                case DELETE_TASK:
                    id = getId(query);
                    if (id == -1) {
                        writeResponse(exchange, "Некорректный id", HttpStatus.SC_BAD_REQUEST);
                        return;
                    }
                    if (taskManager.getTaskById(id).getId() != 0) {
                        taskManager.deleteTaskById(id);
                        writeResponse(exchange, "Задача удалена", HttpStatus.SC_OK);
                    } else {
                        writeResponse(exchange, "Задача с id " + id + " не найдена", HttpStatus.SC_NOT_FOUND);
                    }
                    break;
                case DELETE_EPIC:
                    id = getId(query);
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
                    break;
                case DELETE_SUBTASK:
                    id = getId(query);
                    if (id == -1) {
                        writeResponse(exchange, "Некорректный id", HttpStatus.SC_BAD_REQUEST);
                        return;
                    }
                    if (taskManager.getSubtaskById(id).getId() != 0) {
                        taskManager.deleteSubtaskById(id);
                        writeResponse(exchange, "Подзадача удалена", HttpStatus.SC_OK);
                    } else {
                        writeResponse(exchange, "Подзадача с id " + id + " не найдена", HttpStatus.SC_NOT_FOUND);
                    }
                    break;
                case DELETE_TASKS:
                    taskManager.deleteAllTasks();
                    if (taskManager.getAllTasks().isEmpty()) {
                        writeResponse(exchange, "Все задачи удалены", HttpStatus.SC_OK);
                    }
                    break;
                case DELETE_EPICS:
                    taskManager.deleteAllEpics();
                    if (taskManager.getAllEpics().isEmpty()) {
                        writeResponse(exchange, "Все эпики удалены", HttpStatus.SC_OK);
                    }
                    break;
                case DELETE_SUBTASKS:
                    taskManager.deleteAllSubtasks();
                    if (taskManager.getAllSubtasks().isEmpty()) {
                        writeResponse(exchange, "Все подзадачи удалены", HttpStatus.SC_OK);
                    }
                    break;
                case GET_TASKS:
                    writeResponse(exchange, gson.toJson(taskManager.getAllTasks()), HttpStatus.SC_OK);
                    break;
                case GET_EPICS:
                    writeResponse(exchange, gson.toJson(taskManager.getAllEpics()), HttpStatus.SC_OK);
                    break;
                case GET_SUBTASKS:
                    writeResponse(exchange, gson.toJson(taskManager.getAllSubtasks()), HttpStatus.SC_OK);
                    break;
                case GET_HISTORY:
                    writeResponse(exchange, gson.toJson(taskManager.getHistory()), HttpStatus.SC_OK);
                    break;
                case GET_PRIORITY:
                    writeResponse(exchange, gson.toJson(taskManager.getPrioritizedTasks()), HttpStatus.SC_OK);
                    break;
                default:
                    writeResponse(exchange, "Такого эндпоинта не существует", HttpStatus.SC_NOT_FOUND);
                    break;
            }
        }

        private Endpoint getEndpoint(String path, String method, String query) {
            String[] pathParts = path.split("/");
            if (pathParts.length == 2) {
                return Endpoint.GET_PRIORITY;
            }
            String map = pathParts[2];

            if (query != null) {
                if (method.equals("GET")) {
                    switch (map) {
                        case "task" -> {
                            return Endpoint.GET_TASK;
                        }
                        case "epic" -> {
                            return Endpoint.GET_EPIC;
                        }
                        case "subtask" -> {
                            if (pathParts.length == 3) {
                                return Endpoint.GET_SUBTASK;
                            } else {
                                return Endpoint.GET_SUBTASKS_EPIC;
                            }
                        }
                    }
                }
                if (method.equals("DELETE")) {
                    switch (map) {
                        case "task" -> {
                            return Endpoint.DELETE_TASK;
                        }
                        case "epic" -> {
                            return Endpoint.DELETE_EPIC;
                        }
                        case "subtask" -> {
                            return Endpoint.DELETE_SUBTASK;
                        }
                    }
                }
            } else {
                if (method.equals("GET")) {
                    switch (map) {
                        case "task" -> {
                            return Endpoint.GET_TASKS;
                        }
                        case "epic" -> {
                            return Endpoint.GET_EPICS;
                        }
                        case "subtask" -> {
                            return Endpoint.GET_SUBTASKS;
                        }
                        case "history" -> {
                            return Endpoint.GET_HISTORY;
                        }
                    }
                }
                if (method.equals("POST")) {
                    switch (map) {
                        case "task" -> {
                            return Endpoint.POST_TASK;
                        }
                        case "epic" -> {
                            return Endpoint.POST_EPIC;
                        }
                        case "subtask" -> {
                            return Endpoint.POST_SUBTASK;
                        }
                    }
                }
                if (method.equals("DELETE")) {
                    switch (map) {
                        case "task" -> {
                            return Endpoint.DELETE_TASKS;
                        }
                        case "epic" -> {
                            return Endpoint.DELETE_EPICS;
                        }
                        case "subtask" -> {
                            return Endpoint.DELETE_SUBTASKS;
                        }
                    }
                }
            }
            return Endpoint.UNKNOWN;
        }

        private void writeResponse(HttpExchange exchange, String response, int code) throws IOException {
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(code, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
            exchange.close();
        }

        private int getId(String query) {
            try {
                return Optional.of(Integer.parseInt(query.replaceFirst("id=", ""))).get();
            } catch (NumberFormatException exception) {
                return -1;
            }
        }

        enum Endpoint {
            DELETE_TASKS, DELETE_SUBTASKS, DELETE_EPICS, GET_TASKS, GET_EPICS, GET_SUBTASKS, GET_TASK, GET_EPIC,
            GET_SUBTASK, GET_SUBTASKS_EPIC, DELETE_TASK, DELETE_EPIC, DELETE_SUBTASK, POST_TASK, POST_SUBTASK,
            POST_EPIC, GET_HISTORY, GET_PRIORITY, UNKNOWN
        }
    }
}

