package ru.yandex.practicum.vilkovam.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.vilkovam.manager.TaskManager;
import ru.yandex.practicum.vilkovam.model.Task;

import java.io.IOException;
import java.util.Set;

/**
 * @author Andrew Vilkov
 * @created 16.11.2025 - 17:15
 * @project java-kanban
 */
public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void getRequest(HttpExchange exchange) throws IOException {
        Set<Task> prioritizedTasks = manager.getPrioritizedTasks();
        String json = gson.toJson(prioritizedTasks);
        sendText(exchange, 200, json);
    }
}
