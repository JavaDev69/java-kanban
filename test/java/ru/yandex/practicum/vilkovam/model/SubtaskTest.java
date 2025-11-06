package ru.yandex.practicum.vilkovam.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Andrew Vilkov
 * @created 20.09.2025 - 15:46
 * @project java-kanban
 */
class SubtaskTest {
    private static final String TO_STRING_FORMAT = "Subtask{id=%d, epicId=%d, name='%s', description='%s', status=%s, duration='%s', startTime='%s'}";

    @Test
    void settersAndGettersShouldWorkCorrectly() {
        Subtask subtask = new Subtask("Test subtask", "Description");

        subtask.setId(10);
        subtask.setEpicId(15);
        subtask.setName("My Subtask");
        subtask.setDescription("Subtask description");
        subtask.setStatus(TaskStatus.DONE);

        assertEquals(10, subtask.getId(), "Id не совпадает");
        assertEquals(15, subtask.getEpicId(), "Epic Id не совпадает");
        assertEquals("My Subtask", subtask.getName(), "Имя не совпадает.");
        assertEquals("Subtask description", subtask.getDescription(), "Описание не совпадает.");
        assertEquals(TaskStatus.DONE, subtask.getStatus(), "Статус не совпадает");
    }

    @Test
    void constructorWithNameAndDescriptionShouldSetNameAndDescription() {
        Subtask subtask = new Subtask("Test subtask", "Description");
        assertNull(subtask.getId(), "Id должен быть null");
        assertNull(subtask.getEpicId(), "Epic Id должен быть null");
        assertEquals("Test subtask", subtask.getName(), "Имя не совпадает.");
        assertEquals("Description", subtask.getDescription(), "Описание не совпадает.");
        assertNull(subtask.getStatus(), "Статус должен быть null");
    }

    @Test
    void constructorWithIdEpicIdNameDescriptionShouldSetFields() {
        Subtask subtask = new Subtask(1, 2, "Subtask1", "Desc1");
        assertEquals(1, subtask.getId(), "Id не совпадает");
        assertEquals(2, subtask.getEpicId(), "Epic Id не совпадает");
        assertEquals("Subtask1", subtask.getName(), "Имя не совпадает.");
        assertEquals("Desc1", subtask.getDescription(), "Описание не совпадает.");
        assertNull(subtask.getStatus(), "Статус должен быть null");
    }

    @Test
    void copyConstructorShouldCopyAllFields() {
        Subtask original = new Subtask(5, 7, "Original", "Desc");
        original.setStatus(TaskStatus.IN_PROGRESS);

        Subtask copy = new Subtask(original);

        assertNotSame(original, copy, "Ссылки на объект не должны совпадать");
        assertEquals(original.getId(), copy.getId(), "Id не совпадает");
        assertEquals(original.getEpicId(), copy.getEpicId(), "Epic Id не совпадает");
        assertEquals(original.getName(), copy.getName(), "Имя не совпадает.");
        assertEquals(original.getDescription(), copy.getDescription(), "Описание не совпадает.");
        assertEquals(original.getStatus(), copy.getStatus(), "Статус не совпадает");
    }

    @Test
    void equalsShouldReturnTrueForSubtasksWithSameId() {
        Subtask subtask1 = new Subtask(1, 2, "Subtask1", "Desc1");
        Subtask subtask2 = new Subtask(1, 3, "Subtask2", "Desc2");

        assertEquals(subtask1, subtask2, "Объекты не совпадают");
    }

    @Test
    void equalsShouldReturnFalseForSubtasksWithDifferentIds() {
        Subtask subtask1 = new Subtask(1, 3, "Subtask1", "Desc1");
        Subtask subtask2 = new Subtask(2, 3, "Subtask1", "Desc1");

        assertNotEquals(subtask1, subtask2, "Объекты совпадают");
    }

    @Test
    void hashCodeShouldBeSameForSubtasksWithSameId() {
        Subtask subtask1 = new Subtask(42, 1, "A", "B");
        Subtask subtask2 = new Subtask(42, 2, "C", "D");

        assertEquals(subtask1.hashCode(), subtask2.hashCode(), "HashCode не совпадает");
    }

    @Test
    void toStringShouldReturnNonEmptyStringContainingAllFields() {
        Subtask subtask = new Subtask(7, 1, "Name", "Desc");
        subtask.setStatus(TaskStatus.NEW);

        String str = subtask.toString();

        assertNotNull(str, "toString не должен возвращать null");
        assertEquals(String.format(TO_STRING_FORMAT, 7, 1, "Name", "Desc", TaskStatus.NEW, null, null), str,
                "Формат должен соответствовать: " + TO_STRING_FORMAT);
    }
}