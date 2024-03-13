import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = init();

        // Проверка добавления задач
        System.out.println("Test: Get all Tasks:");
        printAllTasks(taskManager);
        System.out.println("---------------------------");

        // Проверка получения подзадач эпика
        System.out.println("Test: Get Subtasks from Epic:");
        ArrayList<Subtask> subtasks = taskManager.getSubtasksForTask(1);
        for (Subtask subtask : subtasks) {
            System.out.println(subtask);
        }
        System.out.println("---------------------------");

        // Изменение статуса IN_PROGRESS у задачи с id = 2
        System.out.println("Test: Update of Subtask status, id:2 :");
        Task updateTask = taskManager.getTaskById(2);
        updateTask.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateTask(updateTask);
        printAllTasks(taskManager);
        System.out.println("---------------------------");

        // Изменение статуса = DONE у задачи с id = 3
        System.out.println("Test: Update of Subtask status, id:3 :");
        updateTask = taskManager.getTaskById(3);
        updateTask.setStatus(TaskStatus.DONE);
        taskManager.updateTask(updateTask);
        printAllTasks(taskManager);
        System.out.println("---------------------------");

        // Изменение статуса = DONE у задачи с id = 5
        System.out.println("Test: Update of Subtask status, id:5 :");
        updateTask = taskManager.getTaskById(5);
        updateTask.setStatus(TaskStatus.DONE);
        taskManager.updateTask(updateTask);
        printAllTasks(taskManager);
        System.out.println("---------------------------");

        // Изменение статуса = DONE у задачи с id = 1
        System.out.println("Test: Update of Epic status, id:1 :");
        updateTask = taskManager.getTaskById(1);
        updateTask.setDescription("Test update of Epic 1 status");
        updateTask.setStatus(TaskStatus.DONE);
        taskManager.updateTask(updateTask);
        printAllTasks(taskManager);
        System.out.println("---------------------------");

        // Изменение статуса = DONE у задачи с id = 6
        System.out.println("Test: Update of Epic status without subtasks id:6 :");
        updateTask = taskManager.getTaskById(6);
        updateTask.setDescription("Test update of Epic 3 status");
        updateTask.setStatus(TaskStatus.DONE);
        taskManager.updateTask(updateTask);
        printAllTasks(taskManager);
        System.out.println("---------------------------");

        // Удаление Epic 1
        taskManager.deleteTaskById(1);
        printAllTasks(taskManager);
        System.out.println("---------------------------");

        // Удаление Subtask 3
        taskManager.deleteTaskById(5);
        printAllTasks(taskManager);
        System.out.println("---------------------------");


        //Test: delete all Tasks
        System.out.println("Test: delete all Tasks :");
        taskManager.deleteAllTasks();
        ArrayList<Task> tasks = taskManager.getAllTasks();
        if (tasks.isEmpty()) {
            System.out.println("Задач нет, список пуст");
        }
        System.out.println("---------------------------");
    }

    private static TaskManager init() {
        // Create Task Manager
        TaskManager taskManager = new TaskManager();

        // Создание Эпика(Задачи) 1
        Epic epic1 = new Epic("Epic 1", "Epic description 1", TaskStatus.NEW);
        taskManager.createTask(epic1);
        // Создание подзадачи 1 для задачи 1
        Subtask subtask1 = new Subtask("Subtask 1", "Subtask description 1", TaskStatus.NEW, epic1);
        taskManager.createTask(subtask1);
        // Создание подзадачи 2 для задачи 1
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask description 2", TaskStatus.NEW, epic1);
        taskManager.createTask(subtask2);

        // Создание Эпика(Задачи) 2
        Epic epic2 = new Epic("Epic 2", "Epic description 2", TaskStatus.NEW);
        taskManager.createTask(epic2);
        // Создание подзадачи 1 для задачи 1
        Subtask subtask3 = new Subtask("Subtask 3", "Subtask description 3", TaskStatus.NEW, epic2);
        taskManager.createTask(subtask3);

        // Создание Эпика(Задачи) 3
        Epic epic3 = new Epic("Epic 3", "Epic description 3", TaskStatus.NEW);
        taskManager.createTask(epic3);
        return taskManager;
    }

    private static void printAllTasks(TaskManager taskManager) {
        ArrayList<Task> tasks = taskManager.getAllTasks();
        for (Task task : tasks) {
            System.out.println(task);
        }
    }
}