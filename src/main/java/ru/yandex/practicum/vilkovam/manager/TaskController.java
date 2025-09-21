package ru.yandex.practicum.vilkovam.manager;

import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.model.TaskStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * @author Andrew Vilkov
 * @created 21.09.2025 - 11:26
 * @project java-kanban
 */
public class TaskController<T extends Task> implements Controller<T> {
    protected final Map<Integer, T> idToItem = new HashMap<>();
    private final IdGenerator idGenerator;
    protected final UnaryOperator<T> mapper;

    public TaskController(IdGenerator idGenerator, UnaryOperator<T> mapper) {
        this.idGenerator = idGenerator;
        this.mapper = mapper;
    }

    @Override
    public T create(T item) {
        if (item == null) return null;
        setIdAndStatus(item);
        T itemToSave = mapper.apply(item);
        idToItem.put(itemToSave.getId(), itemToSave);
        return item;
    }

    @Override
    public T getById(Integer id) {
        if (!existsById(id)) return null;
        T itemById = idToItem.get(id);
        return mapper.apply(itemById);
    }

    @Override
    public void removeById(Integer id) {
        if (id > 0) {
            idToItem.remove(id);
        }
    }

    @Override
    public void update(T item) {
        if (item == null || !existsById(item.getId())) return;
        T itemToUpdate = mapper.apply(item);
        idToItem.replace(itemToUpdate.getId(), itemToUpdate);
    }

    @Override
    public boolean existsById(Integer id) {
        return id > 0 && idToItem.containsKey(id);
    }

    /**
     * Возвращает коллекцию всех задач
     *
     * @return collections from all of {@link Task}
     */
    @Override
    public List<T> getAllItem() {
        return idToItem.values().stream()
                .map(mapper)
                .toList();
    }

    /**
     * Установка нового id и статуса {@link  TaskStatus#NEW} для переданного объекта {@link Task}/{@link Epic}/{@link Subtask}
     *
     * @param task - target object
     */
    protected void setIdAndStatus(T task) {
        task.setId(idGenerator.nextId());
        task.setStatus(TaskStatus.NEW);
    }
}
