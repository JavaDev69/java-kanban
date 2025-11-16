package ru.yandex.practicum.vilkovam.manager;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.model.TaskStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @author Andrew Vilkov
 * @created 06.11.2025 - 18:14
 * @project java-kanban
 */
public abstract class TaskManagerTest {
    protected static final String DEFAULT_NAME = "Created name";
    protected static final String DEFAULT_DESCRIPTION = "Created description";
    TaskManager taskManager;

    @Test
    void createTaskSuccess() {
        Task task = new Task("Test createTask", "Test createTask description");
        Task createdTask = taskManager.createTask(task);

        assertNotNull(createdTask, "Задача не создана.");

        final Task savedTask = taskManager.getTaskById(createdTask.getId()).orElse(null);

        assertNotNull(savedTask, "Задача не найдена.");
        assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getAllTask();

        assertNotNull(tasks, "Задачи не возвращаются.");
        assertEquals(1, tasks.size(), "Неверное количество задач.");
        assertEquals(task, tasks.getFirst(), "Задачи не совпадают.");
    }

    @Test
    void createEpicSuccess() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        Epic createdEpic = taskManager.createEpic(epic);

        assertNotNull(createdEpic, "Эпик не создан.");
        assertEquals(0, createdEpic.getSubtaskIds().size(), "Количество подзадач не совпадает");
        assertEquals("Test createEpic", createdEpic.getName(), "Имя не совпадает");
        assertEquals("Test createEpic description", createdEpic.getDescription(), "Описание не совпадает");
        assertEquals(TaskStatus.NEW, createdEpic.getStatus(), "Статус не совпадает");

        Optional<Epic> savedEpic = taskManager.getEpicById(createdEpic.getId());

        assertTrue(savedEpic.isPresent(), "Эпик не найдена.");
        assertEquals(epic, savedEpic.get(), "Задачи не совпадают.");

        final List<Epic> epics = taskManager.getAllEpic();

        assertNotNull(epics, "Эпики не возвращаются.");
        assertEquals(1, epics.size(), "Неверное количество эпиков.");
        assertEquals(epic, epics.getFirst(), "Эпики не совпадают.");
    }

    @Test
    void createSubtaskSuccess() {
        Epic epic = new Epic("Test createSubtask", "Test createSubtask description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(epic.getId(), "Test createSubtask", "Test createSubtask description");
        Subtask createdSubtask = taskManager.createSubtask(subtask);

        assertNotNull(createdSubtask, "Подзадача не создана.");
        assertEquals(epic.getId(), subtask.getEpicId(), "Epic id не совпадает.");
        assertEquals("Test createSubtask", createdSubtask.getName(), "Имя не совпадает");
        assertEquals("Test createSubtask description", createdSubtask.getDescription(), "Описание не совпадает");
        assertEquals(TaskStatus.NEW, createdSubtask.getStatus(), "Статус не совпадает");

        final Subtask savedSubtask = taskManager.getSubtaskById(createdSubtask.getId()).orElse(null);

        assertNotNull(savedSubtask, "Подзадача не найдена.");
        assertEquals(subtask, savedSubtask, "Подзадачи не совпадают.");

        final List<Subtask> subtasks = taskManager.getAllSubtask();

        assertNotNull(subtasks, "Подзадачи не возвращаются.");
        assertEquals(1, subtasks.size(), "Неверное количество подзадач.");
        assertEquals(subtask, subtasks.getFirst(), "Подзадачи не совпадают.");
    }

    @Test
    void createTaskWithCustomIdSuccess() {
        Task task = new Task(5, "Test createTask", "Test createTask description");
        Task createdTask = taskManager.createTask(task);

        assertNotNull(createdTask, "Задача не создана.");

        assertEquals(1, task.getId(), "ID Задачи не совпадает.");
        assertEquals(task, createdTask, "Задачи не совпадают.");
    }

    @Test
    void shouldReturnNullWhenCallGetWithWrongId() {
        final Task savedTask = taskManager.getTaskById(5).orElse(null);

        assertNull(savedTask, "Задача найдена.");
    }

    @Test
    void savedTaskShouldNotBeModified() {
        Task sourceTask = new Task("Test createTask", "Test createTask description");
        Task task = new Task(sourceTask);
        taskManager.createTask(task);

        task.setStatus(TaskStatus.DONE);
        task.setDescription("New description");
        task.setName("New name");
        task.setId(5);

        Optional<Task> taskById = taskManager.getTaskById(1);

        assertTrue(taskById.isPresent(), "Задача присутствует");
        assertEquals(1, taskById.get().getId(), "Id не совпадает");
        assertEquals(sourceTask.getName(), taskById.get().getName(), "Имя не совпадает");
        assertEquals(sourceTask.getDescription(), taskById.get().getDescription(), "Описание не совпадает");
        assertEquals(TaskStatus.NEW, taskById.get().getStatus(), "Статус не совпадает");

        Task taskByWrongId = taskManager.getTaskById(task.getId()).orElse(null);
        assertNull(taskByWrongId, "Задача найдена.");

    }

    @Test
    void subtaskCanNotBeItsOwnEpic() {
        Epic epic = new Epic("Test createSubtask", "Test createSubtask description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask("Test createSubtask", "Test createSubtask description");
        subtask.setEpicId(epic.getId());
        taskManager.createSubtask(subtask);

        Subtask secondSubtask = new Subtask(1, subtask.getId(), "Test createSubtask1", "Test createSubtask description1");
        Subtask createdSubtask = taskManager.createSubtask(secondSubtask);
        assertNull(createdSubtask, "Subtask нельзя сделать эпиком другого Subtask");

        subtask.setEpicId(subtask.getId());
        taskManager.updateSubtask(subtask);
        Optional<Subtask> subtaskAfterUpdate = taskManager.getSubtaskById(subtask.getId());
        assertTrue(subtaskAfterUpdate.isPresent(), "Subtask присутствует");
        assertEquals(epic.getId(), subtaskAfterUpdate.get().getEpicId(), "Subtask нельзя сделать своим же эпиком");
    }

    @Test
    void epicCannotBeItsOwnSubtask() {
        Epic epic = new Epic("Test createSubtask", "Test createSubtask description");
        taskManager.createEpic(epic);

        Epic secondEpic = new Epic("Test createSubtask", "Test createSubtask description");
        secondEpic.getSubtaskIds().add(epic.getId());

        taskManager.createEpic(secondEpic);
        Optional<Epic> createdEpic = taskManager.getEpicById(secondEpic.getId());

        assertTrue(createdEpic.isPresent(), "Epic присутствует");
        assertEquals(0, createdEpic.get().getSubtaskIds().size(), "Epic нельзя добавить в самого себя в виде подзадачи");
    }

    @Test
    void updateTaskSuccess() {
        Task task = new Task("Test createTask", "Test createTask description");
        taskManager.createTask(task);

        task.setStatus(TaskStatus.DONE);
        task.setDescription("new description");
        task.setName("new name");
        taskManager.updateTask(task);

        Optional<Task> updatedTask = taskManager.getTaskById(task.getId());

        assertTrue(updatedTask.isPresent(), "Задача присутствует");
        assertEquals(task.getName(), updatedTask.get().getName(), "Имя задачи не совпадает.");
        assertEquals(task.getDescription(), updatedTask.get().getDescription(), "Описание задачи не совпадает.");
        assertEquals(task.getId(), updatedTask.get().getId(), "ID задачи не совпадает.");
        assertEquals(task.getStatus(), updatedTask.get().getStatus(), "Статус задачи не совпадает.");
    }

    @Test
    void updateEpicSuccess() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);

        epic.setStatus(TaskStatus.DONE);
        epic.setDescription("new description");
        epic.setName("new name");
        taskManager.updateEpic(epic);

        Optional<Epic> updatedEpic = taskManager.getEpicById(epic.getId());

        assertTrue(updatedEpic.isPresent(), "Epic присутствует");
        assertEquals(epic.getName(), updatedEpic.get().getName(), "Имя эпика не совпадает.");
        assertEquals(epic.getDescription(), updatedEpic.get().getDescription(), "Описание эпика не совпадает.");
        assertEquals(epic.getId(), updatedEpic.get().getId(), "ID эпика не совпадает.");
        assertIterableEquals(epic.getSubtaskIds(), updatedEpic.get().getSubtaskIds(), "Подзадачи эпика не совпадают");
        assertEquals(TaskStatus.NEW, updatedEpic.get().getStatus(), "Статус эпика не совпадает.");
    }

    @Test
    void updateSubtaskSuccess() {
        Epic epic = new Epic("Test createSubtask", "Test createSubtask description");
        taskManager.createEpic(epic);
        Epic secondEpic = new Epic("Test createSubtask", "Test createSubtask description");
        taskManager.createEpic(secondEpic);
        Subtask subtask = new Subtask(epic.getId(), "Test createSubtask", "Test createSubtask description");
        taskManager.createSubtask(subtask);

        subtask.setStatus(TaskStatus.DONE);
        subtask.setDescription("new description");
        subtask.setName("new name");
        subtask.setEpicId(secondEpic.getId());
        taskManager.updateSubtask(subtask);

        Optional<Subtask> updatedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertTrue(updatedSubtask.isPresent(), "Subtask присутствует");
        assertEquals(subtask.getName(), updatedSubtask.get().getName(), "Имя подзадачи не совпадает.");
        assertEquals(subtask.getDescription(), updatedSubtask.get().getDescription(), "Описание подзадачи не совпадает.");
        assertEquals(subtask.getId(), updatedSubtask.get().getId(), "ID подзадачи не совпадает.");
        assertEquals(subtask.getEpicId(), updatedSubtask.get().getEpicId(), "ID эпика подзадачи не совпадает.");
        assertEquals(subtask.getStatus(), updatedSubtask.get().getStatus(), "Статус подзадачи не совпадает.");
    }

    @Test
    void deleteTaskSuccess() {
        Task task = new Task("Test createTask", "Test createTask description");
        taskManager.createTask(task);
        taskManager.removeTaskById(task.getId());

        List<Task> allTask = taskManager.getAllTask();
        Optional<Task> taskById = taskManager.getTaskById(task.getId());

        assertEquals(0, allTask.size(), "Задача не удалена");
        assertTrue(taskById.isEmpty(), "Задача не удалена");
    }

    @Test
    void deleteEpicSuccess() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(epic.getId(), "Test createSubtask", "Test createSubtask description");
        taskManager.createSubtask(subtask);
        taskManager.removeEpicById(epic.getId());

        List<Epic> allEpic = taskManager.getAllEpic();
        List<Subtask> allSubtask = taskManager.getAllSubtask();

        Optional<Epic> epicById = taskManager.getEpicById(epic.getId());

        assertEquals(0, allEpic.size(), "Epic не удален");
        assertTrue(epicById.isEmpty(), "Epic не удален");
        assertEquals(0, allSubtask.size(), "Подзадачи Epic'а не удалены");
    }

    @Test
    void deleteSubtaskSuccess() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(epic.getId(), "Test createSubtask", "Test createSubtask description");
        taskManager.createSubtask(subtask);
        taskManager.removeSubtaskById(subtask.getId());


        Optional<Epic> epicById = taskManager.getEpicById(epic.getId());
        List<Subtask> allSubtask = taskManager.getAllSubtask();
        Optional<Subtask> subtaskById = taskManager.getSubtaskById(subtask.getId());

        assertTrue(subtaskById.isEmpty(), "Подзадача не удалена");
        assertEquals(0, allSubtask.size(), "Подзадача не удалена");
        assertTrue(epicById.isPresent(), "Epic не найден");
        assertEquals(0, epicById.get().getSubtaskIds().size(), "Подзадача у Epic'а не удалена");
    }

    @Test
    void shouldEpicHaveNewStatusWhenAllSubtaskNew() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(createdEpic.getId(), "Test createSubtask1", "Test createSubtask description1");
        Subtask subtask2 = new Subtask(createdEpic.getId(), "Test createSubtask2", "Test createSubtask description2");
        Subtask subtask3 = new Subtask(createdEpic.getId(), "Test createSubtask3", "Test createSubtask description3");
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        Optional<Epic> epicById = taskManager.getEpicById(createdEpic.getId());
        assertTrue(epicById.isPresent(), "Epic is present");
        assertEquals(TaskStatus.NEW, epicById.get().getStatus(), "Статус эпика не совпадает.");
    }

    @Test
    void shouldEpicHaveDoneStatusWhenAllSubtaskDone() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(createdEpic.getId(), "Test createSubtask1", "Test createSubtask description1");
        Subtask subtask2 = new Subtask(createdEpic.getId(), "Test createSubtask2", "Test createSubtask description2");
        Subtask subtask3 = new Subtask(createdEpic.getId(), "Test createSubtask3", "Test createSubtask description3");
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        subtask1.setStatus(TaskStatus.DONE);
        subtask2.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);

        Optional<Epic> epicById = taskManager.getEpicById(createdEpic.getId());
        assertTrue(epicById.isPresent(), "Epic is present");
        assertEquals(TaskStatus.DONE, epicById.get().getStatus(), "Статус эпика не совпадает.");
    }

    @Test
    void shouldEpicHaveInProgressStatusWhenSubtaskNewAndDone() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(createdEpic.getId(), "Test createSubtask1", "Test createSubtask description1");
        Subtask subtask2 = new Subtask(createdEpic.getId(), "Test createSubtask2", "Test createSubtask description2");
        Subtask subtask3 = new Subtask(createdEpic.getId(), "Test createSubtask3", "Test createSubtask description3");
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        subtask1.setStatus(TaskStatus.DONE);
        subtask3.setStatus(TaskStatus.DONE);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask3);

        Optional<Epic> epicById = taskManager.getEpicById(createdEpic.getId());
        assertTrue(epicById.isPresent(), "Epic is present");
        assertEquals(TaskStatus.IN_PROGRESS, epicById.get().getStatus(), "Статус эпика не совпадает.");
    }

    @Test
    void shouldEpicHaveInProgressStatusWhenSubtaskInProgress() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(createdEpic.getId(), "Test createSubtask1", "Test createSubtask description1");
        Subtask subtask2 = new Subtask(createdEpic.getId(), "Test createSubtask2", "Test createSubtask description2");
        Subtask subtask3 = new Subtask(createdEpic.getId(), "Test createSubtask3", "Test createSubtask description3");
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        subtask1.setStatus(TaskStatus.IN_PROGRESS);
        subtask2.setStatus(TaskStatus.IN_PROGRESS);
        subtask3.setStatus(TaskStatus.IN_PROGRESS);
        taskManager.updateSubtask(subtask1);
        taskManager.updateSubtask(subtask2);
        taskManager.updateSubtask(subtask3);

        Optional<Epic> epicById = taskManager.getEpicById(createdEpic.getId());
        assertTrue(epicById.isPresent(), "Epic is present");
        assertEquals(TaskStatus.IN_PROGRESS, epicById.get().getStatus(), "Статус эпика не совпадает.");
    }

    @Test
    void shouldReturnAllSubtaskByEpicId() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        Epic createdEpic = taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask(createdEpic.getId(), "Test createSubtask1", "Test createSubtask description1");
        Subtask subtask2 = new Subtask(createdEpic.getId(), "Test createSubtask2", "Test createSubtask description2");
        Subtask subtask3 = new Subtask(createdEpic.getId(), "Test createSubtask3", "Test createSubtask description3");
        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);
        taskManager.createSubtask(subtask3);

        List<Subtask> allSubtaskByEpicId = taskManager.getAllSubtaskByEpicId(createdEpic.getId());

        assertIterableEquals(List.of(subtask1, subtask2, subtask3), allSubtaskByEpicId, "Подзадачи эпика не совпадают");
    }
}
