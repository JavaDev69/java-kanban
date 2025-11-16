package ru.yandex.practicum.vilkovam.manager.impl;

import ru.yandex.practicum.vilkovam.controller.ControllersHolder;
import ru.yandex.practicum.vilkovam.controller.impl.TaskController;
import ru.yandex.practicum.vilkovam.exceptions.OverlappingTaskException;
import ru.yandex.practicum.vilkovam.manager.HistoryManager;
import ru.yandex.practicum.vilkovam.manager.IdGenerator;
import ru.yandex.practicum.vilkovam.manager.TaskManager;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.model.TaskStatus;
import ru.yandex.practicum.vilkovam.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Andrew Vilkov
 * @created 28.08.2025 - 13:37
 * @project java-kanban
 */
public class InMemoryTaskManager implements TaskManager {
    private final TaskController<Task> taskController;
    private final TaskController<Epic> epicController;
    private final TaskController<Subtask> subTaskController;
    private final HistoryManager historyManager;
    private final SortedSet<Task> prioritizedTasks;

    public InMemoryTaskManager(IdGenerator idGenerator, HistoryManager historyManager) {
        this.prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        this.historyManager = historyManager;

        ControllersHolder controllers = Managers.getDefaultControllers(idGenerator);
        this.taskController = controllers.taskController();
        this.epicController = controllers.epicController();
        this.subTaskController = controllers.subtaskController();
    }

    public InMemoryTaskManager(SortedSet<Task> prioritizedTasks,
                               HistoryManager historyManager,
                               ControllersHolder controllers) {
        this.prioritizedTasks = prioritizedTasks;
        this.historyManager = historyManager;
        this.taskController = controllers.taskController();
        this.epicController = controllers.epicController();
        this.subTaskController = controllers.subtaskController();
    }

    @Override
    public Task createTask(Task task) {
        if (isOverlapTask(task)) {
            throw new OverlappingTaskException(task);
        }
        return taskController.create(task);
    }

    @Override
    public Optional<Task> getTaskById(Integer id) {
        Task taskById = taskController.getById(id);
        historyManager.add(taskById);
        return Optional.ofNullable(taskById);
    }

    @Override
    public void updateTask(Task task) {
        if (isOverlapTask(task)) {
            throw new OverlappingTaskException(task);
        }
        taskController.update(task);
    }

    @Override
    public void removeTaskById(Integer id) {
        taskController.removeById(id);
        historyManager.remove(id);
    }

    @Override
    public Epic createEpic(Epic epic) {
        return epicController.create(epic);
    }

    @Override
    public Optional<Epic> getEpicById(Integer id) {
        Epic epicById = epicController.getById(id);
        historyManager.add(epicById);
        return Optional.ofNullable(epicById);
    }

    @Override
    public void updateEpic(Epic epic) {
        List<Integer> newSubtaskIds = epic.getSubtaskIds();

        boolean isAllSubtaskBelongToEpic = newSubtaskIds.stream()
                .map(subTaskController::getById)
                .map(Subtask::getEpicId)
                .allMatch(epic.getId()::equals);

        if (isAllSubtaskBelongToEpic) {
            epicController.update(epic);
            updateEpicStatus(epic);
        }
    }

    @Override
    public void removeEpicById(Integer id) {
        Epic epic = epicController.getById(id);
        List<Integer> subtaskIds = epic.getSubtaskIds();
        subtaskIds.forEach(this::removeSubtaskById);
        epicController.removeById(id);
        historyManager.remove(id);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null || subtask.getEpicId() == null || !epicController.existsById(subtask.getEpicId())) {
            return null;
        }
        if (isOverlapTask(subtask)) {
            throw new OverlappingTaskException(subtask);
        }

        Subtask savedSubtask = subTaskController.create(subtask);
        Epic epicById = epicController.getById(savedSubtask.getEpicId());
        List<Integer> subtaskIds = epicById.getSubtaskIds();
        subtaskIds.add(savedSubtask.getId());
        epicController.update(epicById);
        updateEpicStatus(epicById);
        return savedSubtask;
    }

    @Override
    public Optional<Subtask> getSubtaskById(Integer id) {
        Subtask subtask = subTaskController.getById(id);
        historyManager.add(subtask);
        return Optional.ofNullable(subtask);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subTaskController.existsById(subtask.getId())
                || !epicController.existsById(subtask.getEpicId())) {
            return;
        }
        if (isOverlapTask(subtask)) {
            throw new OverlappingTaskException(subtask);
        }

        Subtask savedSubtask = subTaskController.getById(subtask.getId());
        Epic epic = epicController.getById(subtask.getEpicId());
        subTaskController.update(subtask);

        if (!subtask.getEpicId().equals(savedSubtask.getEpicId())) {
            Epic oldEpicForSubtask = epicController.getById(savedSubtask.getEpicId());
            oldEpicForSubtask.getSubtaskIds().remove(subtask.getId());
            epic.getSubtaskIds().add(subtask.getId());
            epicController.update(oldEpicForSubtask);
            epicController.update(epic);
            updateEpicStatus(oldEpicForSubtask);
        }

        updateEpicStatus(epic);
    }

    @Override
    public void removeSubtaskById(Integer id) {
        if (!subTaskController.existsById(id)) return;
        Subtask subtask = subTaskController.getById(id);
        Epic epic = epicController.getById(subtask.getEpicId());
        epic.getSubtaskIds().remove(id);
        epicController.update(epic);
        subTaskController.removeById(id);
        historyManager.remove(id);
        updateEpicStatus(epic);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Task> getPrioritizedTasks() {
        return Collections.unmodifiableSet(prioritizedTasks);
    }

    /**
     * Возвращает коллекцию всех задач
     *
     * @return collections from all of {@link Task}
     */
    @Override
    public List<Task> getAllTask() {
        return taskController.getAllItem();
    }

    /**
     * Возвращает коллекцию всех эпиков
     *
     * @return collections from all of {@link Epic}
     */
    @Override
    public List<Epic> getAllEpic() {
        return epicController.getAllItem();
    }

    /**
     * Возвращает коллекцию всех подзадач
     *
     * @return collections from all of {@link Subtask}
     */
    @Override
    public List<Subtask> getAllSubtask() {
        return subTaskController.getAllItem();
    }

    /**
     * Возвращает коллекцию всех подзадач для определенного эпика по id
     *
     * @param epicId id target epic
     * @return collections from all of {@link Subtask}
     */
    @Override
    public List<Subtask> getAllSubtaskByEpicId(Integer epicId) {
        return getAllSubtask().stream()
                .filter(e -> epicId.equals(e.getEpicId()))
                .toList();
    }

    /**
     * Получение общего списка из объектов {@link Task}/{@link Epic}/{@link Subtask}
     *
     * @return list of all objects(Task/Epic/Subtask)
     */
    @Override
    public List<Task> getUnifiedTaskList() {
        return Stream.of(getAllTask(), getAllEpic(), getAllSubtask())
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    /**
     * Вспомогательный метод для обновление статуса {@link Epic}
     *
     * @param epic target epic for calculate status
     */
    private void updateEpicStatus(Epic epic) {
        if (epic == null || epic.getId() == null || !epicController.existsById(epic.getId())) return;
        Epic targetEpic = epicController.getById(epic.getId());
        List<Integer> subtaskIds = targetEpic.getSubtaskIds();

        if (subtaskIds.isEmpty() || isAllSubtaskHasStatus(targetEpic, TaskStatus.NEW)) {
            targetEpic.setStatus(TaskStatus.NEW);
        } else if (isAllSubtaskHasStatus(targetEpic, TaskStatus.DONE)) {
            targetEpic.setStatus(TaskStatus.DONE);
        } else {
            targetEpic.setStatus(TaskStatus.IN_PROGRESS);
        }
        updateEpicDurationAndEndTime(targetEpic);
        epicController.update(targetEpic);
    }

    /**
     * Вспомогательный метод проверки что все подзадачи эпика {@link Epic} имеют статус {@code status}
     *
     * @param epic   target {@link Epic}
     * @param status target {@link TaskStatus} for check
     * @return result {@code true} or {@code false}
     */
    private boolean isAllSubtaskHasStatus(Epic epic, TaskStatus status) {
        List<Integer> subtaskIds = epic.getSubtaskIds();
        return subTaskController.getAllItem().stream()
                .filter(e -> subtaskIds.contains(e.getId()))
                .allMatch(e -> status.equals(e.getStatus()));
    }

    private void updateEpicDurationAndEndTime(Epic epic) {
        List<Subtask> allSubtaskByEpicId = getAllSubtaskByEpicId(epic.getId());
        if (allSubtaskByEpicId.isEmpty()) return;

        Duration epicDuration = allSubtaskByEpicId.stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        epic.setDuration(epicDuration);

        var epicStartTime = allSubtaskByEpicId.stream()
                .map(Subtask::getStartTime)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);

        epic.setStartTime(epicStartTime);

        var epicEndTime = allSubtaskByEpicId.stream()
                .map(Subtask::getEndTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        epic.setEndTime(epicEndTime);
    }

    private boolean isOverlapTask(Task task) {
        if (prioritizedTasks == null || prioritizedTasks.isEmpty() || task == null) return false;
        return getPrioritizedTasks().stream()
                .filter(e -> !Objects.equals(e.getId(), task.getId()))
                .anyMatch(e -> isOverlapTasks(task, e));
    }

    private static boolean isOverlapTasks(Task t1, Task t2) {
        LocalDateTime startTime1 = t1.getStartTime();
        LocalDateTime endTime1 = t1.getEndTime();

        LocalDateTime startTime2 = t2.getStartTime();
        LocalDateTime endTime2 = t2.getEndTime();

        if (startTime1 == null || endTime1 == null || startTime2 == null || endTime2 == null) return false;

        return startTime2.isAfter(startTime1) && startTime2.isBefore(endTime1)
                || endTime2.isAfter(startTime1) && endTime2.isBefore(endTime1)
                || startTime2.isBefore(startTime1) && endTime2.isAfter(endTime1);
    }
}
