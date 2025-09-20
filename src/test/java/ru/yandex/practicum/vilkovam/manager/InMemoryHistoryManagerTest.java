package ru.yandex.practicum.vilkovam.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;

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
    HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
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

        assertEquals(10, actualHistory.size(), "Количество задач в истории не совпадают.");
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
}