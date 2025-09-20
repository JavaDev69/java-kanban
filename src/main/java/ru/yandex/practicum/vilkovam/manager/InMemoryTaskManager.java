package ru.yandex.practicum.vilkovam.manager;

import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.model.TaskStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Andrew Vilkov
 * @created 28.08.2025 - 13:37
 * @project java-kanban
 */
public class InMemoryTaskManager implements TaskManager {
    private static final UnaryOperator<Task> taskMapper = Task::new;
    private static final UnaryOperator<Subtask> subtaskMapper = Subtask::new;
    private static final UnaryOperator<Epic> epicMapper = Epic::new;
    private final IdGenerator idGenerator;

    private final Map<Integer, Task> idToTask = new HashMap<>();
    private final Map<Integer, Epic> idToEpic = new HashMap<>();
    private final Map<Integer, Subtask> idToSubtask = new HashMap<>();
    private final HistoryManager historyManager;

    public InMemoryTaskManager(IdGenerator idGenerator, HistoryManager historyManager) {
        this.idGenerator = idGenerator;
        this.historyManager = historyManager;
    }

    @Override
    public Task createTask(Task task) {
        return putToList(task, taskMapper, idToTask);
    }

    @Override
    public Task getTaskById(Integer id) {
        Task task = idToTask.get(id);
        historyManager.add(task);
        return new Task(task);
    }

    @Override
    public void updateTask(Task task) {
        idToTask.replace(task.getId(), new Task(task));
    }

    @Override
    public void removeTaskById(Integer id) {
        idToTask.remove(id);
    }

    @Override
    public Epic createEpic(Epic epic) {
        return putToList(epic, epicMapper, idToEpic);
    }

    @Override
    public Epic getEpicById(Integer id) {
        Epic epic = idToEpic.get(id);
        historyManager.add(epic);
        return new Epic(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        Epic epicToAdd = new Epic(epic);
        idToEpic.replace(epicToAdd.getId(), epicToAdd);
        updateEpicStatus(epicToAdd);
    }

    @Override
    public void removeEpicById(Integer id) {
        Epic epic = idToEpic.get(id);
        List<Integer> subtaskIds = epic.getSubtaskIds();
        subtaskIds.forEach(idToSubtask::remove);
        idToEpic.remove(id);
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null || subtask.getEpicId() == null || !idToEpic.containsKey(subtask.getEpicId())) {
            return null;
        }
        Subtask savedSubtask = putToList(subtask, subtaskMapper, idToSubtask);
        Epic epicById = idToEpic.get(savedSubtask.getEpicId());
        List<Integer> subtaskIds = epicById.getSubtaskIds();
        subtaskIds.add(savedSubtask.getId());
        return savedSubtask;
    }

    @Override
    public Subtask getSubtaskById(Integer id) {
        Subtask subtask = idToSubtask.get(id);
        historyManager.add(subtask);
        return new Subtask(subtask);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        Epic epic = idToEpic.get(subtask.getEpicId());
        idToSubtask.replace(subtask.getId(), new Subtask(subtask));
        updateEpicStatus(epic);
    }

    @Override
    public void removeSubtaskById(Integer id) {
        Subtask subtask = getSubtaskById(id);
        Epic epic = idToEpic.get((subtask.getEpicId()));
        epic.getSubtaskIds().remove(id);
        idToSubtask.remove(id);
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
    public Collection<Task> getAllTask() {
        return idToTask.values().stream()
                .map(Task::new)
                .toList();
    }

    /**
     * Возвращает коллекцию всех эпиков
     *
     * @return collections from all of {@link Epic}
     */
    @Override
    public Collection<Epic> getAllEpic() {
        return idToEpic.values().stream()
                .map(Epic::new)
                .toList();
    }

    /**
     * Возвращает коллекцию всех подзадач
     *
     * @return collections from all of {@link Subtask}
     */
    @Override
    public Collection<Subtask> getAllSubtask() {
        return idToSubtask.values().stream()
                .map(Subtask::new)
                .toList();
    }

    /**
     * Возвращает коллекцию всех подзадач для определенного эпика по id
     *
     * @param epicId id target epic
     * @return collections from all of {@link Subtask}
     */
    @Override
    public Collection<Subtask> getAllSubtaskByEpicId(Integer epicId) {
        return idToSubtask.values().stream()
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
     * Установка нового id и статуса {@link  TaskStatus#NEW} для переданного объекта {@link Task}/{@link Epic}/{@link Subtask}
     *
     * @param task - target object
     */
    private void setIdAndStatus(Task task) {
        task.setId(idGenerator.nextId());
        task.setStatus(TaskStatus.NEW);
    }

    /**
     * Вспомогательный метод для обновление статуса {@link Epic}
     *
     * @param epic target epic for calculate status
     */
    private void updateEpicStatus(Epic epic) {
        if (epic == null || epic.getId() == null) return;
        Epic targetEpic = idToEpic.get(epic.getId());
        List<Integer> subtaskIds = targetEpic.getSubtaskIds();

        if (subtaskIds.isEmpty() || isAllSubtaskHasStatus(targetEpic, TaskStatus.NEW)) {
            targetEpic.setStatus(TaskStatus.NEW);
        } else if (isAllSubtaskHasStatus(targetEpic, TaskStatus.DONE)) {
            targetEpic.setStatus(TaskStatus.DONE);
        } else {
            targetEpic.setStatus(TaskStatus.IN_PROGRESS);
        }
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
        return idToSubtask.entrySet().stream()
                .filter(e -> subtaskIds.contains(e.getKey()))
                .map(Map.Entry::getValue)
                .allMatch(e -> status.equals(e.getStatus()));
    }

    private <T extends Task> T putToList(T originTask, UnaryOperator<T> mapper, Map<Integer, T> map) {
        if (originTask == null) {
            return null;
        }

        setIdAndStatus(originTask);
        map.put(originTask.getId(), mapper.apply(originTask));
        return originTask;
    }
}
