package ru.practicum.service;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.http.TaskHttpServer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {

    private TaskHttpServer server;

    @BeforeEach
    void setUp() {
        try {
            server = new TaskHttpServer();
            server.start();
            super.taskManager = new HttpTaskManager();
            initTasks();
            taskManager.getTaskById(1);
            taskManager.getEpicById(2);
            taskManager.getSubtaskById(4);
            taskManager.getSubtaskById(3);
        } catch (IOException e) {
            System.out.println("Ошибка при создании менеджера");
        }
    }

    @AfterEach
    void tearDown() {
        server.stop();
    }

    @Test
    void saveAndLoad() {
        HttpTaskManager httpTaskManager = new HttpTaskManager(true);

        assertEquals(taskManager.getAllTasks().toString(), httpTaskManager.getAllTasks().toString(),
                "Список задач после выгрузки не совпадает");
        assertEquals(taskManager.getAllEpics().toString(), httpTaskManager.getAllEpics().toString(),
                "Список эпиков после выгрузки не совпадает");
        assertEquals(taskManager.getAllSubtasks().toString(), httpTaskManager.getAllSubtasks().toString(),
                "Список подзадач после выгрузки не совпадает");
        assertEquals(taskManager.getPrioritizedTasks().toString(), httpTaskManager.getPrioritizedTasks().toString(),
                "Список приоритизации после выгрузки не совпадает");
        assertEquals(taskManager.getHistory().toString(), httpTaskManager.getHistory().toString(),
                "Список истории после выгрузки не совпадает");

        assertEquals(1, httpTaskManager.getAllTasks().get(0).getId(),
                "Id после выгрузки не совпадает");
        assertEquals(2, httpTaskManager.getAllEpics().get(0).getId(),
                "Id после выгрузки не совпадает");
        assertEquals(3, httpTaskManager.getAllSubtasks().get(0).getId(),
                "Id после выгрузки не совпадает");
        assertEquals(4, httpTaskManager.getAllSubtasks().get(1).getId(),
                "Id после выгрузки не совпадает");

        assertEquals(httpTaskManager.getAllTasks().size(), taskManager.getAllTasks().size(),
                "Количество Tasks не совпадает");
        assertEquals(httpTaskManager.getAllEpics().size(), taskManager.getAllEpics().size(),
                "Количество Epics не совпадает");
        assertEquals(httpTaskManager.getAllSubtasks().size(), taskManager.getAllSubtasks().size(),
                "Количество Subtasks не совпадает");

        assertEquals(httpTaskManager.getTaskIdCounter(), taskManager.getTaskIdCounter(),
                "Идентификатор последней добавленной задачи после выгрузки не совпадает");
    }
}