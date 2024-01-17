package model;

public class SubTask extends Task {
    private int epicId;

    public SubTask(Status status, String name, String description, int epicId) {
        super(status, name, description);
        this.epicId = epicId;
        setType("Subtask");
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }
}