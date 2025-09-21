package ru.yandex.practicum.vilkovam.manager;

import ru.yandex.practicum.vilkovam.model.Task;

import java.util.List;

/**
 * @author Andrew Vilkov
 * @created 21.09.2025 - 11:26
 * @project java-kanban
 */
public interface Controller<T extends Task> {
    T create(T item);

    T getById(Integer id);

    void removeById(Integer id);

    void update(T item);

    boolean existsById(Integer id);

    List<T> getAllItem();
}
