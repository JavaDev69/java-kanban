package ru.yandex.practicum.vilkovam;

import ru.yandex.practicum.vilkovam.manager.TaskManager;
import ru.yandex.practicum.vilkovam.model.Epic;
import ru.yandex.practicum.vilkovam.model.Subtask;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.model.TaskStatus;
import ru.yandex.practicum.vilkovam.util.Managers;

/**
 * @author Andrew Vilkov
 * @created 28.08.2025 - 10:47
 * @project java-kanban
 */
public class Main {
    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        Task firstTask = new Task("Task 1", "Description 1");
        Task secondTask = new Task("Task 2", "Description 2");

        manager.createTask(firstTask);
        manager.createTask(secondTask);

        Epic firstEpic = new Epic("Big epic", "Description Epic 1");
        Epic secondEpic = new Epic("Small epic", "Description Epic 2");

        manager.createEpic(firstEpic);
        manager.createEpic(secondEpic);

        Subtask firstSubtask = new Subtask("Subtask 1", "Description Subtask 1");
        firstSubtask.setEpicId(firstEpic.getId());
        Subtask secondSubtask = new Subtask("Subtask 2", "Description Subtask 2");
        secondSubtask.setEpicId(firstEpic.getId());
        Subtask thirdSubtask = new Subtask("Subtask 3", "Description Subtask 3");
        thirdSubtask.setEpicId(secondEpic.getId());

        manager.createSubtask(firstSubtask);
        manager.createSubtask(secondSubtask);
        manager.createSubtask(thirdSubtask);

        manager.getAllTasks().forEach(System.out::println);
        System.out.println();
        System.out.println("#".repeat(10));
        System.out.println();


        secondSubtask.setStatus(TaskStatus.IN_PROGRESS);
        manager.updateSubtask(secondSubtask);
        manager.getAllTasks().forEach(System.out::println);

        firstTask.setStatus(TaskStatus.DONE);
        secondSubtask.setStatus(TaskStatus.DONE);
        manager.updateTask(firstTask);
        System.out.println();
        System.out.println("#".repeat(10));
        System.out.println();
        manager.getAllTasks().forEach(System.out::println);

        manager.removeEpicById(firstEpic.getId());

        System.out.println();
        System.out.println("#".repeat(10));
        System.out.println();
        manager.getAllTasks().forEach(System.out::println);

        manager.getTaskById(2);
        manager.getSubtaskById(7);
        manager.getTaskById(2);
        manager.getTaskById(2);
        manager.getEpicById(4);


        thirdSubtask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(thirdSubtask);
        System.out.println();
        System.out.println("#".repeat(10));
        System.out.println();
        manager.getAllTasks().forEach(System.out::println);

        System.out.println();
        System.out.print("#".repeat(10));
        System.out.print(" HISTORY ");
        System.out.println("#".repeat(10));
        manager.getHistory().forEach(System.out::println);
    }
}
