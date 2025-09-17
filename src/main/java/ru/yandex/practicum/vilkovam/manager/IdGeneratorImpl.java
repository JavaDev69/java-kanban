package ru.yandex.practicum.vilkovam.manager;

/**
 * @author Andrew Vilkov
 * @created 07.09.2025 - 20:01
 * @project java-kanban
 */
public class IdGeneratorImpl implements IdGenerator {
    private int id = 1;

    @Override
    public int nextId() {
        return id++;
    }
}
