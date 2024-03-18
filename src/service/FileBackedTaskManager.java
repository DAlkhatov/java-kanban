package service;

import exeption.ManagerSaveException;
import model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public static final String TASK_CSV = "resources/task.csv";
    public static File file = new File(TASK_CSV);

    public FileBackedTaskManager() {
    }

    public FileBackedTaskManager(HistoryManager historyManager) {
        this(historyManager, new File(TASK_CSV));
    }

    public static void main(String[] args) {
        FileBackedTaskManager fileManager1 = FileBackedTaskManager.loadFromFile(file);

        Task task = new Task(Status.DONE, "Task", "Description of Task");
        fileManager1.create(task);
        Epic epic = new Epic(Status.NEW, "Epic", "Description of Epic");
        fileManager1.create(epic);
        SubTask st = new SubTask(Status.IN_PROGRESS, "SubTask", "Description of SubTask", epic.getId());
        fileManager1.create(st);
        fileManager1.getTasks();
        fileManager1.getEpics();
        fileManager1.getSubtasks();

        FileBackedTaskManager fileManager2 = FileBackedTaskManager.loadFromFile(file);
        System.out.println(fileManager2.getTasks());
        System.out.println(fileManager2.getEpics());
        System.out.println(fileManager2.getSubtasks());
    }

    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasksList = super.getTasks();
        save();
        return tasksList;
    }

    @Override
    public void clearTasks() {
        super.clearTasks();
        save();
    }

    @Override
    public Task getTask(int id) {
        Task task = super.getTask(id);
        save();
        return task;
    }

    public FileBackedTaskManager(File file) {
        this(Managers.getDefaultHistory(), file);
    }

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        FileBackedTaskManager.file = file;
    }

    @Override
    public SubTask create(SubTask task) {
        SubTask t = super.create(task);
        save();
        return t;
    }

    @Override
    public void updateSubtask(SubTask subTask) {
        super.updateSubtask(subTask);
        save();
    }

    @Override
    public void removeSubtask(int id) {
        super.removeSubtask(id);
        save();
    }

    @Override
    public Epic create(Epic task) {
        Epic t = super.create(task);
        save();
        return t;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpic(int id) {
        super.removeEpic(id);
        save();
    }

    @Override
    public ArrayList<SubTask> getSubtasks() {
        ArrayList<SubTask> subTaskList = super.getSubtasks();
        save();
        return subTaskList;
    }

    @Override
    public void clearSubtasks() {
        super.clearSubtasks();
        save();
    }

    @Override
    public SubTask getSubtask(int id) {
        SubTask subTask = super.getSubtask(id);
        save();
        return subTask;
    }

    @Override
    public Task create(Task task) {
        Task t = super.create(task);
        save();
        return t;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void removeTask(int id) {
        super.removeTask(id);
        save();
    }

    @Override
    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicList = super.getEpics();
        save();
        return epicList;
    }

    @Override
    public void clearEpics() {
        super.clearEpics();
        save();
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = super.getEpic(id);
        save();
        return epic;
    }

    public String toString(Task task) {
        return String.format("%d,%s,%s,%s,%s,%d", task.getId(), task.getType(), task.getName(), task.getStatus(),
                task.getDescription(), task.getEpicId());
    }

    void save() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file, UTF_8))) {
            String firstString = "id,type,name,status,description,epic";
            try (BufferedReader br = new BufferedReader(new FileReader(file, UTF_8))) {
                String firstLine = String.valueOf(br.readLine());
                if (!firstLine.equals(firstString)) {
                    writer.append(firstString);
                    writer.newLine();
                }
            } catch (IOException e) {
                throw new ManagerSaveException(String.format("Ошибка чтения файла: %s", file.getAbsolutePath()), e);
            }
            for (Map.Entry<Integer, Task> entry : tasks.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, Epic> entry : epics.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            for (Map.Entry<Integer, SubTask> entry : subTasks.entrySet()) {
                writer.append(toString(entry.getValue()));
                writer.newLine();
            }
            writer.newLine();
            writer.append(historyToString(historyManager));
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("Ошибка в файле: %s", file.getAbsolutePath()), e);
        }
    }

    public void init() {
        loadFromFile();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        manager.init();
        return manager;
    }

    public void loadFromFile() {
        int maxId = 0;
        try (final BufferedReader reader = new BufferedReader(new FileReader(file, UTF_8))) {
            reader.readLine();
            while (reader.ready()) {
                String line = reader.readLine();
                if (line.isEmpty()) {
                    break;
                }
                final Task task = fromString(line);
                final int id = task.getId();
                switch (task.getType()) {
                    case TaskType.TASK:
                        tasks.put(id, task);
                        break;
                    case TaskType.EPIC:
                        epics.put(id, (Epic) task);
                        break;
                    case TaskType.SUBTASK:
                        subTasks.put(id, (SubTask) task);
                }
                if (maxId < id) {
                    maxId = id;
                }
            }
            String history = reader.readLine();
            if (history == null) {
                return;
            }
            // История
            for (Integer i : historyFromString(history)) {
                switch (getType(i)) {
                    case TaskType.TASK -> historyManager.add(tasks.get(i));
                    case TaskType.EPIC -> historyManager.add(epics.get(i));
                    case TaskType.SUBTASK -> historyManager.add(subTasks.get(i));
                    default -> {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException(String.format("Ошибка при загрузке файла: %s", file.getAbsolutePath()), e);
        }
        seq = maxId;
    }

    public Task fromString(String value) {
        final String[] columns = value.split(",");
        Integer id = Integer.parseInt(columns[0]);
        TaskType type = TaskType.valueOf(columns[1]);
        String name = columns[2];
        Status status = Status.valueOf(columns[3]);
        String description = columns[4];
        Integer epic = null;
        if (!columns[5].equals("null")) {
            epic = Integer.parseInt(columns[5]);
        }
        Task task = null;
        switch (type) {
            case TASK:
                task = new Task(status, name, description);
                tasks.put(id, task);
                break;
            case SUBTASK:
                task = new SubTask(status, name, description, epic);
                subTasks.put(id, (SubTask) task);
                break;
            case EPIC:
                task = new Epic(status, name, description);
                epics.put(id, (Epic) task);
        }
        if (task != null) {
            task.setId(id);
        }
        return task;
    }

    static String historyToString(HistoryManager manager) {
        StringBuilder sb = new StringBuilder();
        for (Task task : manager.getTasks()) {
            sb.append(task.getId());
            if (!task.equals(manager.getTasks().getLast())) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    static List<Integer> historyFromString(String value) {
        final String[] ids = value.split(",");
        List<Integer> historyIds = new ArrayList<>();
        for (String s : ids) {
            Integer id = Integer.parseInt(s);
            historyIds.add(id);
        }
        return historyIds;
    }

    private TaskType getType(int id) {
        TaskType type = null;
        for (Task task : tasks.values()) {
            if (task.getId() == id) {
                type = task.getType();
                return type;
            }
        }
        for (Task task : epics.values()) {
            if (task.getId() == id) {
                type = task.getType();
                return type;
            }
        }
        for (Task task : subTasks.values()) {
            if (task.getId() == id) {
                type = task.getType();
                return type;
            }
        }
        return type;
    }
}
