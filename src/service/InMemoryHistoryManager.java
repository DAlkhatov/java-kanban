package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task item;
        Node next;
        Node prev;

        public Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    HashMap<Integer, Node> history = new HashMap<>();
    Node first;
    Node last;

    public InMemoryHistoryManager() {
    }

    private Node linkLast(Task task) {
        final Node l = last;
        final Node newNode = new Node(l, task, null);
        last = newNode;
        if (l == null)
            first = newNode;
        else
            l.next = newNode;
        return newNode;
    }

    private void removeNode(Node node) {
        if (history.isEmpty() || node == null)
            return;
        Node prevNode = node.prev;
        Node nextNode = node.next;
        if (first == node)
            first = nextNode;
        else if (last == node)
            last = prevNode;
        if (node.prev != null)
            node.prev.next = nextNode;
        if (node.next != null)
            node.next.prev = prevNode;
        history.remove(node.item.getId());
    }

    @Override
    public void add(Task task) {
        Node node = linkLast(task);
        if (history.containsKey(task.getId())) {
            removeNode(node);
        }
        history.put(task.getId(), node);
    }

    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> values = new ArrayList<>();
        for (Node node : history.values()) {
            values.add(node.item);
        }
        return (ArrayList<Task>) values.clone();
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
    }
}