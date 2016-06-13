import java.util.Arrays;

public class Brute {
    public static void main(String[] args) {
      In file = new In(args[0]);
      Point[] points = new Point[file.readInt()];

      StdDraw.setXscale(0, 32768);
      StdDraw.setYscale(0, 32768);

      int ix = 0;
      while (!file.isEmpty()) {
        points[ix] = new Point(file.readInt(), file.readInt());
        points[ix++].draw();
      }

      Arrays.sort(points);

      for (int i = 0; i < points.length; i++) {
        for (int j = i+1; j < points.length; j++) {
          for (int k = j+1; k < points.length; k++) {
            for (int l = k+1; l < points.length; l++) {
              Point p = points[i];
              Point q = points[j];
              Point r = points[k];
              Point s = points[l];
              if (p.slopeTo(q) == p.slopeTo(r) && p.slopeTo(r) == p.slopeTo(s)) {
                System.out.println(p + " -> " + q + " -> " + r + " -> " + s);
                p.drawTo(s);
              }
            }
          }
        }
      }

      file.close();
    }
}