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
        if (task == null) {
            return null;
        }
        return putToList(task, taskMapper, idToTask);
    }

    @Override
    public Task getTaskById(Integer id) {
        if (!idToTask.containsKey(id)) return null;
        Task task = idToTask.get(id);
        historyManager.add(task);
        return new Task(task);
    }

    @Override
    public void updateTask(Task task) {
        if (task == null || task.getId() == null || !idToTask.containsKey(task.getId())) return;
        idToTask.replace(task.getId(), new Task(task));
    }

    @Override
    public void removeTaskById(Integer id) {
        if (id == null || !idToTask.containsKey(id)) return;
        idToTask.remove(id);
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null || !epic.getSubtaskIds().stream().allMatch(idToSubtask::containsKey)) {
            return null;
        }
        return putToList(epic, epicMapper, idToEpic);
    }

    @Override
    public Epic getEpicById(Integer id) {
        if (!idToEpic.containsKey(id)) return null;
        Epic epic = idToEpic.get(id);
        historyManager.add(epic);
        return new Epic(epic);
    }

    @Override
    public void updateEpic(Epic epic) {
        if (epic == null || epic.getId() == null || !idToEpic.containsKey(epic.getId()) ||
                !epic.getSubtaskIds().stream().allMatch(idToSubtask::containsKey)) return;
        Epic epicToAdd = new Epic(epic);
        idToEpic.replace(epicToAdd.getId(), epicToAdd);
        updateEpicStatus(epicToAdd);
    }

    @Override
    public void removeEpicById(Integer id) {
        if (id == null || !idToEpic.containsKey(id)) return;
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
        if (!idToSubtask.containsKey(id)) return null;
        Subtask subtask = idToSubtask.get(id);
        historyManager.add(subtask);
        return new Subtask(subtask);
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        if (subtask == null || subtask.getId() == null || subtask.getEpicId() == null
                || !idToSubtask.containsKey(subtask.getId()) || !idToEpic.containsKey(subtask.getEpicId())) return;
        Epic epic = idToEpic.get(subtask.getEpicId());
        idToSubtask.replace(subtask.getId(), new Subtask(subtask));
        updateEpicStatus(epic);
    }

    @Override
    public void removeSubtaskById(Integer id) {
        if (id == null || !idToSubtask.containsKey(id)) return;
        Subtask subtask = idToSubtask.get(id);
        Epic epic = idToEpic.get(subtask.getEpicId());
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
    public List<Task> getAllTask() {
        return idToTask.values().stream()
                .map(taskMapper)
                .toList();
    }

    /**
     * Возвращает коллекцию всех эпиков
     *
     * @return collections from all of {@link Epic}
     */
    @Override
    public List<Epic> getAllEpic() {
        return idToEpic.values().stream()
                .map(epicMapper)
                .toList();
    }

    /**
     * Возвращает коллекцию всех подзадач
     *
     * @return collections from all of {@link Subtask}
     */
    @Override
    public List<Subtask> getAllSubtask() {
        return idToSubtask.values().stream()
                .map(subtaskMapper)
                .toList();
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

    /**
     * Вспомогательный метод для добавения объекта {@link Task} в соответствующую коллекцию
     *
     * @param originTask оригинальный объект
     * @param mapper     вспомогательная функция для получения копии объекта
     * @param map        целевая коллекция
     * @param <T>        тип объекта
     * @return оригинальный объект с установленным id и статусом
     */
    private <T extends Task> T putToList(T originTask, UnaryOperator<T> mapper, Map<Integer, T> map) {
        setIdAndStatus(originTask);
        map.put(originTask.getId(), mapper.apply(originTask));
        return originTask;
    }
}
