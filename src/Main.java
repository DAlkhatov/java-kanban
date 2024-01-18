import model.Status;
import model.Epic;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        Task task = new Task(Status.IN_PROGRESS, "Main", "Anything");
        Epic epic = new Epic(Status.NEW, "Epic", "");

        System.out.println("Проверка логики сервиса в части создания словарей:");
        TaskManager taskManager = new TaskManager();
        System.out.println(taskManager.create(task));
        System.out.println(taskManager.create(epic));

        System.out.println("\nПроверка логики сервиса в части обновления статуса эпика:");
        SubTask subTask1 = new SubTask(Status.DONE, "Subtask1", "Description of subtask 1", epic.getId());
        SubTask subTask2 = new SubTask(Status.IN_PROGRESS, "Subtask2", "Description of subtask 2", epic.getId());
        System.out.println(taskManager.create(subTask1));
        System.out.println(taskManager.create(subTask2));
        taskManager.addSubtaskId(epic.getId(), subTask1.getId());
        System.out.println(epic);
        taskManager.addSubtaskId(epic.getId(), subTask2.getId());
        System.out.println(epic);

        System.out.println("\nПроверка логики сервиса в части получения словарей:");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubtasks());

        System.out.println("\nПроверка логики сервиса в части получения задач по идентификатору:");
        System.out.println(taskManager.getTask(task.getId()));
        System.out.println(taskManager.getEpic(epic.getId()));
        System.out.println(taskManager.getSubtask(subTask2.getId()));

        System.out.println("\nПроверка логики сервиса в части обновления задач:");
        Task anotherTask = new Task(Status.NEW, "Another task", "Description of another task");
        anotherTask.setId(1);
        taskManager.updateTask(anotherTask);
        System.out.println(taskManager.getTasks());

        System.out.println("\nПроверка логики сервиса в части обновления эпиков:");
        Epic newEpic = new Epic(Status.NEW, "New epic", "Description of new epic");
        newEpic.setId(2);
        taskManager.updateEpic(newEpic);
        System.out.println(taskManager.getEpics());

        System.out.println("\nПроверка логики сервиса в части обновления эпиков:");
        SubTask subTask = new SubTask(Status.NEW, "New subtask", "Description of new subtask",
                epic.getId());
        subTask.setId(subTask1.getId());
        System.out.println(taskManager.getSubtasks());
        taskManager.updateSubtask(subTask);
        System.out.println(taskManager.getSubtasks());

        System.out.println("\nПроверка логики сервиса в части удаления задач по id:");
        System.out.println(taskManager);
        taskManager.removeTask(task.getId());
        System.out.println(taskManager);
        taskManager.removeEpic(epic.getId());
        System.out.println(taskManager);
        taskManager.removeSubtask(subTask1.getId());
        System.out.println(taskManager);

        System.out.println("\nПроверка логики сервиса в части полного удаления задач:");
        epic.setName("New name");
        taskManager.create(task);
        taskManager.create(epic);
        System.out.println(taskManager);
        taskManager.clearTasks();
        System.out.println(taskManager);
        taskManager.clearEpics();
        System.out.println(taskManager);
        taskManager.clearSubtasks();
        System.out.println(taskManager);
    }
}
