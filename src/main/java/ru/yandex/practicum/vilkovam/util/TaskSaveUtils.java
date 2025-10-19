package ru.yandex.practicum.vilkovam.util;

import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.ItemType;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.model.TaskStatus;

import java.util.Collections;

/**
 * @author Andrew Vilkov
 * @created 19.10.2025 - 14:13
 * @project java-kanban
 */
public class TaskSaveUtils {
    private static final String DELIMITER = ",";

    private TaskSaveUtils() {
    }

    public static String toString(Task task) {
        if (ItemType.SUBTASK == task.getType()) {
            Subtask subtask = (Subtask) task;
            return String.join(
                    DELIMITER,
                    String.valueOf(subtask.getId()),
                    subtask.getType().name(),
                    subtask.getName(),
                    task.getStatus().name(),
                    subtask.getDescription(),
                    String.valueOf(subtask.getEpicId()));
        }

        return String.join(
                DELIMITER,
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                "");
    }

    public static Task fromString(String value) {
        String[] column = value.split(DELIMITER, -1);
        if (column.length != 6) return null;
        Integer id = Integer.valueOf(column[0]);
        String name = column[2];
        TaskStatus status = TaskStatus.valueOf(column[3]);
        String desc = column[4];
        String epicId = column[5];
        return switch (ItemType.valueOf(column[1])) {
            case TASK -> new Task(id, name, desc, status);
            case EPIC -> new Epic(id, name, desc, status, Collections.emptyList());
            case SUBTASK -> new Subtask(id, name, desc, status, Integer.valueOf(epicId));
        };
    }
}
