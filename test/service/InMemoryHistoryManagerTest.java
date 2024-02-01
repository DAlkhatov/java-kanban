package service;

import model.Epic;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    static InMemoryTaskManager taskManager;
    static Task task;
    static Epic epic;
    ArrayList<Task> tasks;

    @BeforeEach
    void beforeAll() {
        taskManager = new InMemoryTaskManager();
        task = new Task(Status.NEW, "Task", "Description of the Task");
        taskManager.create(task);
        epic = new Epic(Status.NEW, "Epic", "Description of the Epic");
        taskManager.create(epic);
        tasks = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            taskManager.getTask(task.getId());
            tasks.add(task);
        }
    }

    @DisplayName("Должны быть возвращены одинаковые ArrayList<>")
    @Test
    void shouldReturnEqualArrayLists() {
        taskManager.getEpic(epic.getId());
        tasks.add(epic);
        assertEquals(tasks, taskManager.getHistory());
    }

    @DisplayName("Должны быть возвращены разные ArrayList<>")
    @Test
    void shouldReturnDifferentArrayLists() {
        taskManager.getTask(task.getId());
        taskManager.getEpic(epic.getId());
        tasks.add(task);
        tasks.add(epic);
        assertNotEquals(tasks, taskManager.getHistory());
    }

}