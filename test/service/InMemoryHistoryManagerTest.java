package service;

import model.Epic;
import model.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    static InMemoryTaskManager taskManager;
    int quantity = 8;

    @BeforeEach
    void beforeAll() {
        taskManager = new InMemoryTaskManager();
        for (int i = 0; i < quantity; i++) {
            int counter = i + 1;
            if (i % 2 == 0) {
                Task task = new Task(Status.NEW, "Task " + counter, "Description " + counter);
                taskManager.create(task);
                taskManager.getTask(task.getId());
            } else {
                Task task = new Epic(Status.NEW, "Epic " + counter, "Description of epic " + counter);
                taskManager.create(task);
                taskManager.getTask(task.getId());
            }
        }
    }

    @DisplayName("Должны быть возвращены одинаковые Значения")
    @Test
    void shouldReturnEqualValues() {
        taskManager.removeTask(5);
        assertEquals(taskManager.getHistory().size() , quantity - 1);
    }

    @DisplayName("Должны быть возвращены одинаковые Task-объекты")
    @Test
    void shouldBeeEqualsTasks() {
        Task task = new Task(Status.NEW, "New Task ", "New Description ");
        taskManager.create(task);
        taskManager.getTask(task.getId());
        assertEquals(taskManager.getTask(task.getId()), taskManager.getHistory().getLast());
    }
}