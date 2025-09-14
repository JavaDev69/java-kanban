package ru.yandex.practicum.vilkovam.manager;

import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.model.TaskStatus;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Andrew Vilkov
 * @created 28.08.2025 - 13:37
 * @project java-kanban
 */
public class TaskManager {
    private final IdGenerator idGenerator;

    private final Map<Integer, Task> idToTask = new HashMap<>();
    private final Map<Integer, Epic> idToEpic = new HashMap<>();
    private final Map<Integer, Subtask> idToSubtask = new HashMap<>();

    public TaskManager(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    public Task createTask(Task task) {
        if (task == null) {
            return null;
        }

        setIdAndStatus(task);
        idToTask.put(task.getId(), new Task(task));
        return task;
    }

    public Task getTaskById(Integer id) {
        return new Task(idToTask.get(id));
    }

    public void updateTask(Task task) {
        idToTask.replace(task.getId(), new Task(task));
    }

    public void removeTaskById(Integer id) {
        idToTask.remove(id);
    }

    public Epic createEpic(Epic epic) {
        if (epic == null) {
            return null;
        }

        setIdAndStatus(epic);
        idToEpic.put(epic.getId(), new Epic(epic));
        return epic;
    }

    public Epic getEpicById(Integer id) {
        return new Epic(idToEpic.get(id));
    }

    public void updateEpic(Epic epic) {
        Epic epicToAdd = new Epic(epic);
        idToEpic.replace(epicToAdd.getId(), epicToAdd);
        updateEpicStatus(epicToAdd);
    }

    public void removeEpicById(Integer id) {
        Epic epic = getEpicById(id);
        List<Integer> subtaskIds = epic.getSubtaskIds();
        subtaskIds.forEach(this::removeSubtaskById);
        idToEpic.remove(id);
    }

    public Subtask createSubtask(Subtask subtask) {
        if (subtask == null || subtask.getEpicId() == null) {
            return null;
        }

        setIdAndStatus(subtask);
        Epic epicById = getEpicById(subtask.getEpicId());
        epicById.getSubtaskIds().add(subtask.getId());
        updateEpic(epicById);

        idToSubtask.put(subtask.getId(), new Subtask(subtask));
        return subtask;
    }

    public Subtask getSubtaskById(Integer id) {
        return new Subtask(idToSubtask.get(id));
    }

    public void updateSubtask(Subtask subtask) {
        Epic epic = getEpicById(subtask.getEpicId());
        idToSubtask.replace(subtask.getId(), new Subtask(subtask));
        updateEpicStatus(epic);
    }

    public void removeSubtaskById(Integer id) {
        Subtask subtask = getSubtaskById(id);
        Epic epic = getEpicById(subtask.getEpicId());
        epic.getSubtaskIds().remove(id);
        updateEpic(epic);
        idToSubtask.remove(id);
    }

    /**
     * Возвращает коллекцию всех задач
     *
     * @return collections from all of {@link Task}
     */
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
    public List<Task> getAllTasks() {
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
}
