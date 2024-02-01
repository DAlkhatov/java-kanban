package service;

public class Managers {

    InMemoryTaskManager taskManager;
    InMemoryHistoryManager historyManager;

    public Managers() {
        this.taskManager = new InMemoryTaskManager();
        this.historyManager = new InMemoryHistoryManager();
    }

    public TaskManager getDefault() {
        return taskManager;
    }

    public HistoryManager getDefaultHistory() {
        return historyManager;
    }
}
