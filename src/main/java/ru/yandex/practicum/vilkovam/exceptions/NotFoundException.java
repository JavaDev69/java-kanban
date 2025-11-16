package ru.yandex.practicum.vilkovam.exceptions;

/**
 * @author Andrew Vilkov
 * @created 16.11.2025 - 14:04
 * @project java-kanban
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
