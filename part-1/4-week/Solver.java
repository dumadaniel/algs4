import java.util.Comparator;

public class Solver {
  private Stack<Board> solution;
  private boolean solvable = false;

  public Solver(Board initial) {
    if (initial == null) throw new NullPointerException();

    MinPQ<SearchNode> moves = new MinPQ<SearchNode>(SearchNode.BY_PRIORITY);
    moves.insert(new SearchNode(initial, 0, null, false));
    moves.insert(new SearchNode(initial.twin(), 0, null, true));

    SearchNode min = null;

    while (!(min = moves.delMin()).board.isGoal()) {
      for (Board board : min.board.neighbors()) {
        if (min.prev != null && board.equals(min.prev.board)) continue;
        moves.insert(new SearchNode(board, min.moves+1, min, min.twin));
      }
    }

    if (min.twin && min.board.isGoal()) {
      solvable = false;
    } else {
      solvable = true;
      solution = new Stack<Board>();

      for (SearchNode current = min; current!= null; current = current.prev)
        solution.push(current.board);
    }
  }
  
  public boolean isSolvable() {
    return solvable;
  }

  public int moves() {
    return solution == null ? -1 : solution.size()-1;
  }

  public Iterable<Board> solution() {
    return solution;
  }

  public static void main(String[] args) {
    // create initial board from file
    In in = new In(args[0]);
    int N = in.readInt();
    int[][] blocks = new int[N][N];
    for (int i = 0; i < N; i++)
      for (int j = 0; j < N; j++)
        blocks[i][j] = in.readInt();
    Board initial = new Board(blocks);

    // solve the puzzle
    Solver solver = new Solver(initial);

    // print solution to standard output
    if (!solver.isSolvable()) {
      System.out.println("No solution possible");
    } else {
      System.out.println("Minimum number of moves = " + solver.moves());
      for (Board board : solver.solution())
        System.out.println(board);
    }
  }

  private static class SearchNode {
    private Board board;
    private int moves;
    private int priority;
    private SearchNode prev;
    private boolean twin;
    private static final Comparator<SearchNode> BY_PRIORITY = new Comparator<SearchNode>() {
      public int compare(SearchNode a, SearchNode b) {
        if (a.priority < b.priority) return -1;
        if (a.priority > b.priority) return +1;
        return 0;
      }
    }; //anonymous class!

    private SearchNode(Board board, int moves, SearchNode prev, boolean twin) {
      this.board = board;
      this.prev = prev;
      this.moves = moves;
      this.priority = board.manhattan() + this.moves;
      this.twin = twin;
    }

    public String toString() {
      StringBuilder s = new StringBuilder();
      s.append("moves="+moves+"\n");
      s.append("priority="+priority+"\n");
      s.append(board);
      return s.toString();
    }
  }
}