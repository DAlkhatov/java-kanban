package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskTest {

    Task task = new Task(Status.NEW, "", "");

    @Test
    void shouldChangeAndReturnName() {
        String newName = "New name";
        task.setName(newName);
        assertEquals(newName, task.getName());
    }
}