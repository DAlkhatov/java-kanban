package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {

    private ArrayList<Integer> subtaskIdList;

    public Epic(Status status, String name, String description) {
        super(status, name, description);
        subtaskIdList = new ArrayList<>();
    }

    public Epic(Status status, String name, String description, LocalDateTime starTime, Duration duration) {
        super(status, name, description, starTime, duration);
        subtaskIdList = new ArrayList<>();
    }

    public ArrayList<Integer> getSubtaskIdList() {
        return subtaskIdList;
    }

    public void setSubtaskIdList(ArrayList<Integer> subtaskIdList) {
        this.subtaskIdList = subtaskIdList;
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", status=" + getStatus() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", subtaskId=" + subtaskIdList +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                '}';
    }
}