package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    int seq;
    final HashMap<Integer, Task> tasks;
    final HashMap<Integer, Epic> epics;
    final HashMap<Integer, SubTask> subTasks;
    final HistoryManager historyManager;

    public InMemoryTaskManager() {
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.historyManager = new InMemoryHistoryManager();
    }

    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager; // 3
        this.tasks = new HashMap<>();
        this.epics = new HashMap<>();
        this.subTasks = new HashMap<>();
    }

    private int generateId() {
        return ++seq;
    }

    public void updateEpicStatus(Epic epic) {
        Status status = Status.NEW;
        int counterDone = 0;
        int counterNew = 0;
        for (int subTaskId : epic.getSubtaskIdList()) {
            SubTask subTask = subTasks.get(subTaskId);
            if (Status.DONE.equals(subTask.getStatus())) {
                counterDone++;
            } else if (subTask.getStatus().equals(Status.NEW)) {
                counterNew++;
            }
            if (counterDone == epic.getSubtaskIdList().size()) {
                status = Status.DONE;
            } else if (counterNew == epic.getSubtaskIdList().size()) {
                status = Status.NEW;
            } else {
                status = Status.IN_PROGRESS;
            }
        }
        epic.setStatus(status);
    }

    @Override
    public ArrayList<SubTask> getEpicSubtasks(int epicId) {
        ArrayList<SubTask> subTaskArrayList = new ArrayList<>();
        Epic epic = epics.get(epicId);
        for (int i : epic.getSubtaskIdList()) {
            subTaskArrayList.add(subTasks.get(i));
        }
        return subTaskArrayList;
    }

    public void addSubtaskIdToEpic(int epicId, int subtaskId) {
        Epic epic = epics.get(epicId);
        epic.getSubtaskIdList().add(subtaskId);
        updateEpicStatus(epic);
    }

    public void removeSubtaskIdFromEpic(int epicId, int subtaskId) {
        epics.get(epicId).getSubtaskIdList().remove((Integer) subtaskId);
        updateEpicStatus(epics.get(epicId));
    }

    public void removeAllSubtasksFromEpic(int epicId) {
        if (epics.get(epicId) == null) {
            return;
        }
        epics.get(epicId).getSubtaskIdList().clear();
        updateEpicStatus(epics.get(epicId));
    }

    @Override
    public ArrayList<Task> getTasks() {
        if (tasks == null) {
            return null;
        }
        for (Task task : tasks.values()) {
            historyManager.add(task);
        }
        ArrayList<Task> taskList = new ArrayList<>(tasks.values());
        return (ArrayList<Task>) taskList.clone();
    }

    @Override
    public void clearTasks() {
        for (Task task : tasks.values()) {
            historyManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public Task getTask(int id) {
        if (tasks.get(id) == null)
            return null;
        historyManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Task create(Task task) {
        task.setId(generateId());
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public void updateTask(Task task) {
        Task saved = tasks.get(task.getId());
        if (saved == null) {
            return;
        }
        tasks.put(task.getId(), task);
    }

    @Override
    public void removeTask(int id) {
        historyManager.remove(id);
        tasks.remove(id);
    }

    @Override
    public ArrayList<Epic> getEpics() {
        if (epics == null) {
            return null;
        }
        for (Epic task : epics.values()) {
            historyManager.add(task);
        }
        ArrayList<Epic> epicList = new ArrayList<>(epics.values());
        return (ArrayList<Epic>) epicList.clone();
    }

    @Override
    public void clearEpics() {
        for (Epic epic : epics.values()) {
            historyManager.remove(epic.getId());
            for (int i : epic.getSubtaskIdList()) {
                SubTask s = subTasks.get(i);
                s.setEpicId(null);
            }
            removeAllSubtasksFromEpic(epic.getId());
        }
        epics.clear();
    }

    @Override
    public Epic getEpic(int id) {
        if (epics.get(id) == null)
            return null;
        historyManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Epic create(Epic epic) {
        epic.setId(generateId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic saved = epics.get(epic.getId());
        if (saved == null) {
            return;
        }
        epic.setSubtaskIdList(saved.getSubtaskIdList());
        epic.setStatus(saved.getStatus());
        epic.setDescription(saved.getDescription());
        epics.put(epic.getId(), epic);
    }

    @Override
    public void removeEpic(int id) {
        if (epics.get(id) == null) {
            return;
        }
        historyManager.remove(id);
        removeAllSubtasksFromEpic(id);
        epics.remove(id);
    }

    @Override
    public ArrayList<SubTask> getSubtasks() {
        if (subTasks == null) {
            return null;
        }
        for (SubTask task : subTasks.values()) {
            historyManager.add(task);
        }
        ArrayList<SubTask> subtaskList = new ArrayList<>(subTasks.values());
        return (ArrayList<SubTask>) subtaskList.clone();
    }

    @Override
    public void clearSubtasks() {
        for (SubTask s : subTasks.values()) {
            historyManager.remove(s.getId());
            if (epics.get(s.getEpicId()) == null) {
                break;
            }
            removeSubtaskIdFromEpic(s.getEpicId(), s.getId());
        }
        subTasks.clear();
    }

    @Override
    public SubTask getSubtask(int id) {
        historyManager.add(subTasks.get(id));
        return subTasks.get(id);
    }

    @Override
    public SubTask create(SubTask subTask) {
        subTask.setId(generateId());
        subTasks.put(subTask.getId(), subTask);
        addSubtaskIdToEpic(subTask.getEpicId(), subTask.getId());
        return subTask;
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        SubTask saved = subTasks.get(subTask.getId());
        if (saved == null) {
            return;
        }
        subTask.setEpicId(saved.getEpicId());
        subTasks.put(subTask.getId(), subTask);
        updateEpicStatus(epics.get(saved.getEpicId()));
    }

    @Override
    public void removeSubtask(int id) {
        if (subTasks.get(id) == null) {
            return;
        }
        historyManager.remove(id);
        subTasks.remove(id);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) historyManager.getTasks();
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
