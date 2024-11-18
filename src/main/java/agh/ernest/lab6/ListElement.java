package agh.ernest.lab6;

import java.util.concurrent.locks.ReentrantLock;

public class ListElement {

    private final Object object;
    private ListElement next;
    private final ReentrantLock lock = new ReentrantLock();

    public ListElement(Object object) {
        this.object = object;
        this.next = null;
    }

    public Object getObject() {
        return object;
    }

    public ListElement getNext() {
        return next;
    }

    public void setNext(ListElement next) {
        this.next = next;
    }

    public void lock() {
        lock.lock();
    }

    public void unlock() {
        lock.unlock();
    }
}

