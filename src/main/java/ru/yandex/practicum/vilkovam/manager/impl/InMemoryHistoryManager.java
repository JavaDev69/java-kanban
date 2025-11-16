package ru.yandex.practicum.vilkovam.manager.impl;

import ru.yandex.practicum.vilkovam.manager.HistoryManager;
import ru.yandex.practicum.vilkovam.manager.Node;
import ru.yandex.practicum.vilkovam.model.Task;
import ru.yandex.practicum.vilkovam.model.TaskHolder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Vilkov
 * @created 17.09.2025 - 20:48
 * @project java-kanban
 */
public class InMemoryHistoryManager implements HistoryManager {
    public final int maxHistorySize;

    private final Map<Integer, Node<Task>> historyMap = new HashMap<>();
    private Node<Task> head;
    private Node<Task> tail;

    public InMemoryHistoryManager(int maxHistorySize) {
        this.maxHistorySize = maxHistorySize;
    }

    @Override
    public List<Task> getHistory() {
        if (head == null) {
            return Collections.emptyList();
        }

        List<Task> result = new ArrayList<>();
        Node<Task> node = head;
        while (node != null) {
            result.add(node.getValue());
            node = node.getNext();
        }

        return Collections.unmodifiableList(result);
    }

    @Override
    public void add(Task task) {
        if (task == null || task.getId() == null) return;
        addWithCheckSize(new TaskHolder(task));
    }

    private void addWithCheckSize(Task task) {
        remove(task.getId());
        if (maxHistorySize > 0 && (historyMap.size() > (maxHistorySize - 1))) {
            removeLast();
        }

        var oldHead = head;
        head = new Node<>(null, task, oldHead);
        historyMap.put(task.getId(), head);
        if (oldHead != null) {
            oldHead.setPrev(head);
        }

        if (tail == null) {
            tail = head;
        }
    }

    @Override
    public void remove(int id) {
        if (!historyMap.containsKey(id)) {
            return;
        }

        Node<Task> node = historyMap.remove(id);
        Node<Task> prev = node.getPrev();
        Node<Task> next = node.getNext();

        if (prev == null && next == null) {
            this.tail = null;
            this.head = null;
            return;
        }

        if (prev != null) {
            prev.setNext(next);
            if (next == null) {
                this.tail = prev;
            }
        }

        if (next != null) {
            next.setPrev(prev);
            if (prev == null) {
                this.head = next;
            }
        }
    }

    private void removeLast() {
        if (this.tail == null) return;
        Node<Task> node = this.tail;
        historyMap.remove(node.getValue().getId());

        this.tail = node.getPrev();
        if (this.tail != null) {
            this.tail.setNext(null);
        }
    }
}
