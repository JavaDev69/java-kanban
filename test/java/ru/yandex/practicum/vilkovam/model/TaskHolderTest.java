package ru.yandex.practicum.vilkovam.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * @author Andrew Vilkov
 * @created 20.09.2025 - 16:13
 * @project java-kanban
 */
class TaskHolderTest {
    Task task;
    TaskHolder taskHolder;

    @BeforeEach
    void setUp() {
        task = new Task(5, "Original", "Desc");
        task.setStatus(TaskStatus.IN_PROGRESS);
        taskHolder = new TaskHolder(task);
    }

    @Test
    void getIdShouldReturnTaskId() {
        assertEquals(task.getId(), taskHolder.getId(), "Id не совпадает");
    }

    @Test
    void getNameShouldReturnTaskName() {
        assertEquals(task.getName(), taskHolder.getName(), "Имя не совпадает.");
    }

    @Test
    void getDescriptionShouldReturnTaskDescription() {
        assertEquals(task.getDescription(), taskHolder.getDescription(), "Описание не совпадает.");
    }

    @Test
    void getStatusShouldReturnTaskStatus() {
        assertEquals(task.getStatus(), taskHolder.getStatus(), "Статус не совпадает");
    }

    @Test
    void setIdShouldThrowUnsupportedOperationException() {
        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                () -> taskHolder.setId(100), "Ошибка не соответствует");
        assertEquals("Изменение объекта запрещено!", ex.getMessage(), "Текст ошибки не совпадает");
    }

    @Test
    void setNameShouldThrowUnsupportedOperationException() {
        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                () -> taskHolder.setName("new name"), "Ошибка не соответствует");
        assertEquals("Изменение объекта запрещено!", ex.getMessage(), "Текст ошибки не совпадает");
    }

    @Test
    void setDescriptionShouldThrowUnsupportedOperationException() {
        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                () -> taskHolder.setDescription("new description"), "Ошибка не соответствует");
        assertEquals("Изменение объекта запрещено!", ex.getMessage(), "Текст ошибки не совпадает");
    }

    @Test
    void setStatusShouldThrowUnsupportedOperationException() {
        UnsupportedOperationException ex = assertThrows(UnsupportedOperationException.class,
                () -> taskHolder.setStatus(TaskStatus.DONE), "Ошибка не соответствует");
        assertEquals("Изменение объекта запрещено!", ex.getMessage(), "Текст ошибки не совпадает");
    }

    @Test
    void equalsShouldReturnTrueWhenComparingWithSameTaskHolder() {
        TaskHolder another = new TaskHolder(task);
        assertEquals(taskHolder, another, "Задачи не совпадают.");
    }

    @Test
    void equalsShouldReturnTrueWhenComparingWithSameTask() {
        assertEquals(taskHolder, task, "Задачи не совпадают.");
    }

    @Test
    void equalsShouldReturnFalseForDifferentTask() {
        Task anotherTask = new Task(1, "Task1", "Desc1");
        TaskHolder otherHolder = new TaskHolder(anotherTask);
        assertNotEquals(taskHolder, otherHolder, "Задачи совпадают.");
        assertNotEquals(taskHolder, anotherTask, "Задачи совпадают.");
    }

    @Test
    void hashCodeShouldReturnTaskHashCode() {
        assertEquals(task.hashCode(), taskHolder.hashCode(), "HashCode не совпадает");
    }

    @Test
    void toStringShouldReturnTaskToString() {
        assertEquals(task.toString(), taskHolder.toString(), "toString не совпадает");
    }

}