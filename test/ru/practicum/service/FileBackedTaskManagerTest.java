package ru.practicum.service;

import ru.practicum.enums.TaskStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.model.Task;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FileBackedTaskManagerTest {

    @Test
    void testSaveCsvEmptyAndDeletingCSV() {
        String resourceDirectoryPath = "test/resources";
        File tempFile = new File(resourceDirectoryPath, "empty.csv");

        try {
            FileWriter fileWriter = new FileWriter(tempFile.toString(), StandardCharsets.UTF_8);
            fileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        assertTrue(tempFile.exists(), "Ошибка при сохранении пустого файла");
        boolean deleteTempFile = tempFile.delete();
        assertTrue(deleteTempFile, "Ошибка при удалении файла");
        assertFalse(tempFile.exists(), "Ошибка при удалении пустого файла");
    }

    @Test
    void testSaveCsv() {

        String resourceDirectoryPath = "test/resources";
        File tempFile = new File(resourceDirectoryPath, "export.csv");
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFile);

        Task task1 = fileBackedTaskManager.createTask(new Task("Task1", "Description task1", TaskStatus.NEW));
        Epic epic1 = fileBackedTaskManager.createEpic(new Epic("Epic2", "Description epic2"));
        Subtask subTask1 = fileBackedTaskManager.createSubtask(new Subtask("Sub Task2", "Description sub task3", epic1));

        fileBackedTaskManager.getTaskById(task1.getId());
        fileBackedTaskManager.getEpicById(epic1.getId());
        fileBackedTaskManager.getSubtaskById(subTask1.getId());

        assertTrue(tempFile.exists(), "Ошибка при сохранении файла");
    }

    @Test
    void testLoadCsv() {
        String resourceDirectoryPath = "test/resources";
        File tempFileExport = new File(resourceDirectoryPath, "export.csv");
        File tempFileImport = new File(resourceDirectoryPath, "import.csv");

        try {
            Files.copy(tempFileExport.toPath(), tempFileImport.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(tempFileImport);
        fileBackedTaskManager.loadFromFile(tempFileImport);

        List<Task> loadedTasks = new ArrayList<>(fileBackedTaskManager.getAllTasks());
        List<Epic> loadedEpics = new ArrayList<>(fileBackedTaskManager.getAllEpics());
        List<Subtask> loadedSubTasks = new ArrayList<>(fileBackedTaskManager.getAllSubtasks());

        List<Task> loadedHistoryTasks = fileBackedTaskManager.getHistory();

        int expectedTasksCount = 1;
        int expectedEpicsCount = 1;
        int expectedSubTasksCount = 1;
        int expectedHistoryCount = 2;

        Assertions.assertEquals(expectedTasksCount, loadedTasks.size(),
                "Количество загруженных задач не соответствует ожидаемому");
        Assertions.assertEquals(expectedEpicsCount, loadedEpics.size(),
                "Количество загруженных эпиков не соответствует ожидаемому");
        Assertions.assertEquals(expectedSubTasksCount, loadedSubTasks.size(),
                "Количество загруженных подзадач не соответствует ожидаемому");
        Assertions.assertEquals(expectedHistoryCount, loadedHistoryTasks.size(),
                "Количество загруженных задач в историю не соответствует ожидаемому");
    }

}