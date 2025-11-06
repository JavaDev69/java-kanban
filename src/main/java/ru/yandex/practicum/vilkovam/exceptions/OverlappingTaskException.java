package ru.yandex.practicum.vilkovam.exceptions;

import ru.yandex.practicum.vilkovam.model.Task;

/**
 * @author Andrew Vilkov
 * @created 19.10.2025 - 12:54
 * @project java-kanban
 */
public class OverlappingTaskException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Overlapping task exception for task: %s";

    public OverlappingTaskException(Task task) {
        super(String.format(DEFAULT_MESSAGE, task));
    }
}
