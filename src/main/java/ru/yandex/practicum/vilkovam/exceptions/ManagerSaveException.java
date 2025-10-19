package ru.yandex.practicum.vilkovam.exceptions;

/**
 * @author Andrew Vilkov
 * @created 19.10.2025 - 12:54
 * @project java-kanban
 */
public class ManagerSaveException extends RuntimeException {
    public ManagerSaveException(String message) {
        super(message);
    }

    public ManagerSaveException(Throwable cause) {
        super(cause);
    }

    public ManagerSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
