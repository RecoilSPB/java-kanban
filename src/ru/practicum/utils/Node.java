package ru.practicum.utils;

import java.util.Objects;

public class Node<E> {
    private Node<E> prev;
    private E value;
    private Node<E> next;

    public Node(Node<E> prev, E e, Node<E> next) {
        this.prev = prev;
        this.value = e;
        this.next = next;
    }

    public Node<E> getPrev() {
        return prev;
    }

    public void setPrev(Node<E> prev) {
        this.prev = prev;
    }

    public E getValue() {
        return value;
    }

    public void setValue(E value) {
        this.value = value;
    }

    public Node<E> getNext() {
        return next;
    }

    public void setNext(Node<E> next) {
        this.next = next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Node<?> node = (Node<?>) o;
        return Objects.equals(next, node.next) &&
                Objects.equals(value, node.value) &&
                Objects.equals(prev, node.prev);
    }

    @Override
    public int hashCode() {
        return Objects.hash(next, value, prev);
    }
}