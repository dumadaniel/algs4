import java.util.Iterator;
import java.util.NoSuchElementException;

public class Deque<Item> implements Iterable<Item> {
    private Node<Item> first;
    private Node<Item> last;
    private int size;

    public boolean isEmpty() { return first == null && last == null; }
    public int size() { return size; }
    public Iterator<Item> iterator() { return new DequeIterator(first); }

    public void addFirst(Item item) {
        if (item == null) throw new NullPointerException();

        Node<Item> newItem = new Node<Item>(item);
        if (isEmpty()) {
            first = newItem;
            last = first;
        } else {
            Node<Item> oldFirst = first;
            first = newItem;
            first.next = oldFirst;
            oldFirst.prev = first;
        }
        size++;
    }

    public void addLast(Item item) {
        if (item == null) throw new NullPointerException();

        Node<Item> newItem = new Node<Item>(item);
        if (isEmpty()) {
            last = newItem;
            first = last;
        } else {
            Node<Item> oldLast = last;
            last = newItem;
            last.prev = oldLast;
            oldLast.next = last;
        }
        size++;
    }

    public Item removeFirst() {
        if (isEmpty()) throw new NoSuchElementException();

        Item item = first.item;
        first = first.next;
        if (first == null) last = null;
        else               first.prev = null;

        size--;
        return item;
    }

    public Item removeLast() {
        if (isEmpty()) throw new NoSuchElementException();

        Item item = last.item;
        last = last.prev;
        if (last == null) first = null;
        else              last.next = null;

        size--;
        return item;
    }

    private class DequeIterator<Item> implements Iterator<Item> {
        Node<Item> first;
        public DequeIterator(Node<Item> first) { this.first = first; }
        public boolean hasNext() { return first != null; }
        public void remove() { throw new UnsupportedOperationException(); }
        public Item next() {
            if (first == null) throw new NoSuchElementException();
            Item item = first.item;
            first = first.next;
            return item;
        }
    }

    private class Node<Item> {
        public Node<Item> next;
        public Node<Item> prev;
        public Item item;
        public Node(Item item) { this.item = item; }
    }
}