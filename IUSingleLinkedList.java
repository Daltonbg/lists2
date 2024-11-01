
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Single-linked node implementation of IndexedUnsortedList. An Iterator with
 * working remove() method is implemented, but ListIterator is unsupported.
 *
 * @author Dalton Bilau Goncalves - CS 221
 *
 * @param <T> type to store
 */
public class IUSingleLinkedList<T> implements IndexedUnsortedList<T> {

    private Node<T> head, tail;
    private int size;
    private int modCount;

    /**
     * Creates an empty list
     */
    public IUSingleLinkedList() {
        head = tail = null;
        size = 0;
        modCount = 0;
    }

    @Override
    public void addToFront(T element) {
        Node<T> newNode = new Node<T>(element);
        newNode.setNext(head);
        head = newNode;
        if (tail == null) {
            tail = newNode;
        }
        size++;
        modCount++;
    }

    @Override
    public void addToRear(T element) {
        Node<T> newNode = new Node<T>(element);
        if (isEmpty()) {
            head = newNode;
        } else {
            tail.setNext(newNode);
        }
        tail = newNode;
        size++;
        modCount++;
    }

    @Override
    public void add(T element) {
        addToRear(element);

    }

    @Override
    public void addAfter(T element, T target) {
        //find insertion point
        Node<T> targetNode = head;
        while (targetNode != null && !targetNode.getElement().equals(target)) {
            targetNode = targetNode.getNext();
        }
        if (targetNode == null) {
            throw new NoSuchElementException("LinkedList");
        }
        Node<T> newNode = new Node<>(element);
        newNode.setNext(targetNode.getNext());
        targetNode.setNext(newNode);
        if (targetNode == tail) {
            tail = newNode;
        }

        modCount++;
        size++;
    }

    @Override
    public void add(int index, T element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        if (index == 0) {
            addToFront(element);
        } else {
            Node<T> curNode = head;
            for (int i = 0; i < index - 1; i++) {
                curNode = curNode.getNext();
            }
            Node<T> newNode = new Node<T>(element);
            newNode.setNext(curNode.getNext());
            curNode.setNext(newNode);
            if (newNode.getNext() == null) {
                tail = newNode;
            }
            size++;
            modCount++;
        }
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T element = head.getElement();
        head = head.getNext();
        size--;
        modCount++;
        if (size == 0) {
            tail = null;
        }
        return element;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T element = tail.getElement();
        if (head == tail) {
            head = tail = null;
        } else {
            Node<T> currentNode = head;
            while (currentNode.getNext() != tail) {
                currentNode = currentNode.getNext();
            }
            currentNode.setNext(null);
            tail = currentNode;
        }
        size--;
        modCount++;
        return element;
    }

    @Override
    public T remove(T element) {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }

        boolean found = false;
        Node<T> previous = null;
        Node<T> current = head;

        while (current != null && found == false) {
            if (element.equals(current.getElement())) {
                found = true;
            } else {
                previous = current;
                current = current.getNext();
            }
        }

        if (found == false) {
            throw new NoSuchElementException();
        }

        if (size() == 1) { //one node
            head = tail = null;
        } else if (current == head) { //first node
            head = current.getNext();
        } else if (current == tail) { //last node
            tail = previous;
            tail.setNext(null);
        } else { //somewhere in the middle
            previous.setNext(current.getNext());
        }

        size--;
        modCount++;

        return current.getElement();
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        T removedElement;
        if (index == 0) {
            removedElement = head.getElement();
            head = head.getNext();
            if (head == null) {
                tail = null;
            }
        } else {
            Node<T> currentNode = head;
            Node<T> previousNode = null;
            for (int i = 0; i < index; i++) {
                previousNode = currentNode;
                currentNode = currentNode.getNext();
            }
            previousNode.setNext(currentNode.getNext());
            if (currentNode == tail) {
                tail = previousNode;
            }
            removedElement = currentNode.getElement();
        }
        size--;
        modCount++;
        return removedElement;
    }

    @Override
    public void set(int index, T element) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        Node<T> currentNode = head;
        for (int i = 0; i < index; i++) {
            currentNode = currentNode.getNext();
        }
        currentNode.setElement(element);
        modCount++;
    }

    @Override
    public T get(int index) {
        Node<T> currentNode = head;
        int currentIndex = 0;
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        while (currentIndex < index) {
            currentNode = currentNode.getNext();
            currentIndex++;
        }
        return currentNode.getElement();
    }

    @Override
    public int indexOf(T element) {
        Node<T> currNode = head;
        int currIndex = 0;
        while (currNode != null && !currNode.getElement().equals(element)) {
            currNode = currNode.getNext();
            currIndex++;
        }
        if (currNode == null) {
            currIndex = -1;
        }
        return currIndex;
    }

    @Override
    public T first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return head.getElement();
    }

    @Override
    public T last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return tail.getElement();
    }

    @Override
    public boolean contains(T target) {
        return (indexOf(target) != -1);
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append("[");
        for (T element : this) {
            str.append(element.toString());
            str.append(", ");
        }
        if (size() > 0) {
            str.delete(str.length() - 2, str.length());
        }
        str.append("]");
        return str.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new SLLIterator();
    }

    @Override
    public ListIterator<T> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<T> listIterator(int startingIndex) {
        throw new UnsupportedOperationException();
    }

    /**
     * Iterator for IUSingleLinkedList
     */
    private class SLLIterator implements Iterator<T> {

        private Node<T> nextNode;
        private boolean canRemove;
        private int iterModCount;

        /* Initialize Iterator before first element */
        public SLLIterator() {
            nextNode = head;
            canRemove = false;
            iterModCount = modCount;
        }

        @Override
        public boolean hasNext() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            return nextNode != null;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            T retVal = nextNode.getElement();
            nextNode = nextNode.getNext();
            canRemove = true;
            return retVal;
        }

        @Override
        public void remove() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            if (!canRemove) {
                throw new IllegalStateException();
            }
            canRemove = false;
            if (head.getNext() == nextNode) {
                head = nextNode;
                if (head == null) {
                    tail = null;
                }
            } else {
                Node<T> prevNode = head;
                while (prevNode.getNext().getNext() != nextNode) {
                    prevNode = prevNode.getNext();
                }
                prevNode.setNext(nextNode);
                if (nextNode == null) {
                    tail = prevNode;
                }
            }
            modCount++;
            iterModCount++;
            size--;
        }

    }

}
