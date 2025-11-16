package ru.yandex.practicum.vilkovam.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.vilkovam.manager.TaskManager;
import ru.yandex.practicum.vilkovam.model.Task;

import java.io.IOException;
import java.util.List;

/**
 * @author Andrew Vilkov
 * @created 16.11.2025 - 17:15
 * @project java-kanban
 */
public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void getRequest(HttpExchange exchange) throws IOException {
        List<Task> history = manager.getHistory();
        String json = gson.toJson(history);
        sendText(exchange, 200, json);
    }
}
