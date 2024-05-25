package ru.practicum.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.enums.TaskStatus;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @BeforeEach
    void setUp() {
        file = new File("./resources/forTest.csv");
        super.taskManager = new FileBackedTaskManager(file);
        initTasks();
    }

    @Test
    void testSaveCsvEmptyAndDeletingCSV() {
        try {
            FileWriter fileWriter = new FileWriter(file.toString(), StandardCharsets.UTF_8);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertTrue(file.exists(), "Ошибка при сохранении пустого файла");
        boolean deleteTempFile = file.delete();
        assertTrue(deleteTempFile, "Ошибка при удалении файла");
        assertFalse(file.exists(), "Ошибка при удалении пустого файла");
    }

    @Test
    void testSaveCsv() {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);

        fileBackedTaskManager.createTask(new Task("Task1", "Description task1", TaskStatus.NEW));
        Epic epic1 = fileBackedTaskManager.createEpic(new Epic("Epic2", "Description epic2"));
        Subtask subTask1 = fileBackedTaskManager.createSubtask(new Subtask("Sub Task1", "Description sub task1", epic1, LocalDateTime.parse("24.05.24 15:15", Task.formatter), Duration.of(10, ChronoUnit.MINUTES)));
        fileBackedTaskManager.createSubtask(new Subtask("Sub Task2", "Description sub task2", epic1));

        fileBackedTaskManager.getEpicById(epic1.getId());
        fileBackedTaskManager.getSubtaskById(subTask1.getId());

        assertTrue(file.exists(), "Ошибка при сохранении файла");
    }

    @Test
    void testLoadCsv() {
        FileBackedTaskManager fileBackedTaskManager = FileBackedTaskManager.loadFromFile(file);

        List<Task> loadedTasks = new ArrayList<>(fileBackedTaskManager.getAllTasks());
        List<Epic> loadedEpics = new ArrayList<>(fileBackedTaskManager.getAllEpics());
        List<Subtask> loadedSubTasks = new ArrayList<>(fileBackedTaskManager.getAllSubtasks());

        List<Task> loadedHistoryTasks = fileBackedTaskManager.getHistory();

        int expectedTasksCount = 1;
        int expectedEpicsCount = 1;
        int expectedSubTasksCount = 2;
        int expectedHistoryCount = 2;

        assertEquals(expectedTasksCount, loadedTasks.size(),
                "Количество загруженных задач не соответствует ожидаемому");
        assertEquals(expectedEpicsCount, loadedEpics.size(),
                "Количество загруженных эпиков не соответствует ожидаемому");
        assertEquals(expectedSubTasksCount, loadedSubTasks.size(),
                "Количество загруженных подзадач не соответствует ожидаемому");
        assertEquals(expectedHistoryCount, loadedHistoryTasks.size(),
                "Количество загруженных задач в историю не соответствует ожидаемому");
    }

    @AfterEach
    void tearDown() {
        if ((file.exists())) {
            assertTrue(file.delete());
        }
    }
}