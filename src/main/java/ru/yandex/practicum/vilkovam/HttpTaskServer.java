package ru.yandex.practicum.vilkovam;

import com.sun.net.httpserver.HttpServer;
import ru.yandex.practicum.vilkovam.handler.EpicHandler;
import ru.yandex.practicum.vilkovam.handler.HistoryHandler;
import ru.yandex.practicum.vilkovam.handler.PrioritizedHandler;
import ru.yandex.practicum.vilkovam.handler.SubtaskHandler;
import ru.yandex.practicum.vilkovam.handler.TaskHandler;
import ru.yandex.practicum.vilkovam.manager.TaskManager;
import ru.yandex.practicum.vilkovam.util.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * @author Andrew Vilkov
 * @created 16.11.2025 - 13:16
 * @project java-kanban
 */
public class HttpTaskServer {
    private static final int PORT = 8080;
    private final TaskManager manager;
    private HttpServer server;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }

    private void initialize() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/tasks", new TaskHandler(manager));
            server.createContext("/subtasks", new SubtaskHandler(manager));
            server.createContext("/epics", new EpicHandler(manager));
            server.createContext("/history", new HistoryHandler(manager));
            server.createContext("/prioritized", new PrioritizedHandler(manager));
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public void start() {
        if (server == null) {
            initialize();
        }
        server.start();
    }

    public void stop() {
        server.stop(5);
    }

    public static void main(String[] args) {
        new HttpTaskServer(Managers.getDefault()).start();
    }

}
