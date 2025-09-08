package ru.yandex.practicum.vilkovam.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Vilkov
 * @created 28.08.2025 - 10:50
 * @project java-kanban
 */
public class Epic extends Task {
    private final List<Integer> subtaskIds;

    public Epic(String name, String description) {
        super(name, description);
        this.subtaskIds = new ArrayList<>();
    }

    public Epic(Epic epic) {
        super(epic);
        this.subtaskIds = new ArrayList<>(epic.subtaskIds);
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasks.size=" + getSubtaskIds().size() +
                '}';
    }
}
