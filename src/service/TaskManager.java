package service;

import model.SubTask;
import model.Task;
import model.Epic;

import java.util.HashMap;

public class TaskManager {

    int seq;
    private HashMap<Integer, Task> tasks;
    private HashMap<Integer, Epic> epics;
    private HashMap<Integer, SubTask> subTasks;

    private int generateId() {
        return seq++;
    }

    public TaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    public HashMap<Integer, Task> getTasks() {
        return tasks;
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
        Task saved = tasks.get(task.getId());
        if (saved == null) {
            return;
        }
        task.setStatus(saved.getStatus());
        tasks.put(task.getId(), task);
    }

    public void removeTask(int id) {
        tasks.remove(id);
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    public void clearEpics() {
        for (Epic epic : epics.values()) {
            for (SubTask s : epic.getSubTasks()) {
                epic.removeSubtask(s);
                s.setEpic(null);
            }
            epic.removeAllSubtasks();
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
        epic.setSubTasks(saved.getSubTasks());
        epic.setStatus(saved.getStatus());
        epics.put(epic.getId(), epic);
    }

    public void removeEpic(int id) {
        getEpic(id).removeAllSubtasks();
        epics.remove(id);
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public void clearSubtasks() {
        for (SubTask s : subTasks.values()) {
            s.getEpic().removeSubtask(s);
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
        subTask.setStatus(saved.getStatus());
        subTasks.put(subTask.getId(), subTask);

        Epic epic = saved.getEpic();
        epic.removeSubtask(saved);
        epic.addSubtask(subTask);
    }

    public void removeSubtask(int id) {
        if (getSubtask(id) == null) {
            return;
        }
        SubTask saved = getSubtask(id);
        saved.getEpic().removeSubtask(saved);
        subTasks.remove(id);
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
