package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {

    static InMemoryTaskManager taskManager;
    static Task task;
    static Epic epic;
    static SubTask subTask1;
    static SubTask subTask2;

    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
        task = new Task(Status.NEW, "Task", "Description of the Task");
        taskManager.create(task);
        epic = new Epic(Status.NEW, "Epic", "Description of the Epic");
        taskManager.create(epic);
        subTask1 = new SubTask(Status.NEW, "", "", epic.getId());
        subTask2 = new SubTask(Status.NEW, "", "", epic.getId());
        taskManager.create(subTask1);
        taskManager.create(subTask2);
    }

    @DisplayName("Статус Эпика должен принять значение, в соответствии со значениями Подзадач")
    @Test
    void shouldUpdateEpicStatus() {
        assertEquals(Status.NEW, epic.getStatus());
        subTask1.setStatus(Status.DONE);
        taskManager.updateEpicStatus(epic);
        assertEquals(Status.IN_PROGRESS, epic.getStatus());
        subTask2.setStatus(Status.DONE);
        taskManager.updateEpicStatus(epic);
        assertEquals(Status.DONE, epic.getStatus());
        taskManager.removeAllSubtasksFromEpic(epic.getId());
        assertEquals(Status.NEW, epic.getStatus());
    }

    @DisplayName("ArrayList-ы с Подзадачами должны быть одинаковые")
    @Test
    void shouldReturnEqualsArrayListWithSubtasks() {
        ArrayList<SubTask> subTaskArrayList = new ArrayList<>();
        subTaskArrayList.add(subTask1);
        subTaskArrayList.add(subTask2);
        assertEquals(subTaskArrayList, taskManager.getEpicSubtasks(epic.getId()));

        taskManager.removeSubtaskIdFromEpic(epic.getId(), subTask1.getId());
        subTaskArrayList.remove(subTask1);
        assertEquals(subTaskArrayList, taskManager.getEpicSubtasks(epic.getId()));
    }

    @DisplayName("ArrayList-ы с Задачами должны быть одинаковые")
    @Test
    void shouldReturnEqualsArrayListWithTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task);
        assertEquals(tasks, taskManager.getTasks());

        taskManager.clearTasks();
        tasks.clear();
        assertEquals(tasks, taskManager.getTasks());
    }

    @DisplayName("Возвращаемый task должен быть равен помещаемому task в taskManager")
    @Test
    void shouldReturnEqualTask() {
        assertEquals(task, taskManager.getTask(task.getId()));
    }

    @DisplayName("Должен быть обновлен старый Task")
    @Test
    void shouldUpdateOldTask() {
        Task task1 = new Task(Status.DONE, "", "");
        task1.setId(task.getId());
        taskManager.updateTask(task1);
        assertEquals(task1, taskManager.getTask(task.getId()));
    }

    @DisplayName("Task должен быть удален по id")
    @Test
    void shouldRemoveTask() {
        int id = task.getId();
        taskManager.removeTask(id);
        assertNull(taskManager.getTask(id));
    }

    @DisplayName("ArrayList-ы с Эпиками должны быть одинаковые")
    @Test
    void shouldReturnEqualsArrayListWithEpics() {
        ArrayList<Epic> epics = new ArrayList<>();
        epics.add(epic);
        assertEquals(epics, taskManager.getEpics());

        taskManager.clearEpics();
        epics.clear();
        assertEquals(epics, taskManager.getEpics());
    }

    @DisplayName("Возвращаемый task должен быть равен помещаемому task в taskManager")
    @Test
    void shouldReturnEqualEpic() {
        assertEquals(epic, taskManager.getEpic(epic.getId()));
    }

    @DisplayName("Должен быть обновлен старый Epic")
    @Test
    void shouldUpdateOldEpic() {
        Epic epic1 = new Epic(Status.DONE, "", "");
        epic1.setId(epic.getId());
        taskManager.updateEpic(epic1);
        assertEquals(epic1, taskManager.getEpic(epic.getId()));
    }

    @DisplayName("Epic должен быть удален по id")
    @Test
    void shouldRemoveEpic() {
        taskManager.removeEpic(epic.getId());
        assertNull(taskManager.getTask(epic.getId()));
    }


    @DisplayName("ArrayList<SubTask> должны быть одинаковые")
    @Test
    void shouldReturnEqualsArrayListWithSubtask() {
        ArrayList<SubTask> subTasks = new ArrayList<>();
        subTasks.add(subTask1);
        subTasks.add(subTask2);
        assertEquals(subTasks, taskManager.getSubtasks());

        taskManager.clearSubtasks();
        subTasks.clear();
        assertEquals(subTasks, taskManager.getSubtasks(), "Anything");
    }

    @DisplayName("Возвращаемый subtask должен быть равен помещаемому subtask в taskManager")
    @Test
    void shouldReturnEqualSubtask() {
        assertEquals(subTask1, taskManager.getSubtask(subTask1.getId()));
    }

    @DisplayName("Должен быть обновлен старый subtask")
    @Test
    void shouldUpdateOldSubtask() {
        SubTask subTask = new SubTask(Status.DONE, "", "", epic.getId());
        subTask.setId(subTask1.getId());
        taskManager.updateSubtask(subTask);
        assertEquals(subTask, taskManager.getSubtask(subTask1.getId()));
    }

    @DisplayName("subtask должен быть удален по id")
    @Test
    void shouldRemoveSubtask() {
        taskManager.removeSubtask(subTask1.getId());
        taskManager.removeSubtask(subTask2.getId());
        ArrayList<SubTask> subTasks = new ArrayList<>();
        assertEquals(subTasks, taskManager.getSubtasks());
    }
}