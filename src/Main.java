import model.Status;
import model.Epic;
import model.SubTask;
import model.Task;

import service.InMemoryTaskManager;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();

        Task task = new Task(Status.IN_PROGRESS, "Main", "Anything");
        taskManager.create(task);
        taskManager.getTask(task.getId());

        Epic epic = new Epic(Status.NEW, "Epic", "Description of Epic");
        taskManager.create(epic);
        taskManager.getEpic(epic.getId());

        SubTask subTask1 = new SubTask(Status.DONE, "SubTask1", "Description of Subtask1", epic.getId());
        SubTask subTask2 = new SubTask(Status.NEW, "SubTask2", "Description of Subtask2", epic.getId());
        taskManager.create(subTask1);
        taskManager.getSubtask(subTask1.getId());
        taskManager.create(subTask2);

        printAllTasks(taskManager);

        taskManager.clearSubtasks();
        System.out.println(taskManager.getSubtasks());
        System.out.println(taskManager.getHistory());
    }

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getEpics()) {
            System.out.println(epic);
            for (Task task : manager.getEpicSubtasks(epic.getId())) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubtasks()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }
}
