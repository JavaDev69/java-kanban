package ru.yandex.practicum.vilkovam.manager;

import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;

import java.util.List;
import java.util.Set;

/**
 * @author Andrew Vilkov
 * @created 17.09.2025 - 10:16
 * @project java-kanban
 */
public interface TaskManager {

    Set<Task> getPrioritizedTasks();

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

    List<Task> getAllTask();

    List<Epic> getAllEpic();

    List<Subtask> getAllSubtask();

    List<Subtask> getAllSubtaskByEpicId(Integer epicId);

    List<Task> getUnifiedTaskList();

    List<Task> getHistory();
}
