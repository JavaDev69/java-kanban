package ru.yandex.practicum.vilkovam.manager;

import ru.yandex.practicum.vilkovam.model.Epic;

import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * @author Andrew Vilkov
 * @created 21.09.2025 - 11:46
 * @project java-kanban
 */
public class EpicController extends TaskController<Epic> {
    public EpicController(IdGenerator idGenerator, UnaryOperator<Epic> mapper) {
        super(idGenerator, mapper);
    }

    public EpicController(IdGenerator idGenerator, UnaryOperator<Epic> mapper, Map<Integer, Epic> storage, Map<Integer, Epic> idToItem) {
        super(idGenerator, mapper, storage, idToItem);
    }

    @Override
    public Epic create(Epic item) {
        if (item == null) return null;
        setIdAndStatus(item);
        Epic itemToSave = mapper.apply(item);
        itemToSave.getSubtaskIds().clear();
        idToItem.put(itemToSave.getId(), itemToSave);
        return item;
    }

    @Override
    public void update(Epic item) {
        if (item == null || item.getId() == null || !idToItem.containsKey(item.getId())) return;
        Epic epicToAdd = mapper.apply(item);
        idToItem.replace(epicToAdd.getId(), epicToAdd);
    }
}
