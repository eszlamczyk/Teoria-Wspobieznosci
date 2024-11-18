package agh.ernest.lab6;

public class List implements IList {

    private ListElement head;

    public List() {
        this.head = null;
    }

    public boolean contains(Object item) {
        if (item == null) throw new IllegalArgumentException("Null values are not allowed.");

        ListElement current = head;
        ListElement next;

        if (current == null) return false;

        current.lock();
        try {
            while (current != null) {
                if (current.getObject().equals(item)) {
                    return true;
                }

                next = current.getNext();
                if (next != null) {
                    next.lock();
                }
                current.unlock();
                current = next;
            }
        } finally {
            if (current != null) {
                current.unlock();
            }
        }
        return false;
    }

    public boolean add(Object item) {
        if (item == null) throw new IllegalArgumentException("Null values are not allowed.");

        ListElement newNode = new ListElement(item);

        if (head == null) {
            head = newNode;
            return true;
        }

        ListElement current = head;
        current.lock();
        try {
            while (current.getNext() != null) {
                ListElement next = current.getNext();
                next.lock();
                current.unlock();
                current = next;
            }

            current.setNext(newNode);
        } finally {
            current.unlock();
        }
        return true;
    }

    public boolean remove(Object item) {
        if (item == null) throw new IllegalArgumentException("Null values are not allowed.");

        ListElement current = head;
        ListElement prev = null;

        if (current == null) return false;

        current.lock();
        try {
            while (current != null) {
                if (current.getObject().equals(item)) {
                    if (prev != null) {
                        prev.setNext(current.getNext());
                    } else {
                        head = current.getNext();
                    }
                    return true;
                }

                ListElement next = current.getNext();
                if (next != null) {
                    next.lock();
                }
                if (prev != null) {
                    prev.unlock();
                }
                prev = current;
                current = next;
            }
        } finally {
            if (current != null) {
                current.unlock();
            }
            if (prev != null) {
                prev.unlock();
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "List";
    }
}
