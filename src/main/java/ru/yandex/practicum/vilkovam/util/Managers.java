package ru.yandex.practicum.vilkovam.util;

import ru.yandex.practicum.vilkovam.manager.HistoryManager;
import ru.yandex.practicum.vilkovam.manager.IdGenerator;
import ru.yandex.practicum.vilkovam.manager.IdGeneratorImpl;
import ru.yandex.practicum.vilkovam.manager.InMemoryHistoryManager;
import ru.yandex.practicum.vilkovam.manager.InMemoryTaskManager;
import ru.yandex.practicum.vilkovam.manager.TaskManager;

/**
 * @author Andrew Vilkov
 * @created 17.09.2025 - 20:17
 * @project java-kanban
 */
public class Managers {
    public static final int MAX_HISTORY_SIZE = 10;

    private Managers() {
    }

    public static TaskManager getDefault() {
        return new InMemoryTaskManager(getDefaultIdGenerator(), getDefaultHistory());
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager(MAX_HISTORY_SIZE);
    }

    public static IdGenerator getDefaultIdGenerator() {
        return new IdGeneratorImpl();
    }
}
