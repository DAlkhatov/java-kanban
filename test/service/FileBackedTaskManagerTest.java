package service;

import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FileBackedTaskManagerTest {
    File file;
    FileBackedTaskManager fileManager;
    Task task;
    Epic epic;
    SubTask subTask;

    @BeforeEach
    void beforeEach() {
        try {
            file = File.createTempFile("test", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileManager = FileBackedTaskManager.loadFromFile(file);
        task = new Task(Status.IN_PROGRESS, "Task", "Description of Task");
        fileManager.create(task);
        epic = new Epic(Status.NEW, "Epic", "Description of Epic");
        fileManager.create(epic);
        subTask = new SubTask(Status.DONE, "SubTask", "Description of SubTask", epic.getId());
        fileManager.create(subTask);
        fileManager.getTasks();
        fileManager.getEpics();
        fileManager.getSubtasks();
    }

    @AfterEach
    void afterEach() {
        fileManager.clearTasks();
        fileManager.clearEpics();
        fileManager.clearSubtasks();
    }

    @Test
    @DisplayName("Строки должны совпадать при добавлении данных в файл")
    void shouldBeEqualWhenAddingDataToFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            reader.readLine();
            assertEquals(reader.readLine(), "1,TASK,Task,IN_PROGRESS,Description of Task,null");
            assertEquals(reader.readLine(), "2,EPIC,Epic,DONE,Description of Epic,null");
            assertEquals(reader.readLine(), "3,SUBTASK,SubTask,DONE,Description of SubTask,2");
            reader.readLine();
            assertEquals(reader.readLine(), "1,2,3");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Строки должны быть равны при удалении данных из файла")
    void shouldBeEqualWhenDeletingDataFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            fileManager.clearTasks();
            fileManager.clearEpics();
            reader.readLine();
            assertEquals(reader.readLine(), "3,SUBTASK,SubTask,DONE,Description of SubTask,null");
            reader.readLine();
            assertEquals(reader.readLine(), "3");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}