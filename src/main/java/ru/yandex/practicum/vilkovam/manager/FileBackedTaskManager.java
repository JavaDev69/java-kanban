package ru.yandex.practicum.vilkovam.manager;

import ru.yandex.practicum.vilkovam.exceptions.ManagerSaveException;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.ItemType;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.util.TaskSaveUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;

/**
 * @author Andrew Vilkov
 * @created 19.10.2025 - 12:39
 * @project java-kanban
 */
public class FileBackedTaskManager extends InMemoryTaskManager {
    public static final String CSV_FILE_HEADER = "id,type,name,status,description,epic";
    private final File file;

    public FileBackedTaskManager(File file, IdGenerator idGenerator, HistoryManager historyManager) {
        super(idGenerator, historyManager);
        this.file = file;
    }

    private FileBackedTaskManager(File file,
                                  HistoryManager historyManager,
                                  TaskController<Task> taskController,
                                  TaskController<Epic> epicController,
                                  TaskController<Subtask> subTaskController) {
        super(historyManager, taskController, epicController, subTaskController);
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file, IdGenerator idGenerator, HistoryManager historyManager) {
        try {
            List<String> lines = Files.readAllLines(file.toPath(), UTF_8);
            List<Task> allTasks = lines.stream()
                    .skip(1)
                    .map(TaskSaveUtils::fromString)
                    .toList();

            Map<Integer, Task> idToTask = allTasks.stream()
                    .filter(e -> ItemType.TASK.equals(e.getType()))
                    .map(e -> Map.entry(e.getId(), e))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            Map<Integer, Epic> idToEpic = allTasks.stream()
                    .filter(e -> ItemType.EPIC.equals(e.getType()))
                    .map(e -> Map.entry(e.getId(), (Epic) e))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            Map<Integer, Subtask> idToSubtask = allTasks.stream()
                    .filter(e -> ItemType.SUBTASK.equals(e.getType()))
                    .map(e -> Map.entry(e.getId(), (Subtask) e))
                    .filter(e -> e.getValue().getEpicId() == null
                            || idToEpic.containsKey(e.getValue().getEpicId()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            idToSubtask.forEach((k, v) -> {
                if (v.getEpicId() != null) {
                    Epic epic = idToEpic.get(v.getEpicId());
                    epic.getSubtaskIds().add(k);
                }
            });

            OptionalInt maxId = Stream.of(idToTask, idToEpic, idToSubtask)
                    .map(Map::keySet)
                    .flatMap(Set::stream)
                    .mapToInt(Integer::intValue)
                    .max();

            idGenerator.setNextId(maxId.orElse(1));
            
            return new FileBackedTaskManager(
                    file,
                    historyManager,
                    new TaskController<>(idGenerator, Task::new, idToTask),
                    new EpicController(idGenerator, Epic::new, idToEpic),
                    new TaskController<>(idGenerator, Subtask::new, idToSubtask));
        } catch (Exception e) {
            throw new ManagerSaveException("Cannot load tasks from file " + file.getAbsolutePath(), e);
        }
    }

    private void save() {
        try {
            Files.writeString(file.toPath(), CSV_FILE_HEADER + System.lineSeparator(), CREATE, TRUNCATE_EXISTING, WRITE);

            List<String> listToSave = getUnifiedTaskList().stream()
                    .map(TaskSaveUtils::toString)
                    .toList();
            Files.write(file.toPath(), listToSave, UTF_8, WRITE, APPEND);

        } catch (Exception e) {
            throw new ManagerSaveException(e);
        }
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public Task createTask(Task task) {
        Task tmp = super.createTask(task);
        save();
        return tmp;
    }

    @Override
    public void removeTaskById(Integer id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic tmp = super.createEpic(epic);
        save();
        return tmp;
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpicById(Integer id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask tmp = super.createSubtask(subtask);
        save();
        return tmp;
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    @Override
    public void removeSubtaskById(Integer id) {
        super.removeSubtaskById(id);
        save();
    }
}
