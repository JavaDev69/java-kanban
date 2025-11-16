package ru.yandex.practicum.vilkovam.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.yandex.practicum.vilkovam.adapters.DurationAdapter;
import ru.yandex.practicum.vilkovam.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.vilkovam.exceptions.BadRequest;
import ru.yandex.practicum.vilkovam.exceptions.NotFoundException;
import ru.yandex.practicum.vilkovam.exceptions.OverlappingTaskException;
import ru.yandex.practicum.vilkovam.manager.TaskManager;
import ru.yandex.practicum.vilkovam.model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * @author Andrew Vilkov
 * @created 16.11.2025 - 13:27
 * @project java-kanban
 */
public abstract class BaseHttpHandler implements HttpHandler {
    protected TaskManager manager;
    protected Gson gson;

    protected BaseHttpHandler(TaskManager manager) {
        this.manager = manager;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    getRequest(exchange);
                    break;
                case "POST":
                    postRequest(exchange);
                    break;
                case "DELETE":
                    deleteRequest(exchange);
                    break;
                default:
                    throw new UnsupportedOperationException(exchange.getRequestMethod());
            }
        } catch (NotFoundException e) {
            sendNotFound(exchange);
        } catch (OverlappingTaskException e) {
            sendHasOverlaps(exchange);
        } catch (BadRequest e) {
            sendText(exchange, 400, "");
        } catch (Exception e) {
            serverError(exchange);
        }
    }

    protected abstract void getRequest(HttpExchange exchange) throws IOException;

    protected void postRequest(HttpExchange exchange) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void deleteRequest(HttpExchange exchange) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    protected void sendText(HttpExchange h, int code, String text) throws IOException {
        sendAnswerWithText(h, code, text);
    }

    protected void sendNotFound(HttpExchange h) throws IOException {
        sendAnswerWithoutText(h, 404);
    }

    protected void sendHasOverlaps(HttpExchange h) throws IOException {
        sendAnswerWithoutText(h, 406);
    }

    protected void serverError(HttpExchange h) throws IOException {
        sendAnswerWithoutText(h, 500);
    }

    private void sendAnswerWithoutText(HttpExchange h, int code) throws IOException {
        sendAnswerWithText(h, code, "");
    }

    private void sendAnswerWithText(HttpExchange h, int code, String text) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        h.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        h.sendResponseHeaders(code, resp.length);
        h.getResponseBody().write(resp);
        h.close();
    }

    protected Optional<Integer> getIdFromRequestPath(HttpExchange exchange) {
        String[] paths = getRequestPath(exchange);
        if (paths.length > 2) {
            return Optional.of(Integer.parseInt(paths[2]));
        }
        return Optional.empty();
    }

    protected String[] getRequestPath(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();
        return path.split("/");
    }

    protected <T extends Task> T getBody(HttpExchange exchange, Class<T> clazz) throws IOException {
        InputStream requestBody = exchange.getRequestBody();
        String body = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
        if (body.isEmpty()) {
            throw new BadRequest("Empty body");
        }
        return gson.fromJson(body, clazz);
    }
}
