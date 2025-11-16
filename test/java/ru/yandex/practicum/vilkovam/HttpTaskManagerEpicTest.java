package ru.yandex.practicum.vilkovam;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.time.Duration.ofMinutes;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.yandex.practicum.vilkovam.model.TaskStatus.NEW;

/**
 * @author Andrew Vilkov
 * @created 16.11.2025 - 17:54
 * @project java-kanban
 */
class HttpTaskManagerEpicTest extends BaseHttpTests {

    @Test
    void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);

        String taskJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Epic> epicsFromManager = taskManager.getAllEpic();

        assertNotNull(epicsFromManager, "Эпики не возвращаются");
        assertEquals(1, epicsFromManager.size(), "Некорректное количество эпиков");
        assertEquals(DEFAULT_NAME, epicsFromManager.getFirst().getName(), "Некорректное имя эпика");
    }

    @Test
    void testGetAllEpics() throws IOException, InterruptedException {
        Epic epic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(epic);

        var secondEpic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(secondEpic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = taskManager.getAllEpic();
        String expected = gson.toJson(epicsFromManager);

        assertEquals(expected, response.body(), "Неккоректное тело ответа");
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        Epic epic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(epic);

        var secondEpic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(secondEpic);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + secondEpic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Epic> epicsFromManager = taskManager.getAllEpic();

        assertEquals(1, epicsFromManager.size(), "Неккоректное количество эпиков");
        assertIterableEquals(Collections.singletonList(epic), epicsFromManager, "Эпики не соответствуют");
    }

    @Test
    void testGetEpicById() throws IOException, InterruptedException {
        LocalDateTime nowDateTime = LocalDateTime.now();

        Epic epic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(epic);

        var secondEpic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(secondEpic);

        Subtask subtask1 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, secondEpic.getId(), ofMinutes(1), nowDateTime.minusHours(1));
        Subtask subtask2 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, secondEpic.getId(), ofMinutes(2), nowDateTime);
        Subtask subtask3 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, secondEpic.getId(), ofMinutes(20), nowDateTime.plusDays(1));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/" + secondEpic.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic actual = gson.fromJson(response.body(), Epic.class);

        assertNotNull(actual, "Эпик не возвращается");
        assertEquals(secondEpic, actual, "Неккоректное тело ответа");
    }

    @Test
    void testGetEpicByWrongId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/epics/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Некорректный код ответа");
        assertTrue(response.body().isBlank(), "Неккоректное тело ответа");
    }

    @Test
    void testGetSubtasksByEpicId() throws IOException, InterruptedException {
        LocalDateTime nowDateTime = LocalDateTime.now();

        Epic epic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(epic);

        var secondEpic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(secondEpic);

        Subtask subtask1 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, secondEpic.getId(), ofMinutes(1), nowDateTime.minusHours(1));
        Subtask subtask2 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, secondEpic.getId(), ofMinutes(2), nowDateTime);
        Subtask subtask3 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, secondEpic.getId(), ofMinutes(20), nowDateTime.plusDays(1));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(String.format("http://localhost:8080/epics/%d/subtasks", secondEpic.getId()));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        String expected = gson.toJson(List.of(subtask1, subtask2, subtask3));
        assertEquals(expected, response.body(), "Неккоректное тело ответа");
    }

    @Test
    void testGetSubtasksByWrongEpicId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create(String.format("http://localhost:8080/epics/%d/subtasks", 3));
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Некорректный код ответа");
        assertTrue(response.body().isBlank(), "Неккоректное тело ответа");
    }
}