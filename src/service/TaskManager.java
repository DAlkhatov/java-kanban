package service;

import model.Status;
import model.SubTask;
import model.Task;
import model.Epic;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {

    private int seq;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;

    private int generateId() {
        return ++seq;
    }

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    public void updateEpicStatus(Epic epic) {
        Status status = null;
        if (epic.getSubtaskId() == null || epic.getSubtaskId().isEmpty()) {
            status = Status.NEW;
        } else {
            int counterDone = 0;
            int counterNew = 0;
            for (int subTaskId : epic.getSubtaskId()) {
                if (getSubtask(subTaskId).getStatus().equals(Status.DONE)) {
                    counterDone++;
                } else if (getSubtask(subTaskId).getStatus().equals(Status.NEW)) {
                    counterNew++;
                }
                if (counterDone == epic.getSubtaskId().size()) {
                    status = Status.DONE;
                } else if (counterNew == epic.getSubtaskId().size()) {
                    status = Status.NEW;
                } else {
                    status = Status.IN_PROGRESS;
                }
            }
        }
        epic.setStatus(status);
    }

    public void addSubtaskId(int epicId, int subtaskId) {
        getEpic(epicId).getSubtaskId().add(subtaskId);
        updateEpicStatus(getEpic(epicId));
    }

    public void removeSubtaskId(int epicId, int subtaskId) {
        getEpic(epicId).getSubtaskId().remove(subtaskId);
        updateEpicStatus(getEpic(epicId));
    }

    public void removeAllSubtasks(int epicId) {
        if (getEpic(epicId) == null) {
            return;
        }
        getEpic(epicId).getSubtaskId().clear();
        updateEpicStatus(getEpic(epicId));
    }

    public ArrayList<Task> getTasks() {
        if (tasks == null) {
            return null;
        }
        ArrayList<Task> taskList = new ArrayList<>(tasks.values());
        return (ArrayList<Task>) taskList.clone();
    }

    public void clearTasks() {
        tasks.clear();
    }

    public Task getTask(int id) {
        return tasks.get(id);
    }

    public Task create(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    public void updateTask(Task task) {
        Task saved = getTask(task.getId());
        if (saved == null) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public ArrayList<Epic> getEpics() {
        if (epics == null) {
            return null;
        }
        ArrayList<Epic> epicList = new ArrayList<>(epics.values());
        return (ArrayList<Epic>) epicList.clone();
    }

    public void clearEpics() {
        for (Epic epic : epics.values()) {
            for (int i : epic.getSubtaskId()) {
                SubTask s = getSubtask(i);
                s.setEpicId(0);
            }
            removeAllSubtasks(epic.getId());
        }
        epics.clear();
    }

    public Epic getEpic(int id) {
        return epics.get(id);
    }

    public Epic create(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            return;
        }
        epic.setSubtaskId(saved.getSubtaskId());
        epic.setStatus(saved.getStatus());
        epic.setDescription(saved.getDescription());
        epics.put(epic.getId(), epic);
    }

    public void removeEpic(int id) {
        if (getEpic(id) == null) {
            return;
        }
        removeAllSubtasks(id);
        epics.remove(id);
    }

    public ArrayList<SubTask> getSubtasks() {
        if (subTasks == null) {
            return null;
        }
        ArrayList<SubTask> subtaskList = new ArrayList<>(subTasks.values());
        return (ArrayList<SubTask>) subtaskList.clone();
    }

    public void clearSubtasks() {
        for (SubTask s : subTasks.values()) {
            if (getEpic(s.getEpicId()) == null) {
                break;
            }
            removeSubtask(s.getId());
            removeSubtaskId(s.getEpicId(), s.getId());
        }
        subTasks.clear();
    }

    public SubTask getSubtask(int id) {
        return subTasks.get(id);
    }

    public SubTask create(SubTask subTask) {
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        return subTask;
    }

    public void updateSubtask(SubTask subTask) {
        SubTask saved = subTasks.get(subTask.getId());
        if (saved == null) {
            return;
        }
        subTask.setEpicId(saved.getEpicId());
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(getEpic(saved.getEpicId()));
    }

    public void removeSubtask(int id) {
        if (getSubtask(id) == null) {
            return;
        }
        SubTask saved = getSubtask(id);
        subTasks.remove(id);
        if (getEpic(saved.getEpicId()) == null) {
            return;
        }
        removeSubtaskId(getSubtask(id).getEpicId(), getSubtask(id).getId());
    }

    @Override
    public String toString() {
        return "TaskManager{" +
                "seq=" + seq +
                ", tasks=" + tasks +
                ", epics=" + epics +
                ", subTasks=" + subTasks +
                '}';
    }
}
