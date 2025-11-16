package ru.yandex.practicum.vilkovam.handler;

import com.sun.net.httpserver.HttpExchange;
import ru.yandex.practicum.vilkovam.exceptions.BadRequest;
import ru.yandex.practicum.vilkovam.exceptions.NotFoundException;
import ru.yandex.practicum.vilkovam.manager.TaskManager;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

/**
 * @author Andrew Vilkov
 * @created 16.11.2025 - 13:42
 * @project java-kanban
 */
public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager manager) {
        super(manager);
    }

    @Override
    protected void getRequest(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromRequestPath(exchange);
        String json;

        if (id.isPresent()) {
            Optional<Epic> epicById = manager.getEpicById(id.get());
            if (epicById.isEmpty()) {
                throw new NotFoundException("Not found epic for id: " + id);
            }

            if (isSubtaskRequested(exchange)) {
                List<Subtask> allSubtaskByEpicId = manager.getAllSubtaskByEpicId(id.get());
                json = gson.toJson(allSubtaskByEpicId);
            } else {
                Epic epic = epicById.get();
                json = gson.toJson(epic);
            }
        } else {
            List<Epic> epics = manager.getAllEpic();
            json = gson.toJson(epics);
        }
        sendText(exchange, 200, json);
    }

    @Override
    protected void postRequest(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromRequestPath(exchange);
        Epic epic = getBody(exchange, Epic.class);

        if (id.isEmpty()) {
            Epic created = manager.createEpic(epic);
            if (created == null) {
                throw new BadRequest("Bad Request for create epic");
            }
        } else {
            manager.updateEpic(epic);
        }
        sendText(exchange, 201, "");
    }

    @Override
    protected void deleteRequest(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getIdFromRequestPath(exchange);
        id.ifPresent(manager::removeEpicById);
        sendText(exchange, 200, "");
    }

    private boolean isSubtaskRequested(HttpExchange exchange) {
        String[] requestPath = getRequestPath(exchange);

        return requestPath.length > 3 && "subtasks".equalsIgnoreCase(requestPath[3]);
    }
}
