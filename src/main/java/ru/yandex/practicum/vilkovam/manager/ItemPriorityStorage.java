package ru.yandex.practicum.vilkovam.manager;

import ru.yandex.practicum.vilkovam.model.Task;

import java.util.HashMap;
import java.util.Set;
import java.util.SortedSet;

/**
 * @author Andrew Vilkov
 * @created 05.11.2025 - 20:38
 * @project java-kanban
 */
public class ItemPriorityStorage<T extends Task> extends HashMap<Integer, T> {
    private final Set<Task> sorted;

    public ItemPriorityStorage(SortedSet<Task> sorted) {
        this.sorted = sorted;
    }

    @Override
    public T remove(Object key) {
        T taskForRemove = super.remove(key);
        if (taskForRemove != null && taskForRemove.getStartTime() != null) {
            sorted.remove(taskForRemove);
        }
        return super.remove(key);
    }

    @Override
    public T put(Integer key, T value) {
        if (value.getStartTime() != null) {
            sorted.add(value);
        }
        return super.put(key, value);
    }

    @Override
    public T replace(Integer key, T value) {
        if (value.getStartTime() != null) {
            sorted.remove(value);
            sorted.add(value);
        }
        return super.replace(key, value);
    }
}
