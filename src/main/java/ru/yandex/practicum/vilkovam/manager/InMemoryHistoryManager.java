package ru.yandex.practicum.vilkovam.manager;

import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.model.TaskHolder;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Andrew Vilkov
 * @created 17.09.2025 - 20:48
 * @project java-kanban
 */
public class InMemoryHistoryManager implements HistoryManager {
    public final int maxHistorySize;
    private final LinkedList<Task> history = new LinkedList<>();

    public InMemoryHistoryManager(int maxHistorySize) {
        this.maxHistorySize = maxHistorySize;
    }

    @Override
    public List<Task> getHistory() {
        return Collections.unmodifiableList(history);
    }

    @Override
    public void add(Task task) {
        if (task == null) return;
        addWithCheckSize(new Task(task));
    }

    @Override
    public void add(Subtask subtask) {
        if (subtask == null) return;
        addWithCheckSize(new Subtask(subtask));
    }

    @Override
    public void add(Epic epic) {
        if (epic == null) return;
        addWithCheckSize(new Epic(epic));
    }

    private void addWithCheckSize(Task task) {
        if (history.size() > (maxHistorySize - 1)) {
            history.removeLast();
        }
        history.addFirst(new TaskHolder(task));
    }
}
