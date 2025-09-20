package ru.yandex.practicum.vilkovam.util;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vilkovam.manager.HistoryManager;
import ru.yandex.practicum.vilkovam.manager.IdGenerator;
import ru.yandex.practicum.vilkovam.manager.TaskManager;

/**
 * @author Andrew Vilkov
 * @created 19.09.2025 - 19:06
 * @project java-kanban
 */
class ManagersTest {

    @Test
    void getDefault() {
        TaskManager taskManager = Managers.getDefault();
    }

    @Test
    void getDefaultHistory() {
        HistoryManager historyManager = Managers.getDefaultHistory();

    }

    @Test
    void getDefaultIdGenerator() {
        IdGenerator idGenerator = Managers.getDefaultIdGenerator();
    }
}