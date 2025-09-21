package ru.yandex.practicum.vilkovam.manager;

import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.model.TaskStatus;

import java.util.Collection;
import java.util.List;
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

    public InMemoryTaskManager(IdGenerator idGenerator, HistoryManager historyManager) {
        this.historyManager = historyManager;
        taskController = new TaskController<>(idGenerator, Task::new);
        epicController = new EpicController(idGenerator, Epic::new);
        subTaskController = new TaskController<>(idGenerator, Subtask::new);
    }

    @Override
    public Task createTask(Task task) {
        return taskController.create(task);
    }

    @Override
    public Task getTaskById(Integer id) {
        Task taskById = taskController.getById(id);
        historyManager.add(taskById);
        return taskById;
    }

    @Override
    public void updateTask(Task task) {
        taskController.update(task);
    }

    @Override
    public void removeTaskById(Integer id) {
        taskController.removeById(id);
    }

    @Override
    public Epic createEpic(Epic epic) {
        return epicController.create(epic);
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epicById = epicController.getById(id);
        historyManager.add(epicById);
        return epicById;
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
        subtaskIds.forEach(subTaskController::removeById);
        epicController.removeById(id);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null || subtask.getEpicId() == null || !epicController.existsById(subtask.getEpicId())) {
            return null;
        }

        Subtask savedSubtask = subTaskController.create(subtask);
        Epic epicById = epicController.getById(savedSubtask.getEpicId());
        List<Integer> subtaskIds = epicById.getSubtaskIds();
        subtaskIds.add(savedSubtask.getId());
        epicController.update(epicById);
        return savedSubtask;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = subTaskController.getById(id);
        historyManager.add(subtask);
        return subtask;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || !subTaskController.existsById(subtask.getId())
                || !epicController.existsById(subtask.getEpicId())) {
            return;
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
        updateEpicStatus(epic);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
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

}
