package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtaskId;

    public Epic(Status status, String name, String description) {
        super(status, name, description);
        subtaskId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> subtaskId) {
        this.subtaskId = subtaskId;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", status=" + getStatus() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subtaskId=" + subtaskId +
                '}';
    }
}