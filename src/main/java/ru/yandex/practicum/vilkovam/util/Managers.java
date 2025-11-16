package ru.yandex.practicum.vilkovam.util;

import ru.yandex.practicum.vilkovam.controller.ControllersHolder;
import ru.yandex.practicum.vilkovam.controller.impl.EpicController;
import ru.yandex.practicum.vilkovam.controller.impl.TaskController;
import ru.yandex.practicum.vilkovam.manager.HistoryManager;
import ru.yandex.practicum.vilkovam.manager.IdGenerator;
import ru.yandex.practicum.vilkovam.manager.ItemPriorityStorage;
import ru.yandex.practicum.vilkovam.manager.TaskManager;
import ru.yandex.practicum.vilkovam.manager.impl.IdGeneratorImpl;
import ru.yandex.practicum.vilkovam.manager.impl.InMemoryHistoryManager;
import ru.yandex.practicum.vilkovam.manager.impl.InMemoryTaskManager;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;

import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * @author Andrew Vilkov
 * @created 17.09.2025 - 20:17
 * @project java-kanban
 */
public class Managers {
    public static final int MAX_HISTORY_SIZE = 0;

    private Managers() {
    }

    public static TaskManager getDefault() {
        return getPrioritizedTaskManager();
    }

    public static TaskManager getPrioritizedTaskManager() {
        TreeSet<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        ControllersHolder controllers = getPrioritizedControllers(getDefaultIdGenerator(), prioritizedTasks);
        return new InMemoryTaskManager(prioritizedTasks, getDefaultHistory(), controllers);
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager(MAX_HISTORY_SIZE);
    }

    public static IdGenerator getDefaultIdGenerator() {
        return new IdGeneratorImpl();
    }

    public static ControllersHolder getPrioritizedControllers(IdGenerator idGenerator, SortedSet<Task> prioritizedTasks) {
        var taskController = new TaskController<>(idGenerator, new ItemPriorityStorage<>(prioritizedTasks), Task::new);
        var subtaskController = new TaskController<Subtask>(idGenerator, new ItemPriorityStorage<>(prioritizedTasks), Subtask::new);
        var epicController = new EpicController(idGenerator, Epic::new);
        return new ControllersHolder(taskController, epicController, subtaskController);
    }

    public static ControllersHolder getDefaultControllers(IdGenerator idGenerator) {
        var taskController = new TaskController<>(idGenerator, Task::new);
        var subtaskController = new TaskController<Subtask>(idGenerator, Subtask::new);
        var epicController = new EpicController(idGenerator, Epic::new);
        return new ControllersHolder(taskController, epicController, subtaskController);
    }
}
