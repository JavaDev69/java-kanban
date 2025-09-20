package ru.yandex.practicum.vilkovam.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.model.TaskStatus;
import ru.yandex.practicum.vilkovam.util.Managers;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrew Vilkov
 * @created 18.09.2025 - 19:09
 * @project java-kanban
 */
class InMemoryHistoryManagerTest {
    public static final int MAX_HISTORY_SIZE = 10;
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager(MAX_HISTORY_SIZE);
    }

    @Test
    void shouldAddMultipleTasksToHistoryInCorrectOrder() {
        Task task = new Task(1, "Test 1", "Test 1 description");
        Epic epic = new Epic(5, "Test 3", "Test 3 description");
        Subtask subtask = new Subtask(6, 5, "Test 2", "Test 2 description");

        historyManager.add(task);
        historyManager.add(subtask);
        historyManager.add(epic);

        List<Task> expectedTasks = List.of(epic, subtask, task);
        List<Task> actualTasks = historyManager.getHistory();
        assertIterableEquals(expectedTasks, actualTasks, "Задачи не совпадают.");
    }

    @Test
    void shouldAddSingleTaskToHistory() {
        Task firstTask = new Task(1, "Test addNewTask", "Test addNewTask description");
        historyManager.add(firstTask);

        List<Task> actualHistory = historyManager.getHistory();
        assertNotNull(actualHistory, "После добавления задачи, история не должна быть пустой.");
        assertEquals(1, actualHistory.size(), "Количество задач в истории не совпадают.");
        assertEquals(firstTask, actualHistory.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void shouldContainTenTaskAfterAddingElevenTaskToHistory() {
        List<Task> tasks = IntStream.iterate(1, i -> i + 1)
                .limit(11)
                .mapToObj(id -> new Task(id, "Test " + id, "Test description " + id))
                .toList();
        tasks.forEach(historyManager::add);
        List<Task> actualHistory = historyManager.getHistory();

        assertEquals(MAX_HISTORY_SIZE, actualHistory.size(), "Количество задач в истории не совпадают.");
        assertEquals(tasks.get(10), actualHistory.getFirst(), "Первый в списке - последний добавленный");
        assertEquals(tasks.get(1), actualHistory.getLast(), "Последний в списке - второй добавленный");
        assertFalse(actualHistory.contains(tasks.get(0)), "Первый добавленный отсутствует в списке");
    }

    @Test
    void shouldThrowExceptionWhenChangeTaskFromHistory() {
        Task firstTask = new Task(1, "Test addNewTask", "Test addNewTask description");
        historyManager.add(firstTask);
        Task first = historyManager.getHistory().getFirst();

        assertThrows(UnsupportedOperationException.class, () -> first.setId(2), "Задачу нельзя изменить");
    }

    @Test
    void shouldSavePreviousVersionTaskInHistory() {
        TaskManager taskManager = Managers.getDefault();
        Task createdTask = taskManager.createTask(new Task("Task", "Desk"));

        List<Task> history = taskManager.getHistory();
        assertNotNull(history, "TaskManager должен возвращать не null при вызове getHistory");
        assertEquals(0, history.size(), "История должна быть пуста");

        taskManager.getTaskById(createdTask.getId());
        history = taskManager.getHistory();
        assertEquals(1, history.size(), "История не должна быть пуста");
        assertIterableEquals(Collections.singletonList(createdTask), history, "История не совпадает");

        Task changedTask = new Task(createdTask);
        changedTask.setStatus(TaskStatus.DONE);
        taskManager.updateTask(changedTask);
        taskManager.getTaskById(createdTask.getId());
        history = taskManager.getHistory();

        assertEquals(2, history.size(), "История не должна быть пуста");
        assertIterableEquals(List.of(changedTask, createdTask), history, "История не совпадает");
    }
}