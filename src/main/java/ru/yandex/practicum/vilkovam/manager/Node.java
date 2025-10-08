package ru.yandex.practicum.vilkovam.manager;

import ru.yandex.practicum.vilkovam.model.Task;

/**
 * @author Andrew Vilkov
 * @created 07.10.2025 - 20:31
 * @project java-kanban
 */
public class Node<T extends Task> {
    private Node<T> next;
    private final T value;
    private Node<T> prev;

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public T getValue() {
        return value;
    }

    public Node(Node<T> prev, T value, Node<T> next) {
        this.next = next;
        this.value = value;
        this.prev = prev;
    }
}
