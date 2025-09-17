package ru.yandex.practicum.vilkovam.manager;

import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;

import java.util.Collection;
import java.util.List;

/**
 * @author Andrew Vilkov
 * @created 17.09.2025 - 10:16
 * @project java-kanban
 */
public interface TaskManager {
    Task createTask(Task task);

    Task getTaskById(Integer id);

    void updateTask(Task task);

    void removeTaskById(Integer id);

    Epic createEpic(Epic epic);

    Epic getEpicById(Integer id);

    void updateEpic(Epic epic);

    void removeEpicById(Integer id);

    Subtask createSubtask(Subtask subtask);

    Subtask getSubtaskById(Integer id);

    void updateSubtask(Subtask subtask);

    void removeSubtaskById(Integer id);

    Collection<Task> getAllTask();

    Collection<Epic> getAllEpic();

    Collection<Subtask> getAllSubtask();

    Collection<Subtask> getAllSubtaskByEpicId(Integer epicId);

    List<Task> getAllTasks();

    List<Task> getHistory();
}
