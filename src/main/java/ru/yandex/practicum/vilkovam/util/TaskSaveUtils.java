package ru.yandex.practicum.vilkovam.util;

import ru.yandex.practicum.vilkovam.manager.impl.FileBackedTaskManager;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.ItemType;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.model.TaskStatus;

import java.time.Duration;
import java.time.LocalDateTime;
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

        String epicIdToSave = ItemType.SUBTASK == task.getType() ?
                String.valueOf(((Subtask) task).getEpicId()) : "";
        String startTimeToSave = task.getStartTime() != null ? task.getStartTime().toString() : "";
        String durationToSave = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "";
        return String.join(
                DELIMITER,
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                startTimeToSave,
                durationToSave,
                epicIdToSave);
    }

    public static Task fromString(String value) {
        String[] headers = FileBackedTaskManager.CSV_FILE_HEADER.split(DELIMITER);
        String[] column = value.split(DELIMITER, -1);
        if (column.length != headers.length) return null;
        Integer id = Integer.valueOf(column[0]);
        String name = column[2];
        TaskStatus status = TaskStatus.valueOf(column[3]);
        String desc = column[4];
        String startTimeStr = column[5];
        String durationStr = column[6];
        String epicId = column[7];
        LocalDateTime startTime = "".equals(startTimeStr) ? null : LocalDateTime.parse(startTimeStr);
        Duration duration = "".equals(durationStr) ? null : Duration.ofMinutes(Long.parseLong(durationStr));
        return switch (ItemType.valueOf(column[1])) {
            case TASK -> new Task(id, name, desc, status, duration, startTime);
            case EPIC -> new Epic(id, name, desc, status, Collections.emptyList());
            case SUBTASK -> new Subtask(id, name, desc, status, Integer.valueOf(epicId), duration, startTime);
        };
    }
}
