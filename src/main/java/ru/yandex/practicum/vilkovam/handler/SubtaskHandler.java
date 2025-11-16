package ru.yandex.practicum.vilkovam.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.vilkovam.exceptions.BadRequest;
import ru.yandex.practicum.vilkovam.exceptions.NotFoundException;
import ru.yandex.practicum.vilkovam.manager.TaskManager;
import ru.yandex.practicum.vilkovam.model.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author Andrew Vilkov
 * @created 16.11.2025 - 13:42
 * @project java-kanban
 */
public class SubtaskHandler extends BaseHttpHandler {

    public SubtaskHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void getRequest(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromRequestPath(exchange);
        String json;

        if (id.isPresent()) {
            Optional<Subtask> subtaskById = manager.getSubtaskById(id.get());
            Subtask subtask = subtaskById.orElseThrow(() -> new NotFoundException("Not found subtask for id: " + id));
            json = gson.toJson(subtask);
        } else {
            List<Subtask> subtasks = manager.getAllSubtask();
            json = gson.toJson(subtasks);
        }
        sendText(exchange, 200, json);
    }

    @Override
    protected void postRequest(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromRequestPath(exchange);
        Subtask subtask = getBody(exchange, Subtask.class);

        if (id.isEmpty()) {
            Subtask created = manager.createSubtask(subtask);
            if (created == null) {
                throw new BadRequest("Bad Request for create subtask");
            }
        } else {
            manager.updateSubtask(subtask);
        }
        sendText(exchange, 201, "");
    }

    @Override
    protected void deleteRequest(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromRequestPath(exchange);
        id.ifPresent(manager::removeSubtaskById);
        sendText(exchange, 200, "");
    }
}
