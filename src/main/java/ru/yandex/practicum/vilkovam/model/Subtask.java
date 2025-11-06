package ru.yandex.practicum.vilkovam.model;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Andrew Vilkov
 * @created 28.08.2025 - 10:50
 * @project java-kanban
 */
public class Subtask extends Task {
    private Integer epicId;

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(Subtask subtask) {
        super(subtask);
        this.epicId = subtask.getEpicId();
    }

    public Subtask(int id, Integer epicId, String name, String description) {
        this(id, name, description, null, epicId);
    }

    public Subtask(Integer epicId, String name, String description) {
        this(null, name, description, null, epicId);
    }

    public Subtask(Integer id, String name, String description, TaskStatus status, Integer epicId) {
        this(id, name, description, status, epicId, null, null);
    }

    public Subtask(Integer id,
                   String name,
                   String description,
                   TaskStatus status,
                   Integer epicId,
                   Duration duration,
                   LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
        this.epicId = epicId;
    }

    @Override
    public ItemType getType() {
        return ItemType.SUBTASK;
    }

    public Integer getEpicId() {
        return epicId;
    }

    public void setEpicId(Integer epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", epicId=" + epicId +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                ", duration='" + getDuration() + '\'' +
                ", startTime='" + getStartTime() + '\'' +
                '}';
    }
}
