package ru.yandex.practicum.vilkovam.util;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vilkovam.manager.HistoryManager;
import ru.yandex.practicum.vilkovam.manager.IdGenerator;
import ru.yandex.practicum.vilkovam.manager.TaskManager;
import ru.yandex.practicum.vilkovam.manager.impl.IdGeneratorImpl;
import ru.yandex.practicum.vilkovam.manager.impl.InMemoryHistoryManager;
import ru.yandex.practicum.vilkovam.manager.impl.InMemoryTaskManager;
import ru.yandex.practicum.vilkovam.model.Task;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Andrew Vilkov
 * @created 19.09.2025 - 19:06
 * @project java-kanban
 */
class ManagersTest {

    @Test
    void getDefaultShouldReturnNonNullTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "getDefault() должен возвращать не null");
        assertInstanceOf(InMemoryTaskManager.class, taskManager, "getDefault() должен возвращать InMemoryTaskManager");
    }

    @Test
    void getDefaultShouldReturnTaskManagerWithIdGenerator() {
        TaskManager taskManager = Managers.getDefault();
        Stream.generate(() -> new Task("Task", "Desk"))
                .limit(15)
                .forEach(taskManager::createTask);

        List<Task> allTask = taskManager.getAllTask();
        Task lastTask = allTask.getLast();

        assertEquals(15, allTask.size(), "Количество задач не соответствует");
        assertEquals(1, allTask.get(0).getId(), "Id не соответсвует для первой записи");
        assertEquals(2, allTask.get(1).getId(), "Id не соответсвует для второй записи");
        assertEquals(15, lastTask.getId(), "Id не соответсвует для 12 записи");
    }

    @Test
    void getDefaultHistoryShouldReturnNonNullHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "getDefaultHistory() должен возвращать не null");
        assertInstanceOf(InMemoryHistoryManager.class, historyManager,
                "getDefaultHistory() должен возвращать InMemoryHistoryManager");
        assertNotNull(historyManager.getHistory(), "getHistory должен возвращать не null");
        assertEquals(0, historyManager.getHistory().size(), "История должна быть пуста");
        historyManager.add(new Task(2, "Task", "Desk"));
        assertEquals(1, historyManager.getHistory().size(), "История не должна быть пуста");

    }

    @Test
    void getDefaultIdGeneratorShouldReturnNonNullIdGenerator() {
        IdGenerator idGenerator = Managers.getDefaultIdGenerator();
        assertNotNull(idGenerator, "getDefaultIdGenerator() должен возвращать не null");
        assertInstanceOf(IdGeneratorImpl.class, idGenerator,
                "getDefaultIdGenerator() должен возвращать IdGeneratorImpl");
        assertEquals(1, idGenerator.nextId(), "Id не соответсвует для первой записи");
    }
}