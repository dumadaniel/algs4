import edu.princeton.cs.algs4.In;
import java.io.File;

public class CircularSuffixArray {
  private static final int R = 256;
  private int length;
  private int[] index;

  public CircularSuffixArray(String s) { // circular suffix array of s
    if (s == null) throw new NullPointerException();
    this.length = s.length();

    this.index  = new int[length];
    for (int i = 0; i < length; i++) // init with original suffix indices
      index[i] = i;

    sort(s, index, 0, length() - 1, 0, true);
  }

  private void sort(String s, int[] a, int lo, int hi, int d, boolean log) {
    if (hi <= lo) return;

    int lt = lo, gt = hi;
    char v = s.charAt((a[lo] + d) % s.length()); // get pivot character

    // partition the subarray
    int i = lo + 1;
    while (i <= gt) {
      char t = s.charAt((a[i] + d) % s.length());
      if      (t < v) exch(a, lt++, i++); 
      else if (t > v) exch(a, i, gt--);
      else            i++;
    }

    sort(s, a, lo, lt-1, d);                        // sort < partition
    if (d < (a.length)*2) sort(s, a, lt, gt, d+1);  // sort = partition
    sort(s, a, gt+1, hi, d);                        // sort > partition
  }

  private void exch(int[] a, int i, int j) {
    int tmp = a[i];
    a[i] = a[j];
    a[j] = tmp;
  }

  public int length() { // length of s
    return length;
  }

  public int index(int i) { // returns index of ith sorted suffix
    if (i < 0 || i > length-1) throw new IndexOutOfBoundsException();
    return index[i];
  }

  public static void main(String[] args) { // unit testing of the methods (optional)
    In in = new In(new File(args[0]));
    CircularSuffixArray csa = new CircularSuffixArray(in.readAll());
    for (int i = 0; i < csa.length(); i++)
      System.out.print(csa.index(i)+" ");
    System.out.println("");
  }
}