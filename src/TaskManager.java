import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    private int taskIdCounter; // Переменная для генерации уникальных идентификаторов
    private final HashMap<Integer, Task> tasks;

    public TaskManager() {
        taskIdCounter = 1; // Начальное значение счетчика
        this.tasks = new HashMap<>();
    }

    public void createTask(Task task) {
        task.setId(taskIdCounter);
        tasks.put(taskIdCounter, task);
        taskIdCounter++;
    }

    // Метод для получения задачи по идентификатору
    public Task getTaskById(int taskId) {
        return tasks.get(taskId);
    }

    // Обновление задачи
    public void updateTask(Task updatedTask) {
        tasks.put(updatedTask.getId(), updatedTask);

        if(updatedTask instanceof Epic epic){
            TaskStatus status = calculateEpicStatus(epic);
            epic.setStatus(status);
            tasks.put(epic.getId(), epic);
        }

        if (updatedTask instanceof Subtask subtask){
            Epic epic = (Epic) tasks.get(subtask.getEpicId());
            TaskStatus status = calculateEpicStatus(epic);
            epic.setStatus(status);
            tasks.put(epic.getId(), epic);
        }
    }

    private TaskStatus calculateEpicStatus(Task task) {
        Epic epic = (Epic) task;
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

    // Удаление задачи по идентификатору
    public void deleteTaskById(int taskId) {
        Task task = tasks.get(taskId);
        if (task instanceof Epic epic) {
            tasks.remove(epic.getId());
            for (Subtask subtask : epic.getSubtasks()){
                tasks.remove(subtask.getId());
            }
        }
        if (task instanceof Subtask subtask) {
            tasks.remove(subtask.getId());
            Epic epic = (Epic) tasks.get(subtask.getEpicId());
            epic.removeSubtask(subtask);
            updateTask(epic);
        }
    }

    // Получение списка всех задач
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public ArrayList<Subtask> getSubtasksForTask(int taskId) {
        Task task = tasks.get(taskId);
        return ((Epic) task).getSubtasks();
    }

}
