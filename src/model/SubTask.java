package model;

import java.time.Duration;
import java.time.LocalDateTime;

public class SubTask extends Task {
    private Integer epicId;

    public SubTask(Status status, String name, String description, int epicId) {
        super(status, name, description);
        this.epicId = epicId;
    }

    public SubTask(Status status, String name, String description, int epicId, LocalDateTime startTime,
                   Duration duration) {
        super(status, name, description, startTime, duration);
        this.epicId = epicId;
    }

    public SubTask(Status status, String name, String description) {
        super(status, name, description);
    }

    @Override
    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "SubTask{" +
                "id=" + getId() +
                ", status=" + getStatus() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", epicId=" + epicId +
                ", startTime=" + getStartTime() +
                ", duration=" + getDuration() +
                '}';
    }
}