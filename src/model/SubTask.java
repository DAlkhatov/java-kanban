package model;

public class SubTask extends Task {
    private Epic epic;

    public SubTask(Status status, String name, String description, Epic epic) {
        super(status, name, description);
        this.epic = epic;
        setType("Subtask");
    }

    public Epic getEpic() {
        return epic;
    }

    public void setEpic(Epic epic) {
        this.epic = epic;
    }
}