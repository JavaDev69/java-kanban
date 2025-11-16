package ru.yandex.practicum.vilkovam.controller;

import ru.yandex.practicum.vilkovam.controller.impl.TaskController;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;

/**
 * @author Andrew Vilkov
 * @created 06.11.2025 - 14:41
 * @project java-kanban
 */
public record ControllersHolder(TaskController<Task> taskController,
                                TaskController<Epic> epicController,
                                TaskController<Subtask> subtaskController) {

}
