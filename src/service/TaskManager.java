package service;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    ArrayList<SubTask> getEpicSubtasks(int epicId);

    List<Task> getTasks();

    void clearTasks();

    Task getTask(int id);

    Task create(Task task);

    void updateTask(Task task);

    void removeTask(int id);

    List<Epic> getEpics();

    void clearEpics();

    Epic getEpic(int id);

    Epic create(Epic epic);

    void updateEpic(Epic epic);

    void removeEpic(int id);

    List<SubTask> getSubtasks();

    void clearSubtasks();

    SubTask getSubtask(int id);

    SubTask create(SubTask subTask);

    void updateSubtask(SubTask subTask);

    void removeSubtask(int id);

    List<Task> getHistory();
}
