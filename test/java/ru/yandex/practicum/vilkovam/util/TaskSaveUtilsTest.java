package ru.yandex.practicum.vilkovam.util;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.ItemType;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.model.TaskStatus;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrew Vilkov
 * @created 19.10.2025 - 15:55
 * @project java-kanban
 */
class TaskSaveUtilsTest {

    @Test
    void shouldSerializeTaskSuccess() {
        Task task = new Task(1, "TaskName", "TaskDescription", TaskStatus.NEW);

        String result = TaskSaveUtils.toString(task);
        String expected = "1,TASK,TaskName,NEW,TaskDescription,,,";

        assertEquals(expected, result);
    }

    @Test
    void shouldSerializeEpicSuccess() {
        Epic epic = new Epic(2, "EpicName", "EpicDescription", TaskStatus.IN_PROGRESS, Collections.emptyList());

        String result = TaskSaveUtils.toString(epic);
        String expected = "2,EPIC,EpicName,IN_PROGRESS,EpicDescription,,,";

        assertEquals(expected, result);
    }

    @Test
    void shouldSerializeSubtaskSuccess() {
        Subtask subtask = new Subtask(3, "SubtaskName", "SubtaskDescription", TaskStatus.DONE, 10);

        String result = TaskSaveUtils.toString(subtask);
        String expected = "3,SUBTASK,SubtaskName,DONE,SubtaskDescription,,,10";

        assertEquals(expected, result);
    }

    @Test
    void shouldDeserializeTaskSuccess() {
        String input = "1,TASK,TaskName,NEW,TaskDescription,,,";

        Task task = TaskSaveUtils.fromString(input);

        assertNotNull(task);
        assertEquals(1, task.getId());
        assertEquals("TaskName", task.getName());
        assertEquals("TaskDescription", task.getDescription());
        assertEquals(TaskStatus.NEW, task.getStatus());
        assertEquals(ItemType.TASK, task.getType());
        assertNull(task.getDuration());
        assertNull(task.getStartTime());
        assertInstanceOf(Task.class, task);
    }

    @Test
    void shouldDeserializeEpicSuccess() {
        String input = "2,EPIC,EpicName,IN_PROGRESS,EpicDescription,,,";

        Task task = TaskSaveUtils.fromString(input);

        assertNotNull(task);
        assertInstanceOf(Epic.class, task);
        Epic epic = (Epic) task;

        assertEquals(2, epic.getId());
        assertEquals("EpicName", epic.getName());
        assertEquals("EpicDescription", epic.getDescription());
        assertEquals(TaskStatus.IN_PROGRESS, epic.getStatus());
        assertEquals(ItemType.EPIC, epic.getType());
        assertNull(epic.getDuration());
        assertNull(epic.getStartTime());
        assertIterableEquals(Collections.emptyList(), epic.getSubtaskIds());
    }

    @Test
    void shouldDeserializeSubtaskSuccess() {
        String input = "3,SUBTASK,SubtaskName,DONE,SubtaskDescription,,,10";

        Task task = TaskSaveUtils.fromString(input);

        assertNotNull(task);
        assertInstanceOf(Subtask.class, task);
        Subtask subtask = (Subtask) task;

        assertEquals(3, subtask.getId());
        assertEquals("SubtaskName", subtask.getName());
        assertEquals("SubtaskDescription", subtask.getDescription());
        assertEquals(TaskStatus.DONE, subtask.getStatus());
        assertEquals(10, subtask.getEpicId());
        assertEquals(ItemType.SUBTASK, subtask.getType());
    }

    @Test
    void shouldReturnNullIfLessColumns() {
        String input = "1,TASK,TaskName";

        Task task = TaskSaveUtils.fromString(input);

        assertNull(task);
    }

    @Test
    void shouldThrowExceptionIfInvalidStatus() {
        String input = "1,TASK,TaskName,INVALID_STATUS,Description,,,";

        assertThrows(IllegalArgumentException.class, () -> TaskSaveUtils.fromString(input));
    }

    @Test
    void shouldThrowExceptionIfInvalidType() {
        String input = "1,INVALID_TYPE,TaskName,NEW,Description,,,";

        assertThrows(IllegalArgumentException.class, () -> TaskSaveUtils.fromString(input));
    }
}