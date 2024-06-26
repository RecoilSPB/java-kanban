package ru.practicum.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.enums.TaskStatus;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;
import ru.practicum.service.Managers;
import ru.practicum.service.TaskManager;
import ru.practicum.utils.DurationAdapter;
import ru.practicum.utils.LocalDateAdapter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTest {

    private static final String URL = "http://localhost:" + HttpTaskServer.PORT;
    static TaskManager taskManager;
    static HttpTaskServer server;
    static Gson gson;
    protected Duration DURATION = Duration.ofMinutes(100);
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        taskManager = Managers.getDefault();
        server = new HttpTaskServer(taskManager);
        server.start();
        client = HttpClient.newHttpClient();
        taskManager.deleteAllTasks();
        taskManager.deleteAllEpics();
        taskManager.deleteAllSubtasks();

        Task task1 = new Task("Задача", "description1", TaskStatus.NEW,
                LocalDateTime.of(2024, 1, 1, 0, 0), DURATION);
        taskManager.createTask(task1);

        Epic epic2 = new Epic("Эпик", "description2");
        taskManager.createEpic(epic2);

        Subtask subtask3 = new Subtask("Подзадача", "description3", epic2,
                LocalDateTime.of(2024, 1, 2, 0, 0), DURATION);
        taskManager.createSubtask(subtask3);

        Subtask subtask4 = new Subtask("Подзадача", "description4", epic2,
                LocalDateTime.of(2024, 2, 4, 0, 0), DURATION);
        taskManager.createSubtask(subtask4);

        gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        taskManager.getEpicById(2);
        taskManager.getTaskById(1);
        taskManager.getSubtaskById(3);

    }

    @AfterEach
    void stopServer() {
        server.stop();
    }

    @Test
    void getAllTasks() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/tasks/task/");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> tasksList = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(tasksList, "Список задач не получен");
        assertEquals(taskManager.getAllTasks().toString(), tasksList.toString(), "Получен неверный список задач");
    }

    @Test
    void getAllEpics() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type epicType = new TypeToken<List<Epic>>() {
        }.getType();
        List<Epic> epicsList = gson.fromJson(response.body(), epicType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(epicsList, "Список эпиков не получен");
        assertEquals(taskManager.getAllEpics().toString(), epicsList.toString(), "Получен неверный список эпиков");
    }

    @Test
    void getAllSubtasks() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type subtaskType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> subtasksList = gson.fromJson(response.body(), subtaskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(subtasksList, "Список подзадач не получен");
        assertEquals(taskManager.getAllSubtasks().toString(), subtasksList.toString(), "Получен неверный список подзадач");
    }

    @Test
    void getTaskById() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/tasks/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskDeserialized = gson.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(taskDeserialized, "Задача не получена");
        assertEquals(taskManager.getAllTasks().getFirst().toString(), taskDeserialized.toString(), "Получена неверная задача");
    }

    @Test
    void getTaskIncorrectId() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/tasks/?id=a");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Код ответа не 400");
        assertEquals("Некорректный id", response.body(), "Ответ сервера не совпадает");
    }

    @Test
    void getTaskWrongId() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/tasks/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Код ответа не 404");
        assertEquals("Задача с id 3 не найдена", response.body(), "Ответ сервера не совпадает");
    }

    @Test
    void getEpicById() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/epics/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epicDeserialized = gson.fromJson(response.body(), Epic.class);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(epicDeserialized, "Эпик не получен");
        assertEquals(taskManager.getAllEpics().getFirst().toString(), epicDeserialized.toString(), "Получен неверный эпик");
    }

    @Test
    void getSubtaskById() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/subtasks/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Subtask subtaskDeserialized = gson.fromJson(response.body(), Subtask.class);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(subtaskDeserialized, "Подзадача не получена");
        assertEquals(taskManager.getAllSubtasks().getFirst().toString(), subtaskDeserialized.toString(), "Получена неверная подзадача");
    }

    @Test
    void getSubtasksByEpic() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/epics/?id=2/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type subtaskType = new TypeToken<List<Subtask>>() {
        }.getType();
        List<Subtask> subtasksList = gson.fromJson(response.body(), subtaskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(subtasksList, "Список подзадач не получен");
        assertEquals(taskManager.getSubtasksByEpicId(2).toString(), subtasksList.toString(), "Получен неверный список подзадач");
    }

    @Test
    void getHistory() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/history");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> history = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(history, "Список истории не получен");
        assertEquals(3, history.size(), "Длина списка истории не 3");
        assertEquals(taskManager.getHistory().get(0).getId(), history.get(0).getId(),
                "Id первого элемента списка не совпадает");
        assertEquals(taskManager.getHistory().get(1).getId(), history.get(1).getId(),
                "Id второго элемента списка не совпадает");
        assertEquals(taskManager.getHistory().get(2).getId(), history.get(2).getId(),
                "Id третьего элемента списка не совпадает");
    }

    @Test
    void getPrioritizedTasks() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/prioritized");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Type taskType = new TypeToken<List<Task>>() {
        }.getType();
        List<Task> priority = gson.fromJson(response.body(), taskType);

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertNotNull(priority, "Список приоритетных задач не получен");
        assertEquals(3, priority.size(), "Длина списка приоритетных задач не 3");
        assertEquals(taskManager.getPrioritizedTasks().get(0).getId(), priority.get(0).getId(),
                "Id первого элемента списка не совпадает");
        assertEquals(taskManager.getPrioritizedTasks().get(1).getId(), priority.get(1).getId(),
                "Id второго элемента списка не совпадает");
        assertEquals(taskManager.getPrioritizedTasks().get(2).getId(), priority.get(2).getId(),
                "Id третьего элемента списка не совпадает");
    }

    @Test
    void removeTaskById() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/tasks/?id=1");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertTrue(taskManager.getAllTasks().isEmpty(), "Задача не удалена");
        assertNull(taskManager.getTaskById(1), "Задача не удалена");
    }

    @Test
    void removeEpicById() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/epics/?id=2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпик не удален");
        assertNull(taskManager.getEpicById(2), "Эпик не удален");
    }

    @Test
    void removeSubtaskById() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/subtasks/?id=3");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertEquals(1, taskManager.getAllSubtasks().size(), "Подзадача не удалена");
        assertNull(taskManager.getSubtaskById(3), "Подзадача не удалена");
    }

    @Test
    void removeAllTasks() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertTrue(taskManager.getAllTasks().isEmpty(), "Задачи не удалены");
        assertEquals("Все задачи удалены", response.body(), "Неверный ответ от сервера");
    }

    @Test
    void removeAllEpics() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertTrue(taskManager.getAllEpics().isEmpty(), "Эпики не удалены");
        assertEquals("Все эпики удалены", response.body(), "Неверный ответ от сервера");
    }

    @Test
    void removeAllSubtasks() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Код ответа не 200");
        assertTrue(taskManager.getAllSubtasks().isEmpty(), "Подзадачи не удалены");
        assertEquals("Все подзадачи удалены", response.body(), "Неверный ответ от сервера");
    }

    @Test
    void addNewTask() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/tasks/task/");
        Task task = new Task("Задача", "description5", TaskStatus.NEW,
                LocalDateTime.of(2024, 1, 4, 0, 0), DURATION);
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task savedTask = gson.fromJson(response.body(), Task.class);

        assertEquals(201, response.statusCode(), "Код ответа не 201");
        assertEquals(5, taskManager.getAllTasks().get(1).getId(), "Id новой задачи не совпадает");
        assertEquals(2, taskManager.getAllTasks().size(), "Новая задача не добавлена");

        assertEquals(taskManager.getTaskById(5), savedTask, "Новая задача не добавлена");
    }

    @Test
    void addNewTaskEmptyBody() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/tasks/task/");
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString("");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(400, response.statusCode(), "Код ответа не 400");
        assertEquals("Необходимо заполнить все поля задачи", response.body(), "Ответ сервера не совпадает");
    }


    @Test
    void updateTask() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/tasks/task/");
        Task task = taskManager.getTaskById(1);
        task.setName("Задача");
        task.setDescription("description1");
        task.setStatus(TaskStatus.IN_PROGRESS);
        task.setStartTime(LocalDateTime.of(2024, 1, 4, 0, 0));
        task.setDuration(Duration.ofDays(1000));
        String json = gson.toJson(task);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не 201");
        assertEquals(1, taskManager.getAllTasks().getFirst().getId(), "Id задачи не совпадает");
        assertEquals(1, taskManager.getAllTasks().size(), "Список состоит не из одной задачи");
        assertEquals("Задача обновлена", response.body(), "Задача не обновлена");
        assertEquals(task.toString(), taskManager.getTaskById(1).toString(), "Задача не обновлена");
    }

    @Test
    void addNewEpic() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/epics");
        Epic epic5 = new Epic("Эпик", "description5");
        String json = gson.toJson(epic5);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не 201");
        assertEquals(5, taskManager.getAllEpics().get(1).getId(), "Id нового эпика не совпадает");
        assertEquals(2, taskManager.getAllEpics().size(), "Новый эпик не добавлен");
        assertEquals("Эпик добавлен", response.body(), "Новый эпик не добавлен");
    }

    @Test
    void updateEpic() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/epics");
        Epic epic2 = taskManager.getEpicById(2);
        epic2.setName("Эпик");
        epic2.setDescription("d");
        epic2.setStartTime(LocalDateTime.of(2023, 1, 3, 0, 0));
        epic2.setDuration(Duration.ofDays(1000));
        epic2.setEndTime(LocalDateTime.of(2023, 1, 3, 16, 40));

        String json = gson.toJson(epic2);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не 201");
        assertEquals(2, taskManager.getAllEpics().getFirst().getId(), "Id эпика не совпадает");
        assertEquals(1, taskManager.getAllEpics().size(), "Список состоит не из одного эпика");
        assertEquals("Эпик обновлен", response.body(), "Эпик не обновлен");
        assertEquals(epic2.toString(), taskManager.getEpicById(2).toString(), "Эпик не обновлен");
    }

    @Test
    void addNewSubtask() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/subtasks");
        Subtask subtask5 = new Subtask("Подзадача", "description5", taskManager.getEpicById(2),
                LocalDateTime.of(2023, 1, 4, 0, 0), DURATION);
        String json = gson.toJson(subtask5);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не 201");
        assertEquals(5, taskManager.getAllSubtasks().get(2).getId(), "Id новой подзадачи не совпадает");
        assertEquals(3, taskManager.getAllSubtasks().size(), "Новая подзадача не добавлена");
        assertEquals("Подзадача добавлена", response.body(), "Новая подзадача не добавлена");
    }

    @Test
    void updateSubtask() throws IOException, InterruptedException {
        URI url = URI.create(URL + "/subtasks");
        Subtask subtask3 = taskManager.getSubtaskById(3);
        subtask3.setName("Подзадача");
        subtask3.setDescription("description3");
        subtask3.setStartTime(LocalDateTime.of(2024, 4, 2, 0, 0));
        subtask3.setDuration(Duration.ofDays(500));
        String json = gson.toJson(subtask3);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(body).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Код ответа не 201");
        assertEquals(3, taskManager.getAllSubtasks().getFirst().getId(), "Id подзадачи не совпадает");
        assertEquals(2, taskManager.getAllSubtasks().size(), "Список состоит не из двух подзадач");
        assertEquals("Подзадача обновлена", response.body(), "Подзадача не обновлена");
        assertEquals(subtask3.toString(), taskManager.getSubtaskById(3).toString(), "Подзадача не обновлена");
    }


}