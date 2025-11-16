package ru.yandex.practicum.vilkovam.manager;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.vilkovam.manager.impl.InMemoryTaskManager;
import ru.yandex.practicum.vilkovam.util.Managers;

/**
 * @author Andrew Vilkov
 * @created 18.09.2025 - 19:11
 * @project java-kanban
 */
class InMemoryTaskManagerTest extends TaskManagerTest {

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultIdGenerator(), Managers.getDefaultHistory());
    }
}