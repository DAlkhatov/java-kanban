package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    private int id;
    private Status status;
    private String name;
    private String description;
    private Duration duration;
    private LocalDateTime startTime;

    public Task(Status status, String name, String description, LocalDateTime startTime, Duration duration) {
        this.status = status;
        this.name = name;
        this.description = description;
        this.startTime = startTime == null ? LocalDateTime.now() : startTime;
        this.duration = duration == null ? Duration.ZERO : duration;
    }

    public Task(Status status, String name, String description) {
        this.status = status;
        this.name = name;
        this.description = description;
        this.startTime = LocalDateTime.now();
        this.duration = Duration.ZERO;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public Integer getEpicId() {
        return null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id &&
                status == task.status &&
                Objects.equals(name, task.name) &&
                Objects.equals(description, task.description);
    }

    @Override
    public int hashCode() {
        return id;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", status=" + status +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", type='" + getType() + '\'' +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}
