public class PercolationStats {
    private int n;
    private int t;
    private double[] pstars;
    private double pstarTotal = 0;

    public PercolationStats(int N, int T) {
        if (N <= 0 || T <= 0) 
            throw new IllegalArgumentException("N, T must be greater than 0");

        n = N;
        t = T;
        pstars = new double[t];
        for (int k = 0; k < t; k++) {
          Percolation perc = new Percolation(n);

          double pstar = 0;
          while (!perc.percolates()) {
            int i = StdRandom.uniform(1, n+1);
            int j = StdRandom.uniform(1, n+1);

            if (!perc.isOpen(i, j)) {
              perc.open(i, j);
              pstar++;
            }
          }
          pstar = pstar/(n*n);
          pstars[k] = pstar;
          pstarTotal += pstar;
        }
    }

    public double mean() {
      return pstarTotal/t;
    }

    public double stddev() {
      return StdStats.stddev(pstars);
    }

    public double confidenceLo() {
      double mean = mean();
      double stddev = stddev();
      return mean - ((1.96*stddev)/Math.sqrt(t));
    }

    public double confidenceHi() {
      double mean = mean();
      double stddev = stddev();
      return mean + ((1.96*stddev)/Math.sqrt(t));
    }

    public static void main(String[] args) {
      PercolationStats percStats = new PercolationStats(Integer.parseInt(args[0]), Integer.parseInt(args[1]));

      System.out.println("mean = " + percStats.mean());
      System.out.println("stddev = " + percStats.stddev());
      System.out.println("95% confidence interval = " + percStats.confidenceLo() + ", " +percStats.confidenceHi());
    }
}