package ru.yandex.practicum.vilkovam.model;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Andrew Vilkov
 * @created 20.09.2025 - 16:01
 * @project java-kanban
 */
class EpicTest {
    private static final String TO_STRING_FORMAT = "Epic{id=%s, name='%s', description='%s', status=%s, subtasks.size=%d}";

    @Test
    void settersAndGettersShouldWorkCorrectly() {
        Epic epic = new Epic("Test epic", "Description");

        epic.setId(10);
        epic.setName("My Epic");
        epic.setDescription("Epic description");
        epic.setStatus(TaskStatus.DONE);

        assertEquals(10, epic.getId(), "Id не совпадает");
        assertEquals("My Epic", epic.getName(), "Имя не совпадает.");
        assertEquals("Epic description", epic.getDescription(), "Описание не совпадает.");
        assertEquals(0, epic.getSubtaskIds().size(), "Размер списка подзадач не совпадает");
        assertEquals(TaskStatus.DONE, epic.getStatus(), "Статус не совпадает");
    }

    @Test
    void constructorWithNameAndDescriptionShouldSetNameAndDescription() {
        Epic epic = new Epic("Test epic", "Description");
        assertNull(epic.getId(), "Id должен быть null");
        assertEquals("Test epic", epic.getName(), "Имя не совпадает.");
        assertEquals("Description", epic.getDescription(), "Описание не совпадает.");
        assertEquals(0, epic.getSubtaskIds().size(), "Размер списка подзадач не совпадает");
        assertNull(epic.getStatus(), "Статус должен быть null");
    }

    @Test
    void constructorWithIdNameDescriptionShouldSetFields() {
        Epic epic = new Epic(1, "Epic1", "Desc1");
        assertEquals(1, epic.getId(), "Id не совпадает");
        assertEquals("Epic1", epic.getName(), "Имя не совпадает.");
        assertEquals("Desc1", epic.getDescription(), "Описание не совпадает.");
        assertEquals(0, epic.getSubtaskIds().size(), "Размер списка подзадач не совпадает");
        assertNull(epic.getStatus(), "Статус должен быть null");
    }

    @Test
    void copyConstructorShouldCopyAllFields() {
        Epic original = new Epic(5, "Original", "Desc");
        original.getSubtaskIds().add(2);
        original.setStatus(TaskStatus.IN_PROGRESS);

        Epic copy = new Epic(original);

        assertNotSame(original, copy, "Ссылки на объект не должны совпадать");
        assertEquals(original.getId(), copy.getId(), "Id не совпадает");
        assertIterableEquals(Collections.singletonList(2), copy.getSubtaskIds(), "Subtask Ids не совпадают");
        assertEquals(original.getName(), copy.getName(), "Имя не совпадает.");
        assertEquals(original.getDescription(), copy.getDescription(), "Описание не совпадает.");
        assertEquals(original.getStatus(), copy.getStatus(), "Статус не совпадает");
    }

    @Test
    void equalsShouldReturnTrueForEpicsWithSameId() {
        Epic epic1 = new Epic(1, "Epic1", "Desc1");
        Epic epic2 = new Epic(1, "Epic2", "Desc2");

        assertEquals(epic1, epic2, "Объекты не совпадают");
    }

    @Test
    void equalsShouldReturnFalseForEpicsWithDifferentIds() {
        Epic epic1 = new Epic(1, "Epic1", "Desc1");
        Epic epic2 = new Epic(2, "Epic1", "Desc1");

        assertNotEquals(epic1, epic2, "Объекты совпадают");
    }

    @Test
    void hashCodeShouldBeSameForEpicsWithSameId() {
        Epic epic1 = new Epic(42, "A", "B");
        Epic epic2 = new Epic(42, "C", "D");

        assertEquals(epic1.hashCode(), epic2.hashCode(), "HashCode не совпадает");
    }

    @Test
    void toStringShouldReturnNonEmptyStringContainingAllFields() {
        Epic epic = new Epic(7, "Name", "Desc");
        epic.setStatus(TaskStatus.NEW);

        String str = epic.toString();

        assertNotNull(str, "toString не должен возвращать null");
        assertEquals(format(TO_STRING_FORMAT, 7, "Name", "Desc", TaskStatus.NEW, 0), str,
                "Формат должен соответствовать: " + TO_STRING_FORMAT);
    }
}