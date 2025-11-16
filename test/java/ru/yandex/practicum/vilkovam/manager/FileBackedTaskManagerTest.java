package ru.yandex.practicum.vilkovam.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vilkovam.exceptions.ManagerSaveException;
import ru.yandex.practicum.vilkovam.manager.impl.FileBackedTaskManager;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.util.Managers;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrew Vilkov
 * @created 19.10.2025 - 15:45
 * @project java-kanban
 */
class FileBackedTaskManagerTest extends TaskManagerTest {
    Path path;

    @BeforeEach
    void setUp() {
        path = Paths.get("taskListTest.csv");
        taskManager = new FileBackedTaskManager(
                path.toFile(),
                Managers.getDefaultIdGenerator(),
                Managers.getDefaultHistory());

        path.toFile().deleteOnExit();
    }

    @Test
    void shouldSaveToFileWhenTaskAddedSuccess() throws IOException {
        Task task = new Task("TaskName", "TaskDescription");
        taskManager.createTask(task);

        List<String> actual = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<String> expected = List.of(FileBackedTaskManager.CSV_FILE_HEADER, "1,TASK,TaskName,NEW,TaskDescription,,,");
        assertIterableEquals(expected, actual);
    }

    @Test
    void shouldHaveOnlyHeaderInFileWhenAllTaskRemovedSuccess() throws IOException {
        Task task = new Task("TaskName", "TaskDescription");
        taskManager.createTask(task);
        taskManager.removeTaskById(task.getId());

        List<String> actual = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<String> expected = List.of(FileBackedTaskManager.CSV_FILE_HEADER);
        assertIterableEquals(expected, actual);
    }

    @Test
    void shouldLoadTaskFromFileSuccess() {
        Task task = new Task("TaskName", "TaskDescription");
        taskManager.createTask(task);

        taskManager = FileBackedTaskManager.loadFromFile(
                path.toFile(),
                Managers.getDefaultIdGenerator(),
                Managers.getDefaultHistory());

        Optional<Task> savedTask = taskManager.getTaskById(task.getId());

        assertTrue(savedTask.isPresent(), "Задача не найдена.");
        assertEquals(task, savedTask.get(), "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void shouldSaveToFileWhenThreeTaskAddedSuccess() throws IOException {
        Task task = new Task("TaskName", "TaskDescription");
        taskManager.createTask(task);
        Epic epic = new Epic("EpicName", "EpicDescription");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(epic.getId(), "SubtaskName", "SubtaskDescription");
        taskManager.createSubtask(subtask);

        List<String> actual = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<String> expected = List.of(
                FileBackedTaskManager.CSV_FILE_HEADER,
                "1,TASK,TaskName,NEW,TaskDescription,,,",
                "2,EPIC,EpicName,NEW,EpicDescription,,0,",
                "3,SUBTASK,SubtaskName,NEW,SubtaskDescription,,,2");
        assertIterableEquals(expected, actual);
    }

    @Test
    void shouldLoadTaskFromFileThreeTaskAddedSuccess() {
        Task task = new Task("TaskName", "TaskDescription");
        taskManager.createTask(task);
        Epic epic = new Epic("EpicName", "EpicDescription");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(epic.getId(), "SubtaskName", "SubtaskDescription");
        taskManager.createSubtask(subtask);

        taskManager = FileBackedTaskManager.loadFromFile(
                path.toFile(),
                Managers.getDefaultIdGenerator(),
                Managers.getDefaultHistory());

        List<Task> actual = taskManager.getUnifiedTaskList();
        List<Task> expected = List.of(task, epic, subtask);

        assertIterableEquals(expected, actual);
    }

    @Test
    void shouldLoadFromEmptyFileSuccess() throws IOException {
        Path emptyTmpFile = Files.createTempFile("tmp", ".txt");

        taskManager = FileBackedTaskManager.loadFromFile(
                emptyTmpFile.toFile(),
                Managers.getDefaultIdGenerator(),
                Managers.getDefaultHistory());

        List<Task> actual = taskManager.getUnifiedTaskList();

        assertIterableEquals(Collections.emptyList(), actual);
    }

    @Test
    void shouldDeleteFromFileWhenTaskRemovedSuccess() throws IOException {
        Task task = new Task("TaskName", "TaskDescription");
        taskManager.createTask(task);
        Epic epic = new Epic("EpicName", "EpicDescription");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(epic.getId(), "SubtaskName", "SubtaskDescription");
        taskManager.createSubtask(subtask);

        taskManager.removeTaskById(task.getId());

        List<String> actual = Files.readAllLines(path, StandardCharsets.UTF_8);
        List<String> expected = List.of(
                FileBackedTaskManager.CSV_FILE_HEADER,
                "2,EPIC,EpicName,NEW,EpicDescription,,0,",
                "3,SUBTASK,SubtaskName,NEW,SubtaskDescription,,,2");
        assertIterableEquals(expected, actual);
    }

    @Test
    void shouldThrowExceptionWhenFileNotExist() {
        File file = new File("wrong.file");
        IdGenerator idGenerator = Managers.getDefaultIdGenerator();
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertThrows(ManagerSaveException.class,
                () -> FileBackedTaskManager.loadFromFile(file, idGenerator, historyManager), "Ошибка не соответствует");
    }

    @Test
    void shouldThrowExceptionWhenFileWithWrongContent() throws IOException {
        Path tempFile = Files.createTempFile("tmp", "txt");
        Files.writeString(tempFile, "wrong content\nwrong data");

        File file = tempFile.toFile();
        IdGenerator idGenerator = Managers.getDefaultIdGenerator();
        HistoryManager historyManager = Managers.getDefaultHistory();

        assertThrows(ManagerSaveException.class,
                () -> FileBackedTaskManager.loadFromFile(file, idGenerator, historyManager), "Ошибка не соответствует");
    }
}