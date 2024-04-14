package server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import exeption.NotFoundException;
import exeption.ValidationException;
import model.Epic;
import model.Status;
import model.SubTask;
import model.Task;
import service.FileBackedTaskManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private static FileBackedTaskManager fileManager;
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    public static final String TASK_CSV = "resources/task.csv";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;

    static void writeResponse(HttpExchange exchange,
                              String responseString,
                              int responseCode) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(responseCode, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
        exchange.close();
    }

    static Status getStatus(String status) {
        switch (status.toLowerCase()) {
            case "new" -> {
                return Status.NEW;
            }
            case "in_progress" -> {
                return Status.IN_PROGRESS;
            }
            case "done" -> {
                return Status.DONE;
            }
            default -> {
                return null;
            }
        }
    }

    static LocalDateTime getStartTime(String startTime) {
        return LocalDateTime.parse(startTime, formatter);
    }

    static void getCase(HttpExchange exchange, String[] pathParts) throws IOException {
        Task task;
        Integer id = null;
        if (pathParts.length > 2) {
            try {
                id = Integer.parseInt(pathParts[2]);
            } catch (NumberFormatException e) {
                writeResponse(exchange, "Необходимо передать число, а не символ", 404);
                return;
            }
        }
        switch (pathParts[1]) {
            case "tasks":
                if (pathParts.length > 2) {
                    try {
                        task = fileManager.getTask(id);
                        writeResponse(exchange, task.toString(), 200);
                    } catch (NotFoundException e) {
                        writeResponse(exchange, String.format("Нет задачи с id %d", id), 404);
                    }
                } else {
                    writeResponse(exchange, fileManager.getTasks().toString(), 200);
                }
                break;
            case "epics":
                if (pathParts.length > 2) {
                    try {
                        task = fileManager.getEpic(Integer.parseInt(pathParts[2]));
                        writeResponse(exchange, task.toString(), 200);
                    } catch (NotFoundException e) {
                        writeResponse(exchange, String.format("Нет эпика с id %d", id), 404);
                    }
                } else {
                    writeResponse(exchange, fileManager.getEpics().toString(), 200);
                }
            case "subtasks":
                if (pathParts.length > 2) {
                    try {
                        task = fileManager.getSubtask(Integer.parseInt(pathParts[2]));
                        writeResponse(exchange, task.toString(), 200);
                        return;
                    } catch (NotFoundException e) {
                        writeResponse(exchange, String.format("Нет подзадачи с id %d", id), 404);
                    }
                } else {
                    writeResponse(exchange, fileManager.getSubtasks().toString(), 200);
                }
            default:
                writeResponse(exchange, "Неверный запрос", 406);
        }
    }


    static void postCase(HttpExchange exchange, Headers requestHeaders, String[] pathParts) throws IOException {
        try {
            Task newTask = null;
            try {
                String name = requestHeaders.get("name").getFirst();
                String description = requestHeaders.get("description").getFirst();
                String status = requestHeaders.get("status").getFirst();
                int intDuration = Integer.parseInt(requestHeaders.get("duration").getFirst());
                Duration duration = Duration.ofMinutes(intDuration);
                LocalDateTime dateTime = getStartTime(requestHeaders.get("startTime").getFirst());
                switch (pathParts[1]) {
                    case "tasks":
                        newTask = new Task(getStatus(status), name, description, dateTime, duration);
                        break;
                    case "epics":
                        newTask = new Epic(getStatus(status), name, description, dateTime, duration);
                        break;
                    case "subtasks":
                        int epicId = Integer.parseInt(requestHeaders.get("epicid").getFirst());
                        newTask = new SubTask(getStatus(status), name, description, epicId, dateTime, duration);
                        break;
                    default:
                        writeResponse(exchange, "Неверный запрос", 406);
                }
            } catch (Exception e) {
                writeResponse(exchange, "Неверно переданы заголовки", 406);
                return;
            }
            if (pathParts.length > 2) {
                int id;
                try {
                    id = Integer.parseInt(pathParts[2]);
                } catch (NumberFormatException e) {
                    writeResponse(exchange, "Необходимо передать число, а не символ", 404);
                    return;
                }
                newTask.setId(id);
                try {
                    fileManager.updateTask(newTask);
                } catch (NotFoundException e) {
                    writeResponse(exchange, "Задача не найдена", 406);
                }
                writeResponse(exchange, "Задача успешно обновлена", 201);
            } else {
                fileManager.create(newTask);
                writeResponse(exchange, "Задача успешно записана", 201);
            }

        } catch (ValidationException e) {
            writeResponse(exchange, "Задача пересекается с существующими", 406);
        }
    }

    static void deleteCase(HttpExchange exchange, String[] pathParts) throws IOException {
        switch (pathParts[1]) {
            case "tasks":
                if (pathParts.length > 2) {
                    int id = Integer.parseInt(pathParts[2]);
                    fileManager.removeTask(id);
                } else {
                    fileManager.clearTasks();
                }
                break;
            case "epics":
                if (pathParts.length > 2) {
                    int id = Integer.parseInt(pathParts[2]);
                    fileManager.removeEpic(id);
                } else {
                    fileManager.clearEpics();
                }
                break;
            case "subtasks":
                if (pathParts.length > 2) {
                    int id = Integer.parseInt(pathParts[2]);
                    fileManager.removeSubtask(id);
                } else {
                    fileManager.clearSubtasks();
                }
                break;
            default:
                writeResponse(exchange, "Неверный запрос", 406);
                return;
        }
        writeResponse(exchange, "Удаление выполнено", 200);
    }

    public static void main(String[] args) {
        fileManager = FileBackedTaskManager.loadFromFile(new File(TASK_CSV));
        HttpServer httpServer;
        try {
            httpServer = HttpServer.create();
            httpServer.bind(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TasksHandler());
            httpServer.createContext("/epics", new TasksHandler());
            httpServer.createContext("/subtasks", new TasksHandler());
            httpServer.createContext("/history", new HistoryHandler());
            httpServer.createContext("/prioritized", new PrioritizedHandler());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        httpServer.start();
    }

    static class TasksHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();
            Headers requestHeaders = exchange.getRequestHeaders();
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            switch (requestMethod) {
                case "GET":
                    getCase(exchange, pathParts);
                    break;
                case "POST":
                    postCase(exchange, requestHeaders, pathParts);
                    break;
                case "DELETE":
                    deleteCase(exchange, pathParts);
            }
        }
    }

    static class HistoryHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String history = FileBackedTaskManager.historyToString(fileManager.getHistoryManager());
            writeResponse(exchange, history, 200);
        }
    }

    static class PrioritizedHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            writeResponse(exchange, fileManager.getPrioritizedTasks().toString(), 200);
        }
    }
}

