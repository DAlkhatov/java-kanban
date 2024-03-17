package service;

import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryHistoryManager implements HistoryManager {

    static class Node {
        Task item;
        Node next;
        Node prev;

        private Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    static HashMap<Integer, Node> history = new HashMap<>();
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

    private void removeNode(Node nodeForDel) {
        if (nodeForDel == null)
            return;
        Node prevNode = nodeForDel.prev;
        Node nextNode = nodeForDel.next;
        if ((first == nodeForDel) && (last == nodeForDel)) {
            first = null;
            last = null;
        } else if (first == nodeForDel) {
            first = nextNode;
            nextNode.prev = null;
        } else if (last == nodeForDel) {
            last = prevNode;
            prevNode.next = null;
        }
        if (nodeForDel.prev != null)
            nodeForDel.prev.next = nextNode;
        if (nodeForDel.next != null)
            nodeForDel.next.prev = prevNode;
        history.remove(nodeForDel.item.getId());
    }

    @Override
    public void add(Task task) {
        if (history.containsKey(task.getId())) {
            remove(task.getId());
        }
        Node node = linkLast(task);
        history.put(task.getId(), node);
    }

    @Override
    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node node = first;
        while (node != null) {
            Task task = node.item;
            tasks.add(task);
            node = node.next;
        }
        return tasks;
    }

    @Override
    public void remove(int id) {
        removeNode(history.get(id));
    }
}