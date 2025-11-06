package ru.yandex.practicum.vilkovam.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @author Andrew Vilkov
 * @created 28.08.2025 - 10:50
 * @project java-kanban
 */
public class Task {
    private Integer id;
    private String name;
    private String description;
    private TaskStatus status;
    private Duration duration;
    private LocalDateTime startTime;

    public Task() {
    }

    public Task(String name, String description) {
        this(null, name, description);
    }

    public Task(Integer id, String name, String description) {
        this(id, name, description, null);
    }

    public Task(Integer id, String name, String description, TaskStatus status) {
        this(id, name, description, status, null, null);
    }

    public Task(Integer id,
                String name,
                String description,
                TaskStatus status,
                Duration duration,
                LocalDateTime startTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(Task task) {
        this(task.getId(), task.getName(), task.getDescription(), task.getStatus(), task.getDuration(),
                task.getStartTime());
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public ItemType getType() {
        return ItemType.TASK;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        if (startTime == null || duration == null) return null;
        return startTime.plus(duration);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Task task)) return false;
        return Objects.equals(id, task.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration='" + duration + '\'' +
                ", startTime='" + startTime + '\'' +
                '}';
    }
}
