package ru.yandex.practicum.vilkovam.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.vilkovam.exceptions.BadRequest;
import ru.yandex.practicum.vilkovam.exceptions.NotFoundException;
import ru.yandex.practicum.vilkovam.manager.TaskManager;
import ru.yandex.practicum.vilkovam.model.Task;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author Andrew Vilkov
 * @created 16.11.2025 - 13:42
 * @project java-kanban
 */
public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void getRequest(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromRequestPath(exchange);
        String json;

        if (id.isPresent()) {
            Optional<Task> taskById = manager.getTaskById(id.get());
            Task task = taskById.orElseThrow(() -> new NotFoundException("Not found task for id: " + id));
            json = gson.toJson(task);
        } else {
            List<Task> tasks = manager.getAllTask();
            json = gson.toJson(tasks);
        }

        sendText(exchange, 200, json);
    }

    @Override
    protected void postRequest(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromRequestPath(exchange);
        Task task = getBody(exchange, Task.class);

        if (id.isEmpty()) {
            Task created = manager.createTask(task);
            if (created == null) {
                throw new BadRequest("Bad Request for create task");
            }
        } else {
            manager.updateTask(task);
        }
        sendText(exchange, 201, "");
    }

    @Override
    protected void deleteRequest(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromRequestPath(exchange);
        id.ifPresent(manager::removeTaskById);
        sendText(exchange, 200, "");
    }
}
