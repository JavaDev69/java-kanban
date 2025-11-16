package ru.yandex.practicum.vilkovam.exceptions;

/**
 * @author Andrew Vilkov
 * @created 16.11.2025 - 17:43
 * @project java-kanban
 */
public class BadRequest extends RuntimeException {
    public BadRequest(String message) {
        super(message);
    }
}
