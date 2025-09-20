package ru.yandex.practicum.vilkovam.model;

import java.util.Objects;

/**
 * Вспомогательный класс, хранящий объект {@link Task} и запрещающий
 * модификацию этого объекта.
 *
 * @author Andrew Vilkov
 * @created 18.09.2025 - 20:31
 * @project java-kanban
 */
public final class TaskHolder extends Task {
    private final Task task;

    public TaskHolder(Task task) {
        this.task = task;
    }

    @Override
    public Integer getId() {
        return task.getId();
    }

    @Override
    public void setId(int id) {
        throw new UnsupportedOperationException("Изменение объекта запрещено!");
    }

    @Override
    public String getName() {
        return task.getName();
    }

    @Override
    public void setName(String name) {
        throw new UnsupportedOperationException("Изменение объекта запрещено!");
    }

    @Override
    public String getDescription() {
        return task.getDescription();
    }

    @Override
    public void setDescription(String description) {
        throw new UnsupportedOperationException("Изменение объекта запрещено!");
    }

    @Override
    public TaskStatus getStatus() {
        return task.getStatus();
    }

    @Override
    public void setStatus(TaskStatus status) {
        throw new UnsupportedOperationException("Изменение объекта запрещено!");
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TaskHolder t) {
            return Objects.equals(task, t.task);
        }

        if (o instanceof Task t) {
            return task.equals(t);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return task.hashCode();
    }

    @Override
    public String toString() {
        return task.toString();
    }
}
