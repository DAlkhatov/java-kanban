package service;

import model.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager {

    private final ArrayList<Task> history;

    public InMemoryHistoryManager() {
        this.history = new ArrayList<>();
    }

    @Override
    public void add(Task task) {
        if (history.size() > 9) {
            history.removeFirst();
        }
        if (task != null) {
            history.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) history.clone();
    }
}
