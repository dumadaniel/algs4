import java.util.NoSuchElementException;
import java.util.Iterator;

public class RandomizedQueue<Item> implements Iterable<Item> {
  private Item[] items;
  private int ptr;

  public RandomizedQueue() {
    this.items = (Item[]) new Object[1];
    this.ptr = 0;
  }

  public boolean isEmpty() { return ptr == 0; }

  public int size() { return ptr; }

  public void enqueue(Item item) {
    if (item == null) throw new NullPointerException();
    if (ptr == items.length) upsize();
    items[ptr++] = item;
  }

  public Item dequeue() {
    if (isEmpty()) throw new NoSuchElementException();
    Item item = null;
    if (ptr == 1) {
      item = items[--ptr];
      items[ptr] = null;
    } else {
      int rndm = StdRandom.uniform(ptr);
      item = items[rndm];
      items[rndm] = items[--ptr];
      items[ptr] = null;
    }
    if (!isEmpty() && ptr <= (items.length) / 4) downsize();
    return item;
  }

  public Item sample() {
    if (isEmpty()) throw new NoSuchElementException();
    return items[StdRandom.uniform(ptr)];
  }

  public Iterator<Item> iterator() {
    return new RandomizedQueueIterator(size());
  }

  private void upsize() {
    Item[] newItems = (Item[]) new Object[items.length * 2];
    for (int i = 0; i < items.length; i++) {
      newItems[i] = items[i];
    }
    items = newItems;
  }

  private void downsize() {
    Item[] newItems = (Item[]) new Object[items.length / 2];
    for (int i = 0; i < (items.length) / 2; i++) {
      newItems[i] = items[i];
    }
    items = newItems;
  }

  private class RandomizedQueueIterator<Item> implements Iterator<Item> {
    int[] rndm;
    int ptr;
    public boolean hasNext() { return ptr < rndm.length; }
    public void remove() { throw new UnsupportedOperationException(); }
    public Item next() {
      if (ptr >= rndm.length) throw new NoSuchElementException();
      return (Item) items[rndm[ptr++]];
    }
    public RandomizedQueueIterator(int size) {
      ptr = 0;
      rndm = new int[size];
      for (int i = 0; i < rndm.length; i++) {
        rndm[i] = i;
      }
      StdRandom.shuffle(rndm);
    }
  }
}