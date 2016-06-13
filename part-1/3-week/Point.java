import java.util.Comparator;

public class Point implements Comparable<Point> {

    // compare points by slope
    public final Comparator<Point> SLOPE_ORDER = new BySlope();  // YOUR DEFINITION HERE

    private final int x;                                         // x coordinate
    private final int y;                                         // y coordinate

    // create the point (x, y)
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
    }

    // plot this point to standard drawing
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    // draw line between this point and that point to standard drawing
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    // slope between this point and that point
    public double slopeTo(Point that) {
        if (this.compareTo(that) == 0) return Double.NEGATIVE_INFINITY;
        if (that.x - this.x == 0)      return Double.POSITIVE_INFINITY;
        if (that.y - this.y == 0)      return 0.0;
        return (double) (that.y - this.y)/(that.x - this.x);
    }

    // is this point lexicographically smaller than that one?
    // comparing y-coordinates and breaking ties by x-coordinates
    public int compareTo(Point that) {
        if (this.y < that.y) return -1;
        if (this.y > that.y) return +1;
        if (this.x < that.x) return -1;
        if (this.x > that.x) return +1;
        return 0;
    }

    // return string representation of this point
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    private class BySlope implements Comparator<Point> {
        public int compare(Point a, Point b) {
            if (slopeTo(a) < slopeTo(b)) return -1;
            if (slopeTo(a) > slopeTo(b)) return +1;
            return 0;
        }
    }

    // unit test
    public static void main(String[] args) {
        Point p = new Point(421, 127);
        Point q = new Point(303, 127);
        System.out.println(p.slopeTo(q));

        double neg = 0.0/(303-421);
        System.out.println(neg);
        double pos = 0.0/(421-303);
        System.out.println(pos);
    }
}