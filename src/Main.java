import model.Status;
import model.Epic;
import model.SubTask;
import model.Task;
import service.TaskManager;

public class Main {

    public static void main(String[] args) {
        System.out.println("Проверка модельных классов:\n");
        Task task = new Task(Status.IN_PROGRESS, "Main", "Anything");
        Epic epic = new Epic(Status.NEW, "Epic", "");
        epic.setDescription("New description");
        System.out.println(epic);
        SubTask subTask1 = new SubTask(Status.NEW, "Sub1", "Descr1", epic.getId());
        SubTask subTask2 = new SubTask(Status.DONE, "Sub2", "Descr2", epic.getId());
        epic.addSubtaskId(subTask1.getId());
        epic.addSubtaskId(subTask2.getId());
        System.out.println(epic.getSubtaskId());
        System.out.println(subTask1.getEpicId() + "\n");

        System.out.println("Проверка логики сервиса в части создания словарей:");
        TaskManager taskManager = new TaskManager();
        System.out.println(taskManager.create(task));
        System.out.println(taskManager.create(epic));
        System.out.println(taskManager.create(subTask1));
        System.out.println(taskManager.create(subTask2));
        System.out.println(taskManager + "\n");

        System.out.println("Проверка логики сервиса в части получения словарей:");
        System.out.println(taskManager.getTasks());
        System.out.println(taskManager.getEpics());
        System.out.println(taskManager.getSubTasks() + "\n");

        System.out.println("Проверка логики сервиса в части получения задач по идентификатору:");
        System.out.println(taskManager.getTask(task.getId()));
        System.out.println(taskManager.getEpic(epic.getId()));
        System.out.println(taskManager.getSubtask(subTask2.getId()) + "\n");

        System.out.println("Проверка логики сервиса в части обновления задач:");
        TaskManager newTaskManager = new TaskManager();
        Task newTask = new Task(Status.NEW, "New task!", "Description of NewTask");
        System.out.println(newTaskManager.create(newTask));
        taskManager.updateTask(newTaskManager.getTask(newTask.getId()));
        System.out.println(taskManager.getTasks() + "\n");

        System.out.println("Проверка логики сервиса в части удаления задач:");
        System.out.println(taskManager);
        taskManager.removeTask(newTask.getId());
        System.out.println(taskManager);
        taskManager.removeEpic(epic.getId());
        taskManager.removeSubtask(subTask1.getId());
        System.out.println(taskManager);
        taskManager.clearSubtasks();
        System.out.println(taskManager);

    }
}
