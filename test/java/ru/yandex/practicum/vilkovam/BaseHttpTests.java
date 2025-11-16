package ru.yandex.practicum.vilkovam;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.vilkovam.adapters.DurationAdapter;
import ru.yandex.practicum.vilkovam.adapters.LocalDateTimeAdapter;
import ru.yandex.practicum.vilkovam.manager.TaskManager;
import ru.yandex.practicum.vilkovam.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Andrew Vilkov
 * @created 16.11.2025 - 19:00
 * @project java-kanban
 */
public abstract class BaseHttpTests {
    protected static final String DEFAULT_NAME = "Created name";
    protected static final String DEFAULT_DESCRIPTION = "Created description";
    HttpTaskServer server;
    TaskManager taskManager;
    Gson gson;

    @BeforeEach
    void setUp() {
        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
        taskManager = Managers.getPrioritizedTaskManager();
        server = new HttpTaskServer(taskManager);
        server.start();
    }

    @AfterEach
    void tearDown() {
        if (server != null) {
            server.stop();
        }
    }

}
