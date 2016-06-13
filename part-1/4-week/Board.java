public class Board {
    private int[][] blocks;
    private int N;

    public Board(int[][] blocks) {
        this.N = blocks.length;
        this.blocks = copy(blocks);
    }

    public int dimension() {
        return N;
    }

    public int hamming() {
        int result = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (blocks[i][j] == 0) continue;
                if ((blocks[i][j] - 1) != (i*N + j)) result++;
            }
        }
        return result;
    }

    public int manhattan() {
        int result = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (blocks[i][j] == 0) continue;
                int target = blocks[i][j] - 1;
                result += Math.abs(i-target/N) + Math.abs(j-target % N);
            }
        }
        return result;
    }

    public boolean isGoal() {
        int prev = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i*N+j == N*N-1) continue;
                if (prev >= blocks[i][j]) return false;
                prev = blocks[i][j];
            }
        }
        return true;
    }

    public Board twin() {
        int[][] twin = copy(blocks);

        outerloop:
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (twin[i][j] == 0) continue;
                if ((j+1) >= N || twin[i][j+1] == 0) continue;
                exch(twin, i, j, i, j+1);
                break outerloop;
            }
        }

        return new Board(twin);
    }

    public boolean equals(Object y) {
        if (y == this) return true;
        if (y == null) return false;
        if (y.getClass() != this.getClass()) return false;

        Board that = (Board) y;
        if (this.N != that.N) return false;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (this.blocks[i][j] != that.blocks[i][j]) return false;
            }
        }

        return true;
    }

    public Iterable<Board> neighbors() {
        Queue<Board> neighbors = new Queue<Board>();

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (blocks[i][j] != 0) continue;
                else {
                    if ((i-1) >= 0) { //north
                        Board north = new Board(copy(blocks));
                        exch(north.blocks, i, j, i-1, j);
                        neighbors.enqueue(north);
                    }
                    if ((i+1) < N) { //south
                        Board south = new Board(copy(blocks));
                        exch(south.blocks, i, j, i+1, j);
                        neighbors.enqueue(south);
                    }
                    if ((j-1) >= 0) { //west
                        Board west = new Board(copy(blocks));
                        exch(west.blocks, i, j, i, j-1);
                        neighbors.enqueue(west);
                    }
                    if ((j+1) < N) { //east
                        Board east = new Board(copy(blocks));
                        exch(east.blocks, i, j, i, j+1);
                        neighbors.enqueue(east);
                    }
                    break;
                }
            }
        }

        return neighbors;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", blocks[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }

    /******************************
     * Helper methods
     ******************************/

    private int[][] copy(int[][] a) {
        int[][] copy = new int[a.length][];

        for (int i = 0; i < a.length; i++) {
            copy[i] = new int[a[i].length];
            System.arraycopy(a[i], 0, copy[i], 0, a[i].length);
        }

        return copy;
    }

    private void exch(int[][] a, int i, int j, int k, int l) {
        int temp = a[i][j];
        a[i][j] = a[k][l];
        a[k][l] = temp;
    }

    public static void main(String[] args) {
        int[][] two = new int[2][2];
        two[0][0] = 1;
        two[0][1] = 0;
        two[1][0] = 2;
        two[1][1] = 3;

        int[][] three = new int[3][3];
        three[0][0] = 1;
        three[0][1] = 0;
        three[0][2] = 2;
        three[1][0] = 3;
        three[1][1] = 4;
        three[1][2] = 5;
        three[2][0] = 6;
        three[2][1] = 7;
        three[2][2] = 8;

        Board board = new Board(three);
        System.out.println(board);
        System.out.println(board.twin());

        /*
        for (Board neighbor : board.neighbors()) {
            System.out.println(neighbor);
        }
        
        System.out.println(board);
        System.out.println(board.hamming());
        System.out.println(board.manhattan());
        System.out.println(board.isGoal());
        System.out.println(board.twin());
        */
    }
}