package ru.yandex.practicum.vilkovam;

import ru.yandex.practicum.vilkovam.manager.TaskManager;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.util.Managers;

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
        taskManager.createSubtask(firstSubtask);
        taskManager.createSubtask(secondSubtask);
        taskManager.createSubtask(thirdSubtask);

        taskManager.getTaskById(secondTask.getId());
        taskManager.getHistory().forEach(System.out::println);
        System.out.println("#".repeat(15));

        taskManager.getTaskById(secondTask.getId());
        taskManager.getHistory().forEach(System.out::println);
        System.out.println("#".repeat(15));

        taskManager.getEpicById(epicWithSubtask.getId());
        taskManager.getHistory().forEach(System.out::println);
        System.out.println("#".repeat(15));

        taskManager.getTaskById(firstTask.getId());
        taskManager.getHistory().forEach(System.out::println);
        System.out.println("#".repeat(15));

        taskManager.getTaskById(secondTask.getId());
        taskManager.getHistory().forEach(System.out::println);
        System.out.println("#".repeat(15));

        taskManager.getSubtaskById(firstSubtask.getId());
        taskManager.getHistory().forEach(System.out::println);
        System.out.println("#".repeat(15));

        taskManager.getSubtaskById(thirdSubtask.getId());
        taskManager.getHistory().forEach(System.out::println);
        System.out.println("#".repeat(15));

        taskManager.getSubtaskById(firstSubtask.getId());
        taskManager.getHistory().forEach(System.out::println);
        System.out.println("#".repeat(15));

        taskManager.removeTaskById(secondTask.getId());
        taskManager.getHistory().forEach(System.out::println);
        System.out.println("#".repeat(15));
        
        taskManager.removeEpicById(epicWithSubtask.getId());
        taskManager.getHistory().forEach(System.out::println);
        System.out.println("#".repeat(15));
    }
}
