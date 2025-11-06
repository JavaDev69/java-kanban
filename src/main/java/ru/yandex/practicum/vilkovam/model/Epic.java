package ru.yandex.practicum.vilkovam.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Andrew Vilkov
 * @created 28.08.2025 - 10:50
 * @project java-kanban
 */
public class Epic extends Task {
    private final List<Integer> subtaskIds = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public Epic(Epic epic) {
        super(epic);
        this.subtaskIds.addAll(epic.subtaskIds);
        this.endTime = epic.endTime;
    }

    public Epic(Integer id, String name, String description, TaskStatus status, List<Integer> subtaskIds) {
        super(id, name, description, status);
        this.subtaskIds.addAll(subtaskIds);
    }

    @Override
    public ItemType getType() {
        return ItemType.EPIC;
    }

    public List<Integer> getSubtaskIds() {
        return subtaskIds;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", subtasks.size=" + getSubtaskIds().size() +
                ", duration='" + getDuration() + '\'' +
                ", startTime='" + getStartTime() + '\'' +
                ", endTime='" + endTime + '\'' +
                '}';
    }
}
