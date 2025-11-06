package ru.yandex.practicum.vilkovam.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vilkovam.exceptions.OverlappingTaskException;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.util.Managers;
import ru.yandex.practicum.vilkovam.util.TaskSaveUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import static java.time.Duration.ofDays;
import static java.time.Duration.ofMinutes;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.yandex.practicum.vilkovam.model.TaskStatus.NEW;

/**
 * @author Andrew Vilkov
 * @created 06.11.2025 - 18:48
 * @project java-kanban
 */
class FileBackedPrioritizedTaskManagerTest extends TaskManagerTest {
    Path path;

    @BeforeEach
    void setUp() {
        path = Paths.get("taskListTest.csv");
        TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        IdGenerator idGenerator = Managers.getDefaultIdGenerator();
        ControllersHolder controllers = Managers.getPrioritizedControllers(idGenerator, prioritizedTasks);
        taskManager = new FileBackedTaskManager(
                path.toFile(),
                prioritizedTasks,
                Managers.getDefaultHistory(),
                controllers);

        path.toFile().deleteOnExit();
    }

    @Test
    void shouldSaveToFileWhenTaskAddedSuccess() throws IOException {
        var task = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), LocalDateTime.now());
        taskManager.createTask(task);

        List<String> actual = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<String> expected = List.of(FileBackedTaskManager.CSV_FILE_HEADER, TaskSaveUtils.toString(task));
        assertIterableEquals(expected, actual);
    }

    @Test
    void shouldLoadTaskFromFileSuccess() {
        var task = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), LocalDateTime.now());
        taskManager.createTask(task);

        taskManager = FileBackedTaskManager.loadFromFile(
                path.toFile(),
                Managers.getDefaultIdGenerator(),
                Managers.getDefaultHistory());

        final Task savedTask = taskManager.getTaskById(task.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void shouldThrowOverlappingTaskExceptionWhenTasksOverlappingEnds() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        var task = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime);
        taskManager.createTask(task);

        var secondTask = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime.plusMinutes(1));
        String expectedMessage = "Overlapping task exception for task: " + secondTask;

        OverlappingTaskException ex = assertThrows(OverlappingTaskException.class,
                () -> taskManager.createTask(secondTask), "Ошибка не соответствует");
        assertEquals(expectedMessage, ex.getMessage(), "Текст ошибки не совпадает");
    }

    @Test
    void shouldThrowOverlappingTaskExceptionWhenTasksOverlappingStarts() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        var task = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime);
        taskManager.createTask(task);

        var secondTask = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime.minusMinutes(4));
        String expectedMessage = "Overlapping task exception for task: " + secondTask;

        OverlappingTaskException ex = assertThrows(OverlappingTaskException.class,
                () -> taskManager.createTask(secondTask), "Ошибка не соответствует");
        assertEquals(expectedMessage, ex.getMessage(), "Текст ошибки не совпадает");
    }

    @Test
    void shouldThrowOverlappingTaskExceptionWhenTasksOverlappingIn() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        var task = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime);
        taskManager.createTask(task);

        var secondTask = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(1), nowDateTime.plusMinutes(1));
        String expectedMessage = "Overlapping task exception for task: " + secondTask;

        OverlappingTaskException ex = assertThrows(OverlappingTaskException.class,
                () -> taskManager.createTask(secondTask), "Ошибка не соответствует");
        assertEquals(expectedMessage, ex.getMessage(), "Текст ошибки не совпадает");
    }

    @Test
    void shouldThrowOverlappingTaskExceptionWhenTaskAndSubtaskOverlapping() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        var task = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime);
        taskManager.createTask(task);

        Epic epic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofDays(1), nowDateTime.minusHours(1));
        String expectedMessage = "Overlapping task exception for task: " + subtask;

        OverlappingTaskException ex = assertThrows(OverlappingTaskException.class,
                () -> taskManager.createSubtask(subtask), "Ошибка не соответствует");
        assertEquals(expectedMessage, ex.getMessage(), "Текст ошибки не совпадает");
    }

    @Test
    void createTaskSuccessWhenNoOverlapping() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        var task = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime);
        taskManager.createTask(task);

        var secondTask = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime.plusDays(1));

        Task createdTask = assertDoesNotThrow(() ->
                taskManager.createTask(secondTask), "Ошибка не должна возникнуть");

        assertNotNull(createdTask, "Задача не создана.");

        final Task savedTask = taskManager.getTaskById(createdTask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(secondTask, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertIterableEquals(List.of(task, secondTask), tasks, "Задачи не совпадают.");
    }

    @Test
    void createSubtaskTaskSuccessWhenNoOverlapping() {
        LocalDateTime nowDateTime = LocalDateTime.now();
        var task = new Task(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, ofMinutes(5), nowDateTime);
        taskManager.createTask(task);

        Epic epic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(epic);

        Subtask subtask = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofMinutes(1), nowDateTime.minusHours(1));

        Subtask createdTask = assertDoesNotThrow(() ->
                taskManager.createSubtask(subtask), "Ошибка не должна возникнуть");

        assertNotNull(createdTask, "Задача не создана.");

        final Subtask savedTask = taskManager.getSubtaskById(createdTask.getId());

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(subtask, savedTask, "Задачи не совпадают.");

        final List<Subtask> tasks = taskManager.getAllSubtask();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertIterableEquals(Collections.singletonList(subtask), tasks, "Задачи не совпадают.");
    }

    @Test
    void shouldCalculateCorrectEndDateAndDurationForEpic() {
        LocalDateTime nowDateTime = LocalDateTime.now();

        Epic epic = new Epic(DEFAULT_NAME, DEFAULT_DESCRIPTION);
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofMinutes(1), nowDateTime.minusHours(1));
        Subtask subtask2 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofMinutes(2), nowDateTime);
        Subtask subtask3 = new Subtask(null, DEFAULT_NAME, DEFAULT_DESCRIPTION, NEW, epic.getId(), ofMinutes(20), nowDateTime.plusDays(1));
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        Epic epicById = taskManager.getEpicById(epic.getId());
        LocalDateTime endTime = epicById.getEndTime();
        Duration duration = epicById.getDuration();

        LocalDateTime expectedEndTime = subtask3.getEndTime();
        Duration expectedDuration = subtask1.getDuration().plus(subtask2.getDuration()).plus(subtask3.getDuration());

        assertEquals(expectedEndTime, endTime, "Время завершения эпика не совпадает.");
        assertEquals(expectedDuration, duration, "Продолжительность эпика не совпадает.");
    }


}
