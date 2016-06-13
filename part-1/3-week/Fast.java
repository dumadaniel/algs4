import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class Fast {
    public static void main(String[] args) {
      // READ POINTS FROM FILE
      In file = new In(args[0]);
      Point[] points = new Point[file.readInt()];

      StdDraw.setXscale(0, 32768);
      StdDraw.setYscale(0, 32768);

      int p = 0;
      while (!file.isEmpty()) {
        points[p] = new Point(file.readInt(), file.readInt());
        points[p].draw();
        p++;
      }

      // IMPLEMENT FAST SORT ALGORITHM
      for (int i = 0; i < points.length; i++) {
        Arrays.sort(points, i, points.length, points[i].SLOPE_ORDER);

        List<Double> examined = new ArrayList<Double>();
        int j = 0;
        while (j < i) {
          examined.add(points[j].slopeTo(points[i]));
          j++;
        }

        double prevSlope = 0.0;
        int count = 1;
        while (j < points.length) {
          double currSlope = points[i].slopeTo(points[j]);
          if (prevSlope == currSlope) {
            count++;
          } else {
            if (count >= 3 && !examined.contains(prevSlope)) {
              printSegment(points, i, j, count);
            }
            prevSlope = currSlope;
            count = 1;
          }
          j++;
        }
        if (count >= 3 && !examined.contains(prevSlope))
          printSegment(points, i, j, count);
      }

      System.out.println("Done.");
      file.close();
    }

    // CAN I AVOID CREATING AN AUXILLIARY ARRAY
    // BY SORTING THE SUBARRAY LEXICOGRAPHICALLY
    // BEFORE TESTING COLLINEARITY
    private static void printSegment(Point[] points, int origin, int end, int count) {
      Point[] segment = new Point[count+1];
      int i = 0;

      segment[i++] = points[origin];
      while (i < segment.length) {
        segment[i++] = points[end-count];
        count--;
      }
      Arrays.sort(segment);

      String out = segment[0].toString();
      for (i = 1; i < segment.length; i++) {
        out += " -> " + segment[i];
      }
      System.out.println(out);
      segment[0].drawTo(segment[segment.length-1]);
    }
}