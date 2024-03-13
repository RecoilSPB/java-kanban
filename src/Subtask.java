public class Subtask extends Task {
    private final int epicId;

    public Subtask(String name, String description, TaskStatus status, Epic epic) {
        super(name, description, status);
        this.epicId = epic.getId();
        epic.addSubtask(this);
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" + super.toString() +
                " epicId=" + epicId +
                "} ";
    }
}
