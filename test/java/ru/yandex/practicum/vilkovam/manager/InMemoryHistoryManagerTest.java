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
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrew Vilkov
 * @created 18.09.2025 - 19:09
 * @project java-kanban
 */
class InMemoryHistoryManagerTest {
    public static final int MAX_HISTORY_SIZE = 0;
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
    void shouldEmptyHistoryWhenLastTaskWasDelete() {
        Task task = new Task(1, "Test 1", "Test 1 description");

        historyManager.add(task);
        historyManager.remove(task.getId());

        List<Task> actualTasks = historyManager.getHistory();
        assertEquals(0, actualTasks.size(), "История не пуста");
    }

    @Test
    void shouldContainSingleTaskWhenSecondDelete() {
        Task task = new Task(1, "Test 1", "Test 1 description");
        Task secondTask = new Task(5, "Test 5", "Test 5 description");

        historyManager.add(task);
        historyManager.add(secondTask);
        historyManager.remove(secondTask.getId());

        List<Task> actualTasks = historyManager.getHistory();
        assertEquals(1, actualTasks.size(), "История не пуста");
        assertEquals(task, actualTasks.getLast(), "Задачи не совпадают.");
    }

    @Test
    void shouldContainSingleTaskWhenFirstDelete() {
        Task task = new Task(1, "Test 1", "Test 1 description");
        Task secondTask = new Task(5, "Test 5", "Test 5 description");

        historyManager.add(task);
        historyManager.add(secondTask);
        historyManager.remove(task.getId());

        List<Task> actualTasks = historyManager.getHistory();
        assertEquals(1, actualTasks.size(), "История не пуста");
        assertEquals(secondTask, actualTasks.getLast(), "Задачи не совпадают.");
    }

    @Test
    void shouldContainTwoTaskWhenMiddleTaskDelete() {
        Task task = new Task(1, "Test 1", "Test 1 description");
        Task secondTask = new Task(5, "Test 5", "Test 5 description");
        Task thirdTask = new Task(3, "Test 3", "Test 3 description");

        historyManager.add(task);
        historyManager.add(secondTask);
        historyManager.add(thirdTask);
        historyManager.remove(secondTask.getId());

        List<Task> actualTasks = historyManager.getHistory();
        List<Task> expectedResult = List.of(thirdTask, task);
        assertIterableEquals(expectedResult, actualTasks, "Задачи не совпадают.");
    }

    @Test
    void shouldAddSingleTaskToHistory() {
        Task firstTask = new Task(1, "Test addNewTask", "Test addNewTask description");
        historyManager.add(firstTask);

        List<Task> actualHistory = historyManager.getHistory();
        assertNotNull(actualHistory, "После добавления задачи, история не должна быть null.");
        assertEquals(1, actualHistory.size(), "Количество задач в истории не совпадают.");
        assertEquals(firstTask, actualHistory.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void shouldNotAddTaskToHistoryWithoutTaskId() {
        Task firstTask = new Task("Test addNewTask", "Test addNewTask description");
        historyManager.add(firstTask);

        List<Task> actualHistory = historyManager.getHistory();
        assertNotNull(actualHistory, "После добавления задачи, история не должна быть null.");
        assertEquals(0, actualHistory.size(), "Количество задач в истории не совпадают.");
    }

    @Test
    void shouldContainElevenTaskAfterAddingElevenTaskToHistory() {
        final int taskCount = 11;
        List<Task> tasks = IntStream.iterate(1, i -> i + 1)
                .limit(taskCount)
                .mapToObj(id -> new Task(id, "Test " + id, "Test description " + id))
                .toList();
        tasks.forEach(historyManager::add);
        List<Task> actualHistory = historyManager.getHistory();

        assertEquals(taskCount, actualHistory.size(), "Количество задач в истории не совпадают.");
        assertEquals(tasks.get(taskCount - 1), actualHistory.getFirst(), "Первый в списке - последний добавленный");
        assertEquals(tasks.getFirst(), actualHistory.getLast(), "Последний в списке - второй добавленный");
    }

    @Test
    void shouldContainTenTaskAfterAddingElevenTaskToHistoryWithMaxSizeTen() {
        final int maxTaskCount = 10;
        historyManager = new InMemoryHistoryManager(maxTaskCount);
        final int taskCount = 11;
        List<Task> tasks = IntStream.iterate(1, i -> i + 1)
                .limit(taskCount)
                .mapToObj(id -> new Task(id, "Test " + id, "Test description " + id))
                .toList();
        tasks.forEach(historyManager::add);
        List<Task> actualHistory = historyManager.getHistory();

        assertEquals(maxTaskCount, actualHistory.size(), "Количество задач в истории не совпадают.");
        assertEquals(tasks.get(taskCount - 1), actualHistory.getFirst(), "Первый в списке - последний добавленный");
        assertEquals(tasks.get(1), actualHistory.getLast(), "Последний в списке - второй добавленный");
    }

    @Test
    void shouldThrowExceptionWhenChangeTaskFromHistory() {
        Task firstTask = new Task(1, "Test addNewTask", "Test addNewTask description");
        historyManager.add(firstTask);
        Task first = historyManager.getHistory().getFirst();

        assertThrows(UnsupportedOperationException.class, () -> first.setId(2), "Задачу нельзя изменить");
    }

    @Test
    void shouldNotSavePreviousVersionTaskInHistory() {
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

        assertEquals(1, history.size(), "История не должна быть пуста");
        assertIterableEquals(List.of(changedTask), history, "История не совпадает");
    }
}