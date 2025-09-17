package ru.yandex.practicum.vilkovam.manager;

import ru.yandex.practicum.vilkovam.model.Task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Andrew Vilkov
 * @created 17.09.2025 - 20:48
 * @project java-kanban
 */
public class InMemoryHistoryManager implements HistoryManager {
    private List<Task> history = new ArrayList<>();

    @Override
    public List<Task> getHistory() {
        return Collections.unmodifiableList(history);
    }

    @Override
    public void add(Task task) {
        if (history.size() > 9) {
            history.removeFirst();
        }
        history.add(task);
    }
}
