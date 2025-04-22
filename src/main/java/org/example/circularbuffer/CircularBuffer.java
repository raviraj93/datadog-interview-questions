package org.example.circularbuffer;

/**
 * A fixed-size circular buffer (FIFO queue) with wrap-around indexing.
 * Supports enqueue, dequeue, peek, and status checks.
 *
 * @param <T> Type of elements stored in the buffer
 */
public class CircularBuffer<T> {
    private final T[] buffer;
    private final int capacity;
    private int head = 0;     // next element to dequeue
    private int tail = 0;     // next slot to enqueue
    private int size = 0;     // number of elements currently in buffer

    /**
     * Constructs a circular buffer with the given capacity.
     *
     * @param capacity maximum number of elements buffer can hold
     * @throws IllegalArgumentException if capacity <= 0
     */
    @SuppressWarnings("unchecked")
    public CircularBuffer(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be > 0");
        }
        this.capacity = capacity;
        // Java doesn't allow generic array creation directly
        this.buffer = (T[]) new Object[capacity];
    }

    /**
     * Adds an element to the end of the buffer.
     *
     * @param element the element to enqueue
     * @throws IllegalStateException if the buffer is full
     */
    public void enqueue(T element) {
        if (isFull()) {
            throw new IllegalStateException("Buffer is full");
        }
        buffer[tail] = element;
        // Advance tail with wrap-around
        tail = (tail + 1) % capacity;
        size++;
    }

    /**
     * Removes and returns the element at the front of the buffer.
     *
     * @return the dequeued element
     * @throws IllegalStateException if the buffer is empty
     */
    public T dequeue() {
        if (isEmpty()) {
            throw new IllegalStateException("Buffer is empty");
        }
        T element = buffer[head];
        buffer[head] = null;  // for garbage collection
        // Advance head with wrap-around
        head = (head + 1) % capacity;
        size--;
        return element;
    }

    /**
     * Peeks at the element at the front without removing it.
     *
     * @return the head element
     * @throws IllegalStateException if the buffer is empty
     */
    public T peek() {
        if (isEmpty()) {
            throw new IllegalStateException("Buffer is empty");
        }
        return buffer[head];
    }

    /**
     * Returns whether the buffer is empty.
     *
     * @return true if buffer has no elements
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Returns whether the buffer is full.
     *
     * @return true if buffer has reached its capacity
     */
    public boolean isFull() {
        return size == capacity;
    }

    /**
     * Returns the current number of elements in the buffer.
     *
     * @return current size
     */
    public int size() {
        return size;
    }

    /**
     * Returns the buffer capacity.
     *
     * @return maximum number of elements
     */
    public int capacity() {
        return capacity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CircularBuffer[");
        for (int i = 0; i < size; i++) {
            int idx = (head + i) % capacity;
            sb.append(buffer[idx]);
            if (i < size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Simple demonstration of CircularBuffer usage.
     */
    public static void main(String[] args) {
        CircularBuffer<Integer> cb = new CircularBuffer<>(3);
        System.out.println(cb);           // []

        cb.enqueue(1);
        cb.enqueue(2);
        System.out.println(cb);           // [1, 2]

        cb.enqueue(3);
        System.out.println(cb.isFull());  // true
        System.out.println(cb);           // [1, 2, 3]

        System.out.println(cb.dequeue()); // 1
        System.out.println(cb);           // [2, 3]

        cb.enqueue(4);
        System.out.println(cb);           // [2, 3, 4]
    }
}

