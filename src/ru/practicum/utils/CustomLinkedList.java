package ru.practicum.utils;

import ru.practicum.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomLinkedList {
    private final Map<Integer, Node<Task>> history = new HashMap<>();
    private Node<Task> first;
    private Node<Task> last;

    public void linkLast(Task task) {
        if (task != null) {
            if (history.containsKey(task.getId())) {
                removeNode(task.getId());
            }

            final Node<Task> l = last;
            final Node<Task> newNode = new Node<>(l, task, null);
            last = newNode;
            if (l == null)
                first = newNode;
            else
                l.setNext(newNode);

            history.put(task.getId(), newNode);
        }
    }

    public void removeNode(int id) {
        Node<Task> x = history.get(id);
        if (x == null) {
            return;
        }
        final Node<Task> next = x.getNext();
        final Node<Task> prev = x.getPrev();

        if (prev == null) {
            first = next;
        } else {
            prev.setNext(next);
            x.setPrev(null);
        }

        if (next == null) {
            last = prev;
        } else {
            next.setPrev(prev);
            x.setNext(null);
        }
        x.setValue(null);
        history.remove(id);
    }

    public List<Task> getTasks() {
        List<Task> historyList = new ArrayList<>();
        Node<Task> item = first;
        while (item != null) {
            historyList.add(item.getValue());
            item = item.getNext();
        }
        return historyList;
    }

    public Map<Integer, Node<Task>> getHashMap() {
        return history;
    }

    public Node<Task> getNode(int id) {
        return history.get(id);
    }
}