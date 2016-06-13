import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

public class MoveToFront {
  private static final int R = 256;
  private static final char[] CHARS = new char[R];
  static {
    for (char c = 0; c < R; c++)
      CHARS[c] = c;
  }

  // apply move-to-front encoding, reading from standard input and writing to standard output
  public static void encode() {
    char[] sequence = new char[R];
    System.arraycopy(CHARS, 0, sequence, 0, CHARS.length);

    while(!BinaryStdIn.isEmpty()) {
      char c = BinaryStdIn.readChar();                    // read 8bit char c

      int pos;
      for (pos = 0; pos < sequence.length; pos++)
        if (sequence[pos] == c) break;
      BinaryStdOut.write((char)pos);                      // write index of c in seq

      System.arraycopy(sequence, 0, sequence, 1, pos);
      sequence[0] = c;                                    // move c to front
    }

    BinaryStdOut.close();
  }

  // apply move-to-front decoding, reading from standard input and writing to standard output
  public static void decode() {
    char[] sequence = new char[R];
    System.arraycopy(CHARS, 0, sequence, 0, CHARS.length);

    while(!BinaryStdIn.isEmpty()) {
      char pos = BinaryStdIn.readChar();                      // read 8bit int i

      char c = sequence[pos];
      BinaryStdOut.write(c);                                  // write ith char c in seq
      
      System.arraycopy(sequence, 0, sequence, 1, (int)pos);
      sequence[0] = c;                                        // move c to front
    }

    BinaryStdOut.close();
  }

  // if args[0] is '-', apply move-to-front encoding
  // if args[0] is '+', apply move-to-front decoding
  public static void main(String[] args) {
    if (args[0].equals("-")) 
      encode();
    else if (args[0].equals("+")) 
      decode();
    else 
      System.out.println("Usage: - for encoding, + for decoding");
  }

  private class Node<T> {
    T val;
    Node next;

    Node(T val) {
      this.val = val;
    }
  }
}
