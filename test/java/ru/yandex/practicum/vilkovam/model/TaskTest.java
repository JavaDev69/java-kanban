package ru.yandex.practicum.vilkovam.model;

import org.junit.jupiter.api.Test;

import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Andrew Vilkov
 * @created 18.09.2025 - 21:17
 * @project java-kanban
 */
class TaskTest {
    private static final String TO_STRING_FORMAT = "Task{id=%s, name='%s', description='%s', status=%s}";

    @Test
    void settersAndGettersShouldWorkCorrectly() {
        Task task = new Task();

        task.setId(10);
        task.setName("My Task");
        task.setDescription("Task description");
        task.setStatus(TaskStatus.DONE);

        assertEquals(10, task.getId(), "Id не совпадает");
        assertEquals("My Task", task.getName(), "Имя не совпадает.");
        assertEquals("Task description", task.getDescription(), "Описание не совпадает.");
        assertEquals(TaskStatus.DONE, task.getStatus(), "Статус не совпадает");
    }

    @Test
    void constructorWithNameAndDescriptionShouldSetNameAndDescription() {
        Task task = new Task("Test task", "Description");
        assertNull(task.getId(), "Id должен быть null");
        assertEquals("Test task", task.getName(), "Имя не совпадает.");
        assertEquals("Description", task.getDescription(), "Описание не совпадает.");
        assertNull(task.getStatus(), "Статус должен быть null");
    }

    @Test
    void constructorWithIdNameDescriptionShouldSetFields() {
        Task task = new Task(1, "Task1", "Desc1");
        assertEquals(1, task.getId(), "Id не совпадает");
        assertEquals("Task1", task.getName(), "Имя не совпадает.");
        assertEquals("Desc1", task.getDescription(), "Описание не совпадает.");
        assertNull(task.getStatus(), "Статус должен быть null");
    }

    @Test
    void copyConstructorShouldCopyAllFields() {
        Task original = new Task(5, "Original", "Desc");
        original.setStatus(TaskStatus.IN_PROGRESS);

        Task copy = new Task(original);

        assertEquals(original.getId(), copy.getId(), "Id не совпадает");
        assertEquals(original.getName(), copy.getName(), "Имя не совпадает.");
        assertEquals(original.getDescription(), copy.getDescription(), "Описание не совпадает.");
        assertEquals(original.getStatus(), copy.getStatus(), "Статус не совпадает");
        assertNotSame(original, copy, "Ссылки на объект не должны совпадать");
    }

    @Test
    void equalsShouldReturnTrueForTasksWithSameId() {
        Task task1 = new Task(1, "Task1", "Desc1");
        Task task2 = new Task(1, "Task2", "Desc2");

        assertEquals(task1, task2, "Объекты не совпадают");
    }

    @Test
    void equalsShouldReturnFalseForTasksWithDifferentIds() {
        Task task1 = new Task(1, "Task1", "Desc1");
        Task task2 = new Task(2, "Task1", "Desc1");

        assertNotEquals(task1, task2, "Объекты совпадают");
    }

    @Test
    void hashCodeShouldBeSameForTasksWithSameId() {
        Task task1 = new Task(42, "A", "B");
        Task task2 = new Task(42, "C", "D");

        assertEquals(task1.hashCode(), task2.hashCode(), "HashCode не совпадает");
    }

    @Test
    void toStringShouldReturnNonEmptyStringContainingAllFields() {
        Task task = new Task(7, "Name", "Desc");
        task.setStatus(TaskStatus.NEW);

        String str = task.toString();

        assertNotNull(str, "toString не должен возвращать null");
        assertEquals(format(TO_STRING_FORMAT, 7, "Name", "Desc", TaskStatus.NEW), str,
                "Формат должен соответствовать: " + TO_STRING_FORMAT);
    }
}