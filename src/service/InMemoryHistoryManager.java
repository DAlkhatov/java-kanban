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
            history.remove(0); /* Начиная с этого модуля, по словам старшего наставника, мы начинаем работать в
            21 версии Java. В данной версии Java у ArrayList есть метод removeFirst. */
        }
        if (task != null) {
            history.add(task);
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return (ArrayList<Task>) history.clone();
    }

    public void removeFromHistory(Task task) {
        history.remove(task);
    }
}
