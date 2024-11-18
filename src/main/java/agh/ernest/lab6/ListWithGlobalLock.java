package agh.ernest.lab6;

import java.util.concurrent.locks.ReentrantLock;

public class ListWithGlobalLock implements IList {

    private ListElement head;

    ReentrantLock lock = new ReentrantLock();

    public ListWithGlobalLock() {
        this.head = null;
    }

    public boolean contains(Object item) {
        if (item == null) throw new IllegalArgumentException("Null values are not allowed.");

        lock.lock();
        try {
            ListElement current = head;

            if (current == null) {
                return false;
            }

            while (current != null) {
                if (current.getObject().equals(item)) {
                    return true;
                }
                current = current.getNext();
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    public boolean add(Object item) {
        if (item == null) throw new IllegalArgumentException("Null values are not allowed.");

        lock.lock();
        try {
            ListElement newNode = new ListElement(item);

            if (head == null) {
                head = newNode;
                return true;
            }

            ListElement current = head;
            while (current.getNext() != null) {
                current = current.getNext();
            }

            current.setNext(newNode);
        } finally {
            lock.unlock();
        }
        return true;
    }

    public boolean remove(Object item) {
        if (item == null) throw new IllegalArgumentException("Null values are not allowed.");

        lock.lock();
        try {
            ListElement current = head;
            ListElement prev = null;

            if (current == null) {
                return false;
            }

            while (current != null) {
                if (current.getObject().equals(item)) {
                    if (prev != null) {
                        prev.setNext(current.getNext());
                    } else {
                        head = current.getNext();
                    }
                    return true;
                }
                prev = current;
                current = current.getNext();
            }
        } finally {
            lock.unlock();
        }
        return false;
    }

    @Override
    public String toString() {
        return "ListWithGlobalLock";
    }
}
