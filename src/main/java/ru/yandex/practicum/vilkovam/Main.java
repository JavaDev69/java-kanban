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
 * @created 16.11.2025 - 13:32
 * @project java-kanban
 */
public class Main {
    public static void main(String[] args) {
        HttpTaskServer httpTaskServer = new HttpTaskServer(getPreFilledTaskManager());
        httpTaskServer.start();
    }

    private static TaskManager getPreFilledTaskManager() {
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
        return taskManager;
    }
}
