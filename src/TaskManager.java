import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int taskIdCounter; // Переменная для генерации уникальных идентификаторов
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epics;
    private final HashMap<Integer, Subtask> subtasks;

    public TaskManager() {
        taskIdCounter = 1; // Начальное значение счетчика
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subtasks = new HashMap<>();
    }

    // Методы для Task
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        ArrayList<Integer> taskIds = new ArrayList<>(tasks.keySet());
        for (Integer taskId :taskIds) {
            deleteTaskById(taskId);
        }
    }

    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    public void createTask(Task task) {
        task.setId(taskIdCounter);
        tasks.put(taskIdCounter, task);
        taskIdCounter++;
    }

    public void updateTask(Task updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);
    }

    public void deleteTaskById(int taskId) {
        tasks.remove(taskId);
    }

    //Методы для Epic
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    public void deleteAllEpics() {
        ArrayList<Integer> epicIds = new ArrayList<>(epics.keySet());
        for (Integer epicId : epicIds){
            deleteEpicById(epicId);
        }
    }

    public Epic getEpicById(int taskId) {
        return epics.get(taskId);
    }

    public void createEpic(Epic epic) {
        epic.setId(taskIdCounter);
        epics.put(taskIdCounter, epic);
        taskIdCounter++;
    }

    public void updateEpic(Epic epic) {
        TaskStatus status = calculateEpicStatus(epic);
        epic.setStatus(status);
        tasks.put(epic.getId(), epic);
    }

    public void deleteEpicById(int epicId) {
        Epic epic = epics.get(epicId);
        epics.remove(epicId);
        for (Subtask subtask : epic.getSubtasks()){
            deleteSubtaskById(subtask.getId());
        }
    }

    // Методы для Subtask
    public ArrayList<Subtask> getAllSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteAllSubtasks() {
        ArrayList<Integer> subtaskIds = new ArrayList<>(subtasks.keySet());
        for (Integer subtaskId : subtaskIds){
            deleteSubtaskById(subtaskId);
        }
    }

    public Subtask getSubtaskById(int subtaskId) {
        return subtasks.get(subtaskId);
    }

    public void createSubtask(Subtask subtask) {
        subtask.setId(taskIdCounter);
        subtasks.put(taskIdCounter, subtask);
        taskIdCounter++;
    }

    public void updateSubtask(Subtask subtask) {
        tasks.put(subtask.getId(), subtask);
        Epic epic = epics.get(subtask.getEpicId());
        TaskStatus status = calculateEpicStatus(epic);
        epic.setStatus(status);
    }

    public void deleteSubtaskById(int subtaskId) {
        Subtask subtask = subtasks.get(subtaskId);
        subtasks.remove(subtask.getId());
        Epic epic = epics.get(subtask.getEpicId());
        if (epic != null) {
            epic.removeSubtask(subtask);
            updateEpic(epic);
        }
    }

    // Получение списка всех подзадач определённого эпика
    public ArrayList<Subtask> getSubtasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        return epic.getSubtasks();
    }

    // Управление статусами
    private TaskStatus calculateEpicStatus(Epic epic) {
        ArrayList<Subtask> subtasks = epic.getSubtasks();
        boolean allSubtasksDone = true;
        boolean allSubtasksNew = true;

        for (Subtask subtask : subtasks) {
            if (subtask.getStatus() != TaskStatus.DONE) {
                allSubtasksDone = false;
            }
            if (subtask.getStatus() != TaskStatus.NEW) {
                allSubtasksNew = false;
            }
        }

        if (subtasks.isEmpty() || allSubtasksNew) {
            return TaskStatus.NEW;
        } else if (allSubtasksDone) {
            return TaskStatus.DONE;
        } else {
            return TaskStatus.IN_PROGRESS;
        }
    }

}
