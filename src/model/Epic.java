package model;

import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<SubTask> subTasks;

    public Epic(Status status, String name, String description) {
        super(status, name, description);
        setType("Epic");
        subTasks = new ArrayList<>();
    }

    public void updateStatus() {
        int counterDone = 0;
        int counterNew = 0;
        for (SubTask subTask : subTasks) {
            if (subTask.getStatus().equals(Status.DONE)) {
                counterDone++;
            } else if (subTask.getStatus().equals(Status.NEW)) {
                counterNew++;
            }
            if (counterDone == subTasks.size()) {
                setStatus(Status.DONE);
            } else if (counterNew == subTasks.size()) {
                setStatus(Status.NEW);
            } else {
                setStatus(Status.IN_PROGRESS);

            }
        }
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(ArrayList<SubTask> subTasks) {
        this.subTasks = subTasks;
    }

    public void addSubtask(SubTask subTask) {
        this.subTasks.add(subTask);
        updateStatus();
    }

    public void removeSubtask(SubTask subTask) {
        this.subTasks.remove(subTask);
        updateStatus();
    }

    public void removeAllSubtasks() {
        subTasks.clear();
    }
}