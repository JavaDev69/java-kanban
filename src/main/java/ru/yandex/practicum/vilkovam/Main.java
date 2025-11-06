package ru.yandex.practicum.vilkovam;

import ru.yandex.practicum.vilkovam.manager.TaskManager;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.util.Managers;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author Andrew Vilkov
 * @created 20.09.2025 - 20:16
 * @project java-kanban
 */
public class Main {

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();

        var firstTask = new Task("Test firstTask", "Test firstTask description");
        var secondTask = new Task("Test secondTask", "Test secondTask description");
        taskManager.createTask(firstTask);
        taskManager.createTask(secondTask);

        var epicWithoutSubtask = new Epic("Test epicWithoutSubtask", "Test createepicWithoutSubtaskEpic description");
        var epicWithSubtask = new Epic("Test epicWitSubtask", "Test epicWitSubtask description");
        taskManager.createEpic(epicWithoutSubtask);
        taskManager.createEpic(epicWithSubtask);

        var firstSubtask = new Subtask(epicWithSubtask.getId(), "Test firstSubtask", "Test firstSubtask description");
        var secondSubtask = new Subtask(epicWithSubtask.getId(), "Test secondSubtask", "Test secondSubtask description");
        var thirdSubtask = new Subtask(epicWithSubtask.getId(), "Test thirdSubtask", "Test thirdSubtask description");
        secondSubtask.setDuration(Duration.ofMinutes(5));
        secondSubtask.setStartTime(LocalDateTime.now());
        firstSubtask.setDuration(Duration.ofMinutes(2));
        firstSubtask.setStartTime(LocalDateTime.now().plusHours(1));
        taskManager.createSubtask(firstSubtask);
        taskManager.createSubtask(secondSubtask);
        taskManager.createSubtask(thirdSubtask);

        taskManager.getTaskById(secondTask.getId());
        printHistory(taskManager);

        taskManager.getTaskById(secondTask.getId());
        printHistory(taskManager);

        taskManager.getEpicById(epicWithSubtask.getId());
        printHistory(taskManager);

        taskManager.getTaskById(firstTask.getId());
        printHistory(taskManager);

        taskManager.getTaskById(secondTask.getId());
        printHistory(taskManager);

        taskManager.getSubtaskById(firstSubtask.getId());
        printHistory(taskManager);

        taskManager.getSubtaskById(thirdSubtask.getId());
        printHistory(taskManager);

        taskManager.getSubtaskById(firstSubtask.getId());
        printHistory(taskManager);

        taskManager.removeTaskById(secondTask.getId());
        printHistory(taskManager);

        taskManager.removeEpicById(epicWithSubtask.getId());
        printHistory(taskManager);
    }

    private static void printHistory(TaskManager taskManager) {
        taskManager.getHistory().forEach(System.out::println);
        System.out.println("#".repeat(15));
    }
}
