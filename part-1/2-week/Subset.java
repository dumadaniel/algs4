public class Subset {
  public static void main(String[] args) {
    RandomizedQueue<String> rq = new RandomizedQueue<String>();
    int k = Integer.parseInt(args[0]);

    int count = 0;
    while (count++ < k) {
      rq.enqueue(StdIn.readString());
    }
    
    while (!StdIn.isEmpty()) {
      int rndm = StdRandom.uniform(0, count++);
      String s = StdIn.readString();
      if (rndm < k) {
        rq.dequeue();
        rq.enqueue(s);
      }
    }

    for (int i = 0; i < k; i++) {
      System.out.println(rq.dequeue());
    }
  }
}