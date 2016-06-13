public class Percolation {
    private WeightedQuickUnionUF wquf;
    private boolean[] open;
    private boolean[] bottoms; //is tree at root i connected to bottom?
    private int n;
    private int virtualTop;

    public Percolation(int N) {
      if (N <= 0)
        throw new IllegalArgumentException("N must be greater than 0");

      n = N;
      virtualTop = 0;
      open = new boolean[(n*n)+2];
      bottoms = new boolean[(n*n)+2];
      wquf = new WeightedQuickUnionUF((n*n)+2);
    }

    public void open(int i, int j) {
      validate(i, j);

      int northX = i-1;
      int southX = i+1;
      int eastX = i;
      int westX = i;

      i = i-1; //offset for flat array
      int site = i*n + j;
      open[site] = true;

      if (i == 0)     wquf.union(virtualTop, site);
      if (i == n-1)   bottoms[site] = true;

      if (northX > 0 && isOpen(northX, j)) { //north
        if (bottoms[wquf.find((i-1)*n + j)]) bottoms[site] = true;
        wquf.union(site, ((i-1)*n + j));
      }
      if (southX <= n && isOpen(southX, j)) { //south
        if (bottoms[wquf.find((i+1)*n + j)]) bottoms[site] = true;
        wquf.union(site, ((i+1)*n + j));
      }
      if ((j+1) <= n && isOpen(eastX, j+1)) { //east
        if (bottoms[wquf.find(i*n + (j+1))]) bottoms[site] = true;
        wquf.union(site, (i*n + (j+1)));
      }
      if ((j-1) > 0 && isOpen(westX, j-1)) { //west
        if (bottoms[wquf.find(i*n + (j-1))]) bottoms[site] = true;
        wquf.union(site, (i*n + (j-1)));
      }

      if (bottoms[site]) bottoms[wquf.find(site)] = true;
    }

    public boolean isOpen(int i, int j) {
      validate(i, j);
      return open[(i-1)*n + j];
    }

    public boolean isFull(int i, int j) {
      validate(i, j);
      return isOpen(i, j) && wquf.connected(virtualTop, ((i-1)*n + j));
    }

    public boolean percolates() {
      return bottoms[wquf.find(virtualTop)]; //virtual top connected to bottom
    }

    private void validate(int i, int j) {
      if (i < 1 || j < 1 || i > n || j > n)
        throw new IndexOutOfBoundsException("i, j must be between 1 and N");      
    }

    public static void main(String[] args) {
      Percolation perc = new Percolation(1);
      perc.open(1, 1);
      System.out.println("percolates: "+perc.percolates());
    }
}