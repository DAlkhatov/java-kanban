package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtaskId;

    public Epic(Status status, String name, String description) {
        super(status, name, description);
        setType("Epic");
        subtaskId = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskId() {
        return subtaskId;
    }

    public void setSubtaskId(ArrayList<Integer> subtaskId) {
        this.subtaskId = subtaskId;
    }

    public void addSubtaskId(int id) {
        this.subtaskId.add(id);
    }

    public void removeSubtaskId(int subtaskId) {
        this.subtaskId.remove(subtaskId);
    }

    public void removeAllSubtasks() {
        subtaskId.clear();
    }
}