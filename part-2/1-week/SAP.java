import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

import java.util.List;
import java.util.ArrayList;

public class SAP {
  private Digraph G;
  private int[][] lengths, ancestors;

  // constructor takes a digraph (not necessarily a DAG)
  public SAP(Digraph G) {
    if (G == null) 
      throw new NullPointerException();

    this.G = new Digraph(G);
    //lengths = new int[G.V()][G.V()];
    //ancestors = new int[G.V()][G.V()];
  }

  // length of shortest ancestral path between v and w; -1 if no such path
  public int length(int v, int w) {
    //if (lengths[v][w] != 0) return lengths[v][w];
    return bfs(v, w, true);
  }

  // a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
  public int ancestor(int v, int w) {
    //if (ancestors[v][w] != 0) return ancestors[v][w];
    return bfs(v, w, false);
  }

  // length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
  public int length(Iterable<Integer> vSet, Iterable<Integer> wSet) {
    if (vSet == null || wSet == null)
      throw new NullPointerException();

    boolean path = false;
    int sap = G.V();
    for (int v : vSet) {
      for (int w : wSet) {
        int length = length(v, w);
        if (length != -1 && length < sap) {
          path = true;
          sap = length;
        }
      }
    }

    if (path)
      return sap;

    return -1;
  }

  // a common ancestor that participates in shortest ancestral path; -1 if no such path
  public int ancestor(Iterable<Integer> vSet, Iterable<Integer> wSet) {
    if (vSet == null || wSet == null) 
      throw new NullPointerException();

    boolean path = false;
    int sap = G.V();
    int ancestor = -1;
    for (int v : vSet) {
      for (int w : wSet) {
        int length = length(v, w);
        if (length != -1 && length < sap) {
          path = true;
          sap = length;
          ancestor = ancestor(v, w);
        }
      }
    }

    if (path)
      return ancestor;

    return -1;
  }

  // 1. bfs on v
  // 2. bfs on w, add distToW to distToV, keep smallest total
  private int bfs(int v, int w, boolean length) {
    boolean[] vMarked = new boolean[G.V()];
    boolean[] wMarked = new boolean[G.V()];
    int[] vDistTo = new int[G.V()];
    int[] wDistTo = new int[G.V()];

    Queue<Integer> q = new Queue<Integer>();

    // BFS on G from v
    q.enqueue(v);
    vMarked[v] = true;
    vDistTo[v] = 0;

    while (!q.isEmpty()) {
      int curr = q.dequeue();
      for (int x : G.adj(curr)) {
        if (!vMarked[x]) {
          q.enqueue(x);
          vMarked[x] = true;
          vDistTo[x] = vDistTo[curr] + 1;
        }
      }
    }

    // BFS on G from w
    q.enqueue(w);
    wMarked[w] = true;
    wDistTo[w] = 0;

    while (!q.isEmpty()) {
      int curr = q.dequeue();
      for (int x : G.adj(curr)) {
        if (!wMarked[x]) {
          q.enqueue(x);
          wMarked[x] = true;
          wDistTo[x] = wDistTo[curr] + 1;
        }
      }
    }

    boolean shared = false;
    int ancestor = -1;
    int sap = G.V();

    for (int i = 0; i < vMarked.length; i++) {
      if (vMarked[i] && wMarked[i] && (vDistTo[i] + wDistTo[i] < sap)) {
        shared = true;
        ancestor = i;
        sap = vDistTo[i] + wDistTo[i];
      }
    }

    if (shared) {
      if (length) {
        //lengths[v][w] = sap;
        return sap;
      }
      else {
        //ancestors[v][w] = ancestor;
        return ancestor;
      }
    }

    //lengths[v][w] = -1;
    //ancestors[v][w] = -1;
    return -1;
  }

  // unit testing
  public static void main(String[] args) {
    In in = new In(args[0]);
    Digraph G = new Digraph(in);
    SAP sap = new SAP(G);

    if (args.length > 1)
      testSet(sap);
    else
      testSingle(sap);
  }

  private static void testSingle(SAP sap) {
    while (!StdIn.isEmpty()) {
      int v = StdIn.readInt();
      int w = StdIn.readInt();
      int length = sap.length(v, w);
      int ancestor = sap.ancestor(v, w);
      StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
    }
  }

  private static void testSet(SAP sap) {
    List<Integer> a = new ArrayList<Integer>();
    List<Integer> b = new ArrayList<Integer>();

    // 3 11 -- 4, 
    // 3 12 -- 4
    // 3 2  -- 3
    // 9 11 -- 3
    // 9 12 -- 3
    // 9 2  -- 4
    // 7 11 -- 5
    // 7 12 -- 5
    // 7 2  -- 4

    a.add(3);
    a.add(9);
    a.add(7);

    b.add(11);
    b.add(12);
    b.add(2);

    int length = sap.length(a, b);
    int ancestor = sap.ancestor(a, b);
    StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
  }
}