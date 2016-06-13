import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.ArrayList;

public class BurrowsWheeler {
  private static final int R = 256;

  // apply Burrows-Wheeler encoding, reading from standard input and writing to standard output
  public static void encode() {
    while(!BinaryStdIn.isEmpty()) {
      String s = BinaryStdIn.readString();
      CircularSuffixArray csa = new CircularSuffixArray(s);

      boolean first = false;
      for (int i = 0; i < csa.length(); i++) {
        // look for original string in sorted suffix array
        if (csa.index(i) == 0) {
          first = true;
          BinaryStdOut.write(i);

          // write previous i-1 characters
          for (int j = 0; j < i; j++)
            BinaryStdOut.write(s.charAt((csa.index(j) + (s.length() - 1)) % s.length()));
        }

        // write current char if index of original string found
        if (first) BinaryStdOut.write(s.charAt((csa.index(i) + (s.length() - 1)) % s.length()));
      }
    } 

    BinaryStdOut.close();
  }

  // apply Burrows-Wheeler decoding, reading from standard input and writing to standard output
  public static void decode() {
    // read first - index of original string in the sorted suffix array
    int first = BinaryStdIn.readInt();

    // build t[] - last column of sorted suffix array - from input
    int[] count = new int[R+1];
    ArrayList<Character> t = new ArrayList<Character>();
    while (!BinaryStdIn.isEmpty()) {
      char c = BinaryStdIn.readChar();
      t.add(c);
      count[(int)c + 1]++;
    }

    // sort t[] and construct next[]
    char[] sort  = new char[t.size()];
    for (int r = 0; r < R; r++) // compute frequency cumulates
      count[r+1] += count[r];

    // if jth orig suffix (original string shifted j chars) is ith row in sorted order
    // then next[i] = index in sorted csa where (j+1)st original suffix appears
    int[] next = new int[t.size()];
    for (int k = 0; k < t.size(); k++) {
      sort[count[t.get(k)]] = t.get(k);
      next[count[t.get(k)]++] = k;
    }

    for (int i = 0; i < next.length; i++, first = next[first])
      BinaryStdOut.write(sort[first]);

    BinaryStdOut.close();
  }

  // if args[0] is '-', apply Burrows-Wheeler encoding
  // if args[0] is '+', apply Burrows-Wheeler decoding
  public static void main(String[] args) {
    if (args[0].equals("-")) 
      encode();
    else if (args[0].equals("+")) 
      decode();
    else 
      System.out.println("Usage: - for encoding, + for decoding");
  }
}