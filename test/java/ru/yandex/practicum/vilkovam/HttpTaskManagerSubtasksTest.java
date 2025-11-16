package ru.yandex.practicum.vilkovam;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

import static java.time.Duration.ofDays;
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
class HttpTaskManagerSubtasksTest extends BaseHttpTests {

    @Test
    void testAddSubtasks() throws IOException, InterruptedException {
        LocalDateTime nowDateTime = LocalDateTime.now();

        Epic epic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofMinutes(1), nowDateTime.minusHours(1));

        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Subtask> subtaskFromManager = taskManager.getAllSubtask();
        subtask.setId(2);
        assertNotNull(subtaskFromManager, "Subtask не возвращаются");
        assertEquals(1, subtaskFromManager.size(), "Некорректное количество Subtask");
        assertEquals(subtask, subtaskFromManager.getFirst(), "Некорректное имя Subtask");
    }

    @Test
    void testGetAllSubtasks() throws IOException, InterruptedException {
        LocalDateTime nowDateTime = LocalDateTime.now();

        Epic epic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofMinutes(1), nowDateTime.minusHours(1));
        Subtask subtask2 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofMinutes(2), nowDateTime);
        Subtask subtask3 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofMinutes(20), nowDateTime.plusDays(1));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> subtaskFromManager = taskManager.getAllSubtask();
        String expected = gson.toJson(subtaskFromManager);

        assertEquals(expected, response.body(), "Неккоректное тело ответа");
    }

    @Test
    void testDeleteSubtask() throws IOException, InterruptedException {
        LocalDateTime nowDateTime = LocalDateTime.now();

        Epic epic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofMinutes(1), nowDateTime.minusHours(1));
        Subtask subtask2 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofMinutes(2), nowDateTime);
        Subtask subtask3 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofMinutes(20), nowDateTime.plusDays(1));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask2.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Subtask> subtaskFromManager = taskManager.getAllSubtask();

        assertEquals(2, subtaskFromManager.size(), "Неккоректное количество Subtask");
        assertIterableEquals(List.of(subtask1, subtask3), subtaskFromManager, "Subtask не соответствуют");
    }

    @Test
    void testGetSubtaskById() throws IOException, InterruptedException {
        LocalDateTime nowDateTime = LocalDateTime.now();

        Epic epic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofMinutes(1), nowDateTime.minusHours(1));
        Subtask subtask2 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofMinutes(2), nowDateTime);
        Subtask subtask3 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofMinutes(20), nowDateTime.plusDays(1));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/" + subtask2.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Subtask actual = gson.fromJson(response.body(), Subtask.class);

        assertNotNull(actual, "Subtask не возвращается");
        assertEquals(subtask2, actual, "Неккоректное тело ответа");
    }

    @Test
    void testGetSubtaskByWrongId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Некорректный код ответа");
        assertTrue(response.body().isBlank(), "Неккоректное тело ответа");
    }

    @Test
    void testAddSubtaskWithOverlapping() throws IOException, InterruptedException {
        LocalDateTime nowDateTime = LocalDateTime.now();
        var task = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime);
        taskManager.createTask(task);
        
        Epic epic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofDays(1), nowDateTime.minusHours(1));

        String taskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/subtasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Некорректный код ответа");
        assertTrue(response.body().isBlank(), "Неккоректное тело ответа");
    }
}