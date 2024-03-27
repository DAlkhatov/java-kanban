package service;

import exeption.ManagerSaveException;
import exeption.ValidationException;
import model.*;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class FileBackedTaskManager extends InMemoryTaskManager {

    public static final String TASK_CSV = "resources/task.csv";
    public File file;
    ZoneOffset zoneOffset = ZoneOffset.ofHours(3);
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    Comparator<Task> comparator = Comparator.comparing(Task::getStartTime);
    TreeSet<Task> prioritizedTasks = new TreeSet<>(comparator);

    public FileBackedTaskManager(HistoryManager historyManager) {
        this(historyManager, new File(TASK_CSV));
    }

    public FileBackedTaskManager(File file) {
        this(Managers.getDefaultHistory(), file);
    }

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public static void main(String[] args) {
        FileBackedTaskManager fileManager1 = FileBackedTaskManager.loadFromFile(new File(TASK_CSV));
        Duration duration = Duration.ofHours(2);

        Task task = new Task(Status.DONE, "Task", "Description of Task",
                LocalDateTime.of(2019, 1, 1, 0, 0), duration);
        fileManager1.create(task);
        Epic epic = new Epic(Status.NEW, "Epic", "Description of Epic");
        fileManager1.create(epic);
        SubTask st = new SubTask(Status.DONE, "SubTask", "Description of SubTask", epic.getId(),
                LocalDateTime.of(2018, 1, 1, 10, 5), duration);
        SubTask st1 = new SubTask(Status.IN_PROGRESS, "SubTask1", "Description of SubTask1", epic.getId(),
                LocalDateTime.of(2017, 3, 25, 10, 5), Duration.ofDays(3));
        fileManager1.create(st);
        fileManager1.create(st1);
        fileManager1.getTasks();
        fileManager1.getEpics();
        fileManager1.getSubtasks();

        FileBackedTaskManager fileManager2 = FileBackedTaskManager.loadFromFile(new File(TASK_CSV));
        System.out.println(fileManager2.getTasks());
        System.out.println(fileManager2.getEpics());
        System.out.println(fileManager2.getSubtasks());
        System.out.println();
        for (Task task1 : fileManager1.getPrioritizedTasks()) {
            System.out.println(task1);
        }
    }

    public TreeSet<Task> getPrioritizedTasks() {
        prioritizedTasks.addAll(tasks.values());
        prioritizedTasks.addAll(subTasks.values());
        return prioritizedTasks;
    }

    private long getStartEpochSecond(Task task) {
        return task.getStartTime().toInstant(zoneOffset).getEpochSecond();
    }

    private long getEndEpochSecond(Task task) {
        return task.getEndTime().toInstant(zoneOffset).getEpochSecond();
    }

    public boolean isIntersection(Task t1, Task t2) {
        return ((getStartEpochSecond(t1) < getStartEpochSecond(t2)) ||
                (getStartEpochSecond(t1) < getEndEpochSecond(t2))) &&
                (getStartEpochSecond(t2) < getEndEpochSecond(t1)) ||
                (((getStartEpochSecond(t2) < getStartEpochSecond(t1)) ||
                        (getStartEpochSecond(t2) < getEndEpochSecond(t1))) &&
                        (getStartEpochSecond(t1) < getEndEpochSecond(t2)));
    }

    public void add(Task task) {
        Stream<Task> allTasks = Stream.concat(tasks.values().stream(), subTasks.values().stream());
        if (allTasks.anyMatch(t -> !t.equals(task) && isIntersection(task, t))) {
            throw new ValidationException("Невозможно создать задачу из-за неверно указанного времени");
        }
    }

/*    public void add(Task task) {
        for (Task t : tasks.values()) {
            if (!t.equals(task) && isIntersection(task, t)) {
                throw new ValidationException("Невозможно создать подзадачу из-за неверно указанного времени");
            }
        }
        for (SubTask s : subTasks.values()) {
            if (!s.equals(task) && isIntersection(task, s)) {
                throw new ValidationException("Невозможно создать подзадачу из-за неверно указанного времени");
            }
        }
    }*/

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

    @Override
    public SubTask create(SubTask task) {
        add(task);
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
        add(task);
/*        for (Task task1 : tasks.values()) {
            if (!task1.equals(task) && isIntersection(task, task1)) {
                throw new ValidationException("Невозможно создать подзадачу из-за неверно указанного времени");
            }
        }*/
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
        return String.format("%d,%s,%s,%s,%s,%d,%s,%s", task.getId(), task.getType(), task.getName(), task.getStatus(),
                task.getDescription(), task.getEpicId(), task.getStartTime().format(formatter), task.getDuration());
    }

    void save() {
        try (final BufferedWriter writer = new BufferedWriter(new FileWriter(file, UTF_8))) {
            try {
                if (file.length() == 0) {
                    writer.append("id,type,name,status,description,epic,startTime,duration");
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

        String[] dateTime = columns[6].split(" ");
        String[] date = dateTime[0].split("\\.");
        String[] time = dateTime[1].split(":");
        LocalDateTime localDateTime = LocalDateTime.of(Integer.parseInt(date[2]), Integer.parseInt(date[1]),
                Integer.parseInt(date[0]), Integer.parseInt(time[0]), Integer.parseInt(time[1]));

        Duration duration = Duration.parse(columns[7]);

        if (!columns[5].equals("null")) {
            epic = Integer.parseInt(columns[5]);
        }
        Task task = null;
        switch (type) {
            case TASK:
                task = new Task(status, name, description, localDateTime, duration);
                tasks.put(id, task);
                break;
            case SUBTASK:
                task = new SubTask(status, name, description, epic, localDateTime, duration);
                subTasks.put(id, (SubTask) task);
                break;
            case EPIC:
                task = new Epic(status, name, description, localDateTime, duration);
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
        Arrays.stream(ids).forEach(s -> historyIds.add(Integer.parseInt(s)));
        return historyIds;
    }

    private TaskType getType(int id) {
        return Stream.of(tasks.values(), epics.values(), subTasks.values())
                .flatMap(Collection::stream)
                .filter(task -> task.getId() == id)
                .map(Task::getType)
                .findFirst()
                .orElse(null);
    }
}
