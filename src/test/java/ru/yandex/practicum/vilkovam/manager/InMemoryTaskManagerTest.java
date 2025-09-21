package ru.yandex.practicum.vilkovam.manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.model.TaskStatus;
import ru.yandex.practicum.vilkovam.util.Managers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * @author Andrew Vilkov
 * @created 18.09.2025 - 19:11
 * @project java-kanban
 */
class InMemoryTaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultIdGenerator(), Managers.getDefaultHistory());
    }

    @Test
    void createTaskSuccess() {
        Task task = new Task("Test createTask", "Test createTask description");
        Task createdTask = taskManager.createTask(task);

        assertNotNull(createdTask, "Задача не создана.");

        final Task savedTask = taskManager.getTaskById(createdTask.getId());

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

        final Task savedEpic = taskManager.getEpicById(createdEpic.getId());

        assertNotNull(savedEpic, "Эпик не найдена.");
        assertEquals(epic, savedEpic, "Задачи не совпадают.");

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

        final Subtask savedSubtask = taskManager.getSubtaskById(createdSubtask.getId());

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
        final Task savedTask = taskManager.getTaskById(5);

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

        Task taskById = taskManager.getTaskById(1);

        assertEquals(1, taskById.getId(), "Id не совпадает");
        assertEquals(sourceTask.getName(), taskById.getName(), "Имя не совпадает");
        assertEquals(sourceTask.getDescription(), taskById.getDescription(), "Описание не совпадает");
        assertEquals(TaskStatus.NEW, taskById.getStatus(), "Статус не совпадает");

        Task taskByWrongId = taskManager.getTaskById(task.getId());
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
        Subtask subtaskAfterUpdate = taskManager.getSubtaskById(subtask.getId());
        assertEquals(epic.getId(), subtaskAfterUpdate.getEpicId(), "Subtask нельзя сделать своим же эпиком");
    }

    @Test
    void epicCannotBeItsOwnSubtask() {
        Epic epic = new Epic("Test createSubtask", "Test createSubtask description");
        taskManager.createEpic(epic);

        Epic secondEpic = new Epic("Test createSubtask", "Test createSubtask description");
        secondEpic.getSubtaskIds().add(epic.getId());

        taskManager.createEpic(secondEpic);
        Epic createdEpic = taskManager.getEpicById(secondEpic.getId());
        assertEquals(0, createdEpic.getSubtaskIds().size(), "Epic нельзя добавить в самого себя в виде подзадачи");
    }

    @Test
    void updateTaskSuccess() {
        Task task = new Task("Test createTask", "Test createTask description");
        taskManager.createTask(task);

        task.setStatus(TaskStatus.DONE);
        task.setDescription("new description");
        task.setName("new name");
        taskManager.updateTask(task);

        Task updatedTask = taskManager.getTaskById(task.getId());
        assertEquals(task.getName(), updatedTask.getName(), "Имя задачи не совпадает.");
        assertEquals(task.getDescription(), updatedTask.getDescription(), "Описание задачи не совпадает.");
        assertEquals(task.getId(), updatedTask.getId(), "ID задачи не совпадает.");
        assertEquals(task.getStatus(), updatedTask.getStatus(), "Статус задачи не совпадает.");
    }

    @Test
    void updateEpicSuccess() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);

        epic.setStatus(TaskStatus.DONE);
        epic.setDescription("new description");
        epic.setName("new name");
        taskManager.updateEpic(epic);

        Epic updatedEpic = taskManager.getEpicById(epic.getId());
        assertEquals(epic.getName(), updatedEpic.getName(), "Имя эпика не совпадает.");
        assertEquals(epic.getDescription(), updatedEpic.getDescription(), "Описание эпика не совпадает.");
        assertEquals(epic.getId(), updatedEpic.getId(), "ID эпика не совпадает.");
        assertIterableEquals(epic.getSubtaskIds(), updatedEpic.getSubtaskIds(), "Подзадачи эпика не совпадают");
        assertEquals(TaskStatus.NEW, updatedEpic.getStatus(), "Статус эпика не совпадает.");
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

        Subtask updatedSubtask = taskManager.getSubtaskById(subtask.getId());
        assertEquals(subtask.getName(), updatedSubtask.getName(), "Имя подзадачи не совпадает.");
        assertEquals(subtask.getDescription(), updatedSubtask.getDescription(), "Описание подзадачи не совпадает.");
        assertEquals(subtask.getId(), updatedSubtask.getId(), "ID подзадачи не совпадает.");
        assertEquals(subtask.getEpicId(), updatedSubtask.getEpicId(), "ID эпика подзадачи не совпадает.");
        assertEquals(subtask.getStatus(), updatedSubtask.getStatus(), "Статус подзадачи не совпадает.");
    }

    @Test
    void deleteTaskSuccess() {
        Task task = new Task("Test createTask", "Test createTask description");
        taskManager.createTask(task);
        taskManager.removeTaskById(task.getId());

        List<Task> allTask = taskManager.getAllTask();
        Task taskById = taskManager.getTaskById(task.getId());

        assertEquals(0, allTask.size(), "Задача не удалена");
        assertNull(taskById, "Задача не удалена");
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

        Epic epicById = taskManager.getEpicById(epic.getId());

        assertEquals(0, allEpic.size(), "Epic не удален");
        assertNull(epicById, "Epic не удален");
        assertEquals(0, allSubtask.size(), "Подзадачи Epic'а не удалены");
    }

    @Test
    void deleteSubtaskSuccess() {
        Epic epic = new Epic("Test createEpic", "Test createEpic description");
        taskManager.createEpic(epic);
        Subtask subtask = new Subtask(epic.getId(), "Test createSubtask", "Test createSubtask description");
        taskManager.createSubtask(subtask);
        taskManager.removeSubtaskById(subtask.getId());


        Epic epicById = taskManager.getEpicById(epic.getId());
        List<Subtask> allSubtask = taskManager.getAllSubtask();
        Subtask subtaskById = taskManager.getSubtaskById(subtask.getId());

        assertNull(subtaskById, "Подзадача не удалена");
        assertEquals(0, allSubtask.size(), "Подзадача не удалена");
        assertNotNull(epicById, "Epic не найден");
        assertEquals(0, epicById.getSubtaskIds().size(), "Подзадача у Epic'а не удалена");
    }

}