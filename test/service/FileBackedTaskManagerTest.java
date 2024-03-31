package service;

import exeption.ValidationException;
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
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {
    File file;
    FileBackedTaskManager fileManager;
    Task task;
    Epic epic;
    SubTask subTask;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    Duration duration = Duration.ofHours(10);

    @BeforeEach
    void beforeEach() {
        try {
            file = File.createTempFile("test", null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        fileManager = FileBackedTaskManager.loadFromFile(file);
        task = new Task(Status.IN_PROGRESS, "Task", "Description of Task",
                LocalDateTime.of(2019, 1, 1, 0, 0), duration);
        fileManager.create(task);
        epic = new Epic(Status.NEW, "Epic", "Description of Epic",
                LocalDateTime.of(2020, 1, 1, 0, 0), duration);
        fileManager.create(epic);
        subTask = new SubTask(Status.DONE, "SubTask", "Description of SubTask", epic.getId(),
                LocalDateTime.of(2021, 1, 1, 0, 0), duration);
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
            assertEquals(reader.readLine(), String.format("1,TASK,Task,IN_PROGRESS,Description of Task,null," +
                            "%s,%s", LocalDateTime.of(2019, 1, 1, 0, 0).format(formatter),
                    duration));
            assertEquals(reader.readLine(), String.format("2,EPIC,Epic,DONE,Description of Epic,null," +
                            "%s,%s", LocalDateTime.of(2021, 1, 1, 0, 0).format(formatter),
                    duration));
            assertEquals(reader.readLine(), String.format("3,SUBTASK,SubTask,DONE,Description of SubTask,2," +
                            "%s,%s", LocalDateTime.of(2021, 1, 1, 0, 0).format(formatter),
                    duration));
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
            assertEquals(reader.readLine(), String.format("3,SUBTASK,SubTask,DONE,Description of SubTask,null," +
                            "%s,%s", LocalDateTime.of(2021, 1, 1, 0, 0).format(formatter),
                    duration));
            reader.readLine();
            assertEquals(reader.readLine(), "3");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @DisplayName("Проверка пересечения временных зон")
    void shouldCheckForIntersectionOfTimeZones() {
        SubTask s1 = new SubTask(Status.DONE, "SubTask", "Description of SubTask", epic.getId(),
                LocalDateTime.of(2021, 1, 1, 9, 59), duration);
        assertThrows(ValidationException.class, () -> fileManager.create(s1));

        SubTask s2 = new SubTask(Status.DONE, "SubTask", "Description of SubTask", epic.getId(),
                LocalDateTime.of(2020, 12, 31, 14, 1), duration);
        assertThrows(ValidationException.class, () -> fileManager.create(s2));

        SubTask s3 = new SubTask(Status.DONE, "SubTask", "Description of SubTask", epic.getId(),
                LocalDateTime.of(2021, 1, 1, 10, 0), duration);
        SubTask s4 = new SubTask(Status.DONE, "SubTask", "Description of SubTask", epic.getId(),
                LocalDateTime.of(2020, 12, 31, 14, 0), duration);
        assertDoesNotThrow(() -> fileManager.create(s3));
        assertDoesNotThrow(() -> fileManager.create(s4));
    }

    @Test
    @DisplayName("")
    void should() {
        TreeSet<Task> timeSet = fileManager.getPrioritizedTasks();
        assertEquals(timeSet.getFirst(), task);
        assertEquals(timeSet.getLast(), subTask);
    }
}