import ru.practicum.enums.TaskStatus;
import ru.practicum.model.Epic;
import ru.practicum.model.Subtask;
import ru.practicum.service.Managers;
import ru.practicum.service.TaskManager;

public class Main {
    public static void main(String[] args) {
        // Create ru.practicum.model.Task Manager
        TaskManager taskManager = Managers.getDefault();
        init(taskManager);

        // Проверка получение списка всех задач
        System.out.println("Test: Get all Tasks:");
        System.out.println(taskManager.getAllTasks());
        System.out.println("---------------------------\n");
        System.out.println("Test: Get all Epics:");
        System.out.println(taskManager.getAllEpics());
        System.out.println("---------------------------\n");
        System.out.println("Test: Get all Subtasks:");
        System.out.println(taskManager.getAllSubtasks());
        System.out.println("---------------------------\n");

        // Проверка получения подзадач эпика
        System.out.println("Test: Get Subtasks from ru.practicum.model.Epic:");
        System.out.println(taskManager.getSubtasksByEpicId(1));
        System.out.println("---------------------------\n");

        // Изменение статуса IN_PROGRESS у задачи с id = 2
        System.out.println("Test: Update of ru.practicum.model.Subtask status, id:2 :");
        Subtask subtask = taskManager.getSubtaskById(2);
        subtask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask);
        System.out.println("ru.practicum.model.Subtask:\n" + taskManager.getSubtaskById(subtask.getId()));
        System.out.println("ru.practicum.model.Epic:\n" + taskManager.getEpicById(subtask.getEpicId()));
        System.out.println("---------------------------\n");

        // Изменение статуса = DONE у задачи с id = 3
        System.out.println("Test: Update of ru.practicum.model.Subtask status, id:3 :");
        subtask = taskManager.getSubtaskById(3);
        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);
        System.out.println("ru.practicum.model.Subtask:\n" + taskManager.getSubtaskById(subtask.getId()));
        System.out.println("ru.practicum.model.Epic:\n" + taskManager.getEpicById(subtask.getEpicId()));
        System.out.println("---------------------------\n");


        // Изменение статуса = DONE у задачи с id = 5
        System.out.println("Test: Update of ru.practicum.model.Subtask status, id:5 :");
        subtask = taskManager.getSubtaskById(5);
        subtask.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask);
        System.out.println("ru.practicum.model.Subtask:\n" + taskManager.getSubtaskById(subtask.getId()));
        System.out.println("ru.practicum.model.Epic:\n" + taskManager.getEpicById(subtask.getEpicId()));
        System.out.println("---------------------------\n");

        // Изменение статуса = DONE у задачи с id = 1
        System.out.println("Test: Update of ru.practicum.model.Epic status, id:1 :");
        Epic epic = taskManager.getEpicById(1);
        epic.setDescription("Test update of ru.practicum.model.Epic 1 status");
        epic.setStatus(TaskStatus.DONE);
        taskManager.updateEpic(epic);
        System.out.println("ru.practicum.model.Epic:\n" + taskManager.getEpicById(epic.getId()));
        System.out.println("---------------------------\n");

        // Изменение статуса = DONE у задачи с id = 6
        System.out.println("Test: Update of ru.practicum.model.Epic status without subtasks id:6 :");
        epic = taskManager.getEpicById(6);
        epic.setDescription("Test update of ru.practicum.model.Epic 3 status");
        epic.setStatus(TaskStatus.DONE);
        taskManager.updateEpic(epic);
        System.out.println("ru.practicum.model.Epic:\n" + taskManager.getEpicById(epic.getId()));
        System.out.println("---------------------------\n");

        // Удаление ru.practicum.model.Epic 1
        System.out.println("Test: Delete ru.practicum.model.Epic by id: 1");
        taskManager.deleteEpicById(1);
        System.out.println("Epics:\n" + taskManager.getAllEpics());
        System.out.println("Subtasks:\n" + taskManager.getAllSubtasks());
        System.out.println("---------------------------\n");

        // Удаление ru.practicum.model.Subtask 3
        System.out.println("Test: Delete ru.practicum.model.Subtask by id: 5");
        taskManager.deleteSubtaskById(5);
        System.out.println("Epics:\n" + taskManager.getAllEpics());
        System.out.println("Subtasks:\n" + taskManager.getAllSubtasks());
        System.out.println("---------------------------\n");

        // Просмотр истории
        System.out.println("Test: Get History");
        System.out.println("History:\n" + taskManager.getHistory());
        System.out.println("---------------------------\n");


        //Test: delete all Tasks
        System.out.println("Test: delete all Tasks :");
        taskManager.deleteAllTasks();
        System.out.println("Tasks:\n" + taskManager.getAllTasks());
        System.out.println("---------------------------\n");

        //Test: delete all Epics
        System.out.println("Test: delete all Epics :");
        init(taskManager);
        System.out.println("Before Epics:\n" + taskManager.getAllEpics());
        System.out.println("Before Subtasks:\n" + taskManager.getAllSubtasks());
        taskManager.deleteAllEpics();
        System.out.println("After Epics:\n" + taskManager.getAllEpics());
        System.out.println("After Subtasks:\n" + taskManager.getAllSubtasks());
        System.out.println("---------------------------\n");

        //Test: delete all Subtasks
        System.out.println("Test: delete all Subtasks :");
        init(taskManager);
        System.out.println("Before Epics:\n" + taskManager.getAllEpics());
        System.out.println("Before Subtasks:\n" + taskManager.getAllSubtasks());
        taskManager.deleteAllSubtasks();
        System.out.println("After Epics:\n" + taskManager.getAllEpics());
        System.out.println("After Subtasks:\n" + taskManager.getAllSubtasks());
        System.out.println("---------------------------\n");

    }

    private static void init(TaskManager taskManager) {


        // Создание Эпика(Задачи) 1
        Epic epic1 = new Epic("ru.practicum.model.Epic 1", "ru.practicum.model.Epic description 1");
        taskManager.createEpic(epic1);
        // Создание подзадачи 1 для задачи 1
        Subtask subtask1 = new Subtask("ru.practicum.model.Subtask 1", "ru.practicum.model.Subtask description 1", epic1);
        taskManager.createSubtask(subtask1);
        // Создание подзадачи 2 для задачи 1
        Subtask subtask2 = new Subtask("ru.practicum.model.Subtask 2", "ru.practicum.model.Subtask description 2", epic1);
        taskManager.createSubtask(subtask2);

        // Создание Эпика(Задачи) 2
        Epic epic2 = new Epic("ru.practicum.model.Epic 2", "ru.practicum.model.Epic description 2");
        taskManager.createEpic(epic2);
        // Создание подзадачи 1 для задачи 1
        Subtask subtask3 = new Subtask("ru.practicum.model.Subtask 3", "ru.practicum.model.Subtask description 3", epic2);
        taskManager.createSubtask(subtask3);

        // Создание Эпика(Задачи) 3
        Epic epic3 = new Epic("ru.practicum.model.Epic 3", "ru.practicum.model.Epic description 3");
        taskManager.createEpic(epic3);
    }
}