package ru.yandex.practicum.vilkovam.manager;

import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;

import java.util.List;

/**
 * @author Andrew Vilkov
 * @created 17.09.2025 - 20:45
 * @project java-kanban
 */
public interface HistoryManager {
    List<Task> getHistory();

    void add(Task task);

    void add(Subtask subtask);

    void add(Epic epic);
}
