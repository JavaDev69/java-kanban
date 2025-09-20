package ru.yandex.practicum.vilkovam.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.util.Managers;

import java.util.List;

/**
 * @author Andrew Vilkov
 * @created 18.09.2025 - 19:11
 * @project java-kanban
 */
class InMemoryTaskManagerTest {
    TaskManager taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = new InMemoryTaskManager(Managers.getDefaultIdGenerator(), Managers.getDefaultHistory());
    }

    @Test
    void createTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        Task createdTask = taskManager.createTask(task);

        final Task savedTask = taskManager.getTaskById(createdTask.getId());

        Assertions.assertNotNull(savedTask, "Задача не найдена.");
        Assertions.assertEquals(task, savedTask, "Задачи не совпадают.");

        final List<Task> tasks = taskManager.getUnifiedTaskList();

        Assertions.assertNotNull(tasks, "Задачи не возвращаются.");
        Assertions.assertEquals(1, tasks.size(), "Неверное количество задач.");
        Assertions.assertEquals(task, tasks.get(0), "Задачи не совпадают.");
    }

    @Test
    void getTaskById() {
    }

    @Test
    void updateTask() {
    }

    @Test
    void removeTaskById() {
    }

    @Test
    void createEpic() {
    }

    @Test
    void getEpicById() {
    }

    @Test
    void updateEpic() {
    }

    @Test
    void removeEpicById() {
    }

    @Test
    void createSubtask() {
    }

    @Test
    void getSubtaskById() {
    }

    @Test
    void updateSubtask() {
    }

    @Test
    void removeSubtaskById() {
    }

    @Test
    void getHistory() {
    }

    @Test
    void getAllTask() {
    }

    @Test
    void getAllEpic() {
    }

    @Test
    void getAllSubtask() {
    }

    @Test
    void getAllSubtaskByEpicId() {
    }

    @Test
    void getUnifiedTaskList() {
    }
}