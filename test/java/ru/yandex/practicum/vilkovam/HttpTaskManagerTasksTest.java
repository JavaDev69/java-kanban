package ru.yandex.practicum.vilkovam;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.model.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
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
class HttpTaskManagerTasksTest extends BaseHttpTests {

    @Test
    void testAddTask() throws IOException, InterruptedException {
        Task task = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION,
                TaskStatus.NEW, Duration.ofMinutes(5), LocalDateTime.now());

        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTask();

        assertNotNull(tasksFromManager, "Задачи не возвращаются");
        assertEquals(1, tasksFromManager.size(), "Некорректное количество задач");
        assertEquals(DEFAULT_NAME, tasksFromManager.getFirst().getName(), "Некорректное имя задачи");
    }

    @Test
    void testGetAllTask() throws IOException, InterruptedException {
        LocalDateTime nowDateTime = LocalDateTime.now();
        var task = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime);
        taskManager.createTask(task);

        var secondTask = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime.plusDays(1));
        taskManager.createTask(secondTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTask();
        String expected = gson.toJson(tasksFromManager);

        assertEquals(expected, response.body(), "Неккоректное тело ответа");
    }

    @Test
    void testDeleteTask() throws IOException, InterruptedException {
        LocalDateTime nowDateTime = LocalDateTime.now();
        var task = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime);
        taskManager.createTask(task);

        var secondTask = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime.plusDays(1));
        taskManager.createTask(secondTask);

        var thirdTask = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime.plusDays(1));
        taskManager.createTask(thirdTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + secondTask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).DELETE().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        List<Task> tasksFromManager = taskManager.getAllTask();

        assertEquals(2, tasksFromManager.size(), "Неккоректное количество задач");
        assertIterableEquals(List.of(task, thirdTask), tasksFromManager, "Задачи не соответствуют");
    }

    @Test
    void testGetTaskById() throws IOException, InterruptedException {
        LocalDateTime nowDateTime = LocalDateTime.now();
        var task = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime);
        taskManager.createTask(task);

        var secondTask = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime.plusDays(1));
        taskManager.createTask(secondTask);

        var thirdTask = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime.plusDays(1));
        taskManager.createTask(thirdTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/" + secondTask.getId());
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task actual = gson.fromJson(response.body(), Task.class);

        assertNotNull(actual, "Задача не возвращается");
        assertEquals(secondTask, actual, "Неккоректное тело ответа");
    }

    @Test
    void testGetTaskByWrongId() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks/2");
        HttpRequest request = HttpRequest.newBuilder().uri(url).GET().build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode(), "Некорректный код ответа");
        assertTrue(response.body().isBlank(), "Неккоректное тело ответа");
    }

    @Test
    void testAddTaskWithOverlapping() throws IOException, InterruptedException {
        LocalDateTime nowDateTime = LocalDateTime.now();
        var task = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime);
        taskManager.createTask(task);

        var secondTask = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(1), nowDateTime.plusMinutes(1));

        String taskJson = gson.toJson(secondTask);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder().uri(url).POST(HttpRequest.BodyPublishers.ofString(taskJson)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(406, response.statusCode(), "Некорректный код ответа");
        assertTrue(response.body().isBlank(), "Неккоректное тело ответа");
    }
}