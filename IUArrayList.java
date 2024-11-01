
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * Array-based implementation of IndexedUnsortedList. An Iterator with working
 * remove() method is implemented, but ListIterator is unsupported.
 *
 * @author Dalton Bilau Goncalves CS221
 *
 * @param <T> type to store
 */
public class IUArrayList<T> implements IndexedUnsortedList<T> {

    private static final int DEFAULT_CAPACITY = 10;
    private static final int NOT_FOUND = -1;

    private int rear;
    private T[] array;
    private int modCount;

    /**
     * Creates an empty list with default initial capacity
     */
    public IUArrayList() {
        this(DEFAULT_CAPACITY);
    }

    /**
     * Creates an empty list with the given initial capacity
     *
     * @param defaultCapacity
     */
    @SuppressWarnings("unchecked")
    public IUArrayList(int defaultCapacity) {
        array = (T[]) (new Object[defaultCapacity]);
        rear = 0;
        modCount = 0;
    }

    /**
     * Double the capacity of array
     */
    private void expandCapacity() {
        array = Arrays.copyOf(array, array.length * 2);
    }

    @Override
    public void addToFront(T element) {
        if (rear == array.length) {
            expandCapacity();
        }
        //shift elements
        for (int i = rear; i > 0; i--) {
            array[i] = array[i - 1];
        }
        array[0] = element;
        rear++;
        modCount++;

    }

    @Override
    public void addToRear(T element) {
        if (rear == array.length) {
            expandCapacity();
        }
        array[rear] = element;
        rear++;
        modCount++;
    }

    @Override
    public void add(T element) {
        addToRear(element);
    }

    @Override
    public void addAfter(T element, T target) {
        if (rear == array.length) {
            expandCapacity();
        }
        if (!contains(target)) {
            throw new NoSuchElementException();
        }
        int index = indexOf(target) + 1;
        add(index, element);
    }

    @Override
    public void add(int index, T element) {
        if (rear == array.length) {
            expandCapacity();
        }
        if (index < 0 || index > rear) {
            throw new IndexOutOfBoundsException();
        }
        rear++;
        //shift elements
        for (int i = rear - 1; i >= index; i--) {
            array[i + 1] = array[i];
        }
        array[index] = element;
        modCount++;
    }

    @Override
    public T removeFirst() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T retVal = array[0];
        rear--;
        //shift elements
        for (int i = 0; i < rear; i++) {
            array[i] = array[i + 1];
        }
        array[rear] = null;
        modCount++;

        return retVal;
    }

    @Override
    public T removeLast() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        T retVal = array[rear - 1];
        array[rear - 1] = null;
        rear--;
        modCount++;
        return retVal;
    }

    @Override
    public T remove(T element) {
        int index = indexOf(element);
        if (index == NOT_FOUND) {
            throw new NoSuchElementException();
        }

        T retVal = array[index];

        rear--;
        //shift elements
        for (int i = index; i < rear; i++) {
            array[i] = array[i + 1];
        }
        array[rear] = null;
        modCount++;

        return retVal;
    }

    @Override
    public T remove(int index) {
        if (index < 0 || index >= rear) {
            throw new IndexOutOfBoundsException();
        }
        T retVal = array[index];

        rear--;
        //shift elements
        for (int i = index; i < rear; i++) {
            array[i] = array[i + 1];
        }
        array[rear] = null;
        modCount++;

        return retVal;
    }

    @Override
    public void set(int index, T element) {
        if (index < 0 || index >= rear) {
            throw new IndexOutOfBoundsException();
        }
        array[index] = element;
        if (rear == index) {
            rear++;
        }
        modCount++;
    }

    @Override
    public T get(int index) {
        if (index < 0 || index >= rear) {
            throw new IndexOutOfBoundsException();
        }
        return array[index];
    }

    @Override
    public int indexOf(T element) {
        int index = NOT_FOUND;

        if (!isEmpty()) {
            int i = 0;
            while (index == NOT_FOUND && i < rear) {
                if (element.equals(array[i])) {
                    index = i;
                } else {
                    i++;
                }
            }
        }

        return index;
    }

    @Override
    public T first() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return array[0];
    }

    @Override
    public T last() {
        if (isEmpty()) {
            throw new NoSuchElementException();
        }
        return array[rear - 1];
    }

    @Override
    public boolean contains(T target) {
        return (indexOf(target) != NOT_FOUND);

    }

    @Override
    public boolean isEmpty() {
        if (rear == 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int size() {
        return rear;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (T element : this) {
            sb.append(element.toString());
            sb.append(", ");
        }
        if (size() > 0) {
            sb.delete(sb.length() - 2, sb.length()); // remove last comma & space
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public Iterator<T> iterator() {
        return new ALIterator();
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
     * Basic Iterator for IUArrayList includes remove()
     */
    private class ALIterator implements Iterator<T> {

        private int nextIndex;
        private boolean canRemove;
        private int iterModCount;

        /**
         * Inititalizes the iterator in front of first element.
         */
        public ALIterator() {
            nextIndex = 0;
            canRemove = false;
            iterModCount = modCount;
        }

        @Override
        public boolean hasNext() {
            if (iterModCount != modCount) {
                throw new ConcurrentModificationException();
            }
            return nextIndex < rear;
        }

        @Override
        public T next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            canRemove = true;
            nextIndex++;
            return array[nextIndex - 1];

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
            for (int i = nextIndex - 1; i < rear - 1; i++) {
                array[i] = array[i + 1];
            }
            array[rear - 1] = null;
            rear--;
            nextIndex--;
            modCount++;
            iterModCount++;
        }
    } //end ALIterator class

} //end IUArrayList class
