package ru.practicum.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import ru.practicum.http.HttpTaskClient;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.util.stream.Collectors;

public class HttpTaskManager extends FileBackedTaskManager {

    private static final String TASKS = "/tasks";
    private static final String EPICS = "/epics";
    private static final String SUBTASKS = "/subtasks";
    private static final String HISTORY = "/history";

    private final HttpTaskClient client = new HttpTaskClient();

    private final Gson gson = Managers.getGson();

    public HttpTaskManager(boolean toLoad) {
        if (toLoad) {
            load();
        }
    }

    public HttpTaskManager() {
        this(false);
    }


    @Override
    public void save() {
        client.put(TASKS, gson.toJson(getAllTasks()));
        client.put(EPICS, gson.toJson(getAllEpics()));
        client.put(SUBTASKS, gson.toJson(getAllSubtasks()));
        client.put(HISTORY, gson.toJson(getHistory().stream().map(Task::getId).collect(Collectors.toList())));
    }

    private void load() {
        JsonElement jsonTasks = JsonParser.parseString(client.load(TASKS));
        JsonArray tasksArray = jsonTasks.getAsJsonArray();
        for (JsonElement jsonTask : tasksArray) {
            Task task = gson.fromJson(jsonTask, Task.class);
            int id = task.getId();
            tasks.put(id, task);
            prioritizedTasks.add(task);
            if (id > getTaskIdCounter()) {
                setStartGenerateTaskId(id);
            }
        }

        JsonElement jsonEpics = JsonParser.parseString(client.load(EPICS));
        JsonArray epicsArray = jsonEpics.getAsJsonArray();
        for (JsonElement jsonEpic : epicsArray) {
            Epic epic = gson.fromJson(jsonEpic, Epic.class);
            int id = epic.getId();
            epics.put(id, epic);
            if (id > getTaskIdCounter()) {
                setStartGenerateTaskId(id);
            }
        }

        JsonElement jsonSubtasks = JsonParser.parseString(client.load(SUBTASKS));
        JsonArray subtasksArray = jsonSubtasks.getAsJsonArray();
        for (JsonElement jsonSubtask : subtasksArray) {
            Subtask subtask = gson.fromJson(jsonSubtask, Subtask.class);
            int id = subtask.getId();
            subtasks.put(id, subtask);
            prioritizedTasks.add(subtask);
            if (id > getTaskIdCounter()) {
                setStartGenerateTaskId(id);
            }
        }

        JsonElement historyList = JsonParser.parseString(client.load(HISTORY));
        JsonArray historyArray = historyList.getAsJsonArray();
        for (JsonElement jsonId : historyArray) {
            int id = jsonId.getAsInt();
            if (tasks.containsKey(id)) {
                historyManager.add(tasks.get(id));
            } else if (epics.containsKey(id)) {
                historyManager.add(epics.get(id));
            } else if (subtasks.containsKey(id)) {
                historyManager.add(subtasks.get(id));
            }
        }
    }
}