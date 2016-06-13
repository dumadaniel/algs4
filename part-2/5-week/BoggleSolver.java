import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

import java.util.HashSet;
import java.util.Set;

public class BoggleSolver {
  private static final int R = 26;
  private AlphaTrie<Boolean> dict;
  private boolean[] marked;
  private Bag<Integer>[] adj;

  // Initializes the data structure using the given array of strings as the dictionary.
  // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
  public BoggleSolver(String[] dictionary) {
    dict = new AlphaTrie<Boolean>();

    for (String word : dictionary)
      dict.put(word, true);
  }

  // Returns the set of all valid words in the given Boggle board, as an Iterable.
  public Iterable<String> getAllValidWords(BoggleBoard board) {
    Set<String> set = new HashSet<String>();
    int rows = board.rows();
    int cols = board.cols();

    // preprocess adjacent tiles
    adj = (Bag<Integer>[]) new Bag[rows * cols];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        int tile = flat(i, j, cols);
        adj[tile] = new Bag<Integer>();

        if (i != 0)        adj[tile].add(flat(i-1, j, cols));
        if (i != rows - 1) adj[tile].add(flat(i+1, j, cols));
        if (j != 0)        adj[tile].add(flat(i, j-1, cols));
        if (j != cols - 1) adj[tile].add(flat(i, j+1, cols));

        // upleft, upright, downleft, downright
        if (i != 0 && j != 0)               adj[tile].add(flat(i-1, j-1, cols));
        if (i != 0 && j != cols - 1)        adj[tile].add(flat(i-1, j+1, cols));
        if (i != rows - 1 && j != 0)        adj[tile].add(flat(i+1, j-1, cols));
        if (i != rows - 1 && j != cols - 1) adj[tile].add(flat(i+1, j+1, cols));
      }
    }

    // DFS
    marked = new boolean[rows * cols];
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        int s = flat(i, j, cols);

        StringBuilder path = new StringBuilder();
        path.append(getChar(s, board));

        dfs(s, path, board, set, dict.root);
      }
    }

    return set;
  }

  private void dfs(int v, StringBuilder path, BoggleBoard board, Set<String> words, Node node) {
    char c = path.charAt(path.length() - 1);

    // don't extend path if node is null or node does not contain current char in path
    if (node == null || node.next[c - 'A'] == null) return;

    if (c == 'Q') {
      path.append('U');
      node = node.next[c - 'A'];
      c = 'U';
    }

    if (path.length() > 2) {
      String word = path.toString();
      if (dict.contains(word)) words.add(word);
    }

    marked[v] = true;
    for (int w : adj[v]) {
      if (!marked[w]) {
        path.append(getChar(w, board));
        dfs(w, path, board, words, node.next[c - 'A']);
        removeFromPath(path);
      }
    }
    marked[v] = false;
  }

  private int flat(int i, int j, int cols) {
    return i * cols + j;
  }

  private char getChar(int v, BoggleBoard board) {
    return board.getLetter(v / board.cols(), v % board.cols());
  }

  private void removeFromPath(StringBuilder path) {
    if (path.charAt(path.length() - 2) == 'Q') path.deleteCharAt(path.length() - 1);
    path.deleteCharAt(path.length() - 1);
  }

  // Returns the score of the given word if it is in the dictionary, zero otherwise.
  // (You can assume the word contains only the uppercase letters A through Z.)
  public int scoreOf(String word) {
    if (!dict.contains(word)) return 0;

    if      (word.length() >= 8) return 11;
    else if (word.length() >= 7) return 5;
    else if (word.length() >= 6) return 3;
    else if (word.length() >= 5) return 2;
    else if (word.length() >= 3) return 1;
    return 0;
  }

  public static void main(String[] args) {
    Stopwatch sw = new Stopwatch();
    In in = new In(args[0]);
    String[] dictionary = in.readAllStrings();
    BoggleSolver solver = new BoggleSolver(dictionary);
    BoggleBoard board = new BoggleBoard(args[1]);
    int score = 0;
    for (String word : solver.getAllValidWords(board))
    {
        score += solver.scoreOf(word);
    }
    StdOut.println("Score = " + score);
    StdOut.println("Time: " + sw.elapsedTime() + " seconds.");
  }

  private static class AlphaTrie<T> {
    private Node root = new Node();
    private int N;

    public int size() {
      return N;
    }

    public void put(String key, T val) {
      root = put(root, key, val, 0);
    }

    private Node put(Node x, String key, T val, int d) {
      if (x == null) x = new Node();
      if (d == key.length()) { 
        if (x.val == null) N++;
        x.val = val;
        return x;
      }
      char c = key.charAt(d);
      x.next[c - 'A'] = put(x.next[c - 'A'], key, val, d+1);
      return x;
    }

    public boolean contains(String key) {
      return get(key) != null;
    }

    public T get(String key) {
      Node x = get(root, key, 0);
      if (x == null) return null;
      return (T) x.val;
    }

    private Node get(Node x, String key, int d) {
      if (x == null) return null;
      if (d == key.length()) return x;
      char c = key.charAt(d);
      return get(x.next[c - 'A'], key, d+1);
    }

    public Iterable<String> keys() {
      return keysWithPrefix("");
    }

    public Iterable<String> keysWithPrefix(String prefix) {
      Queue<String> queue = new Queue<String>();
      Node x = get(root, prefix, 0);
      collect(x, new StringBuilder(prefix), queue);
      return queue;
    }

    private void collect(Node x, StringBuilder prefix, Queue<String> q) {
      if (x == null) return;
      if (x.val != null) q.enqueue(prefix.toString());
      for (char c = 65; c < 91; c++) {
        prefix.append(c);
        collect(x.next[c - 'A'], prefix, q);
        prefix.deleteCharAt(prefix.length() - 1);
      }
    }
  }

  private static class Node {
    private Object val;
    private Node[] next = new Node[R];
  }
}