public class PointSET {
  private SET<Point2D> points;

  public PointSET() {
    this.points = new SET<Point2D>();
  }

  public boolean isEmpty() {
    return points.isEmpty();
  }

  public int size() {
    return points.size();
  }

  public void insert(Point2D p) {
    if (p == null) throw new NullPointerException();
    points.add(p);
  }

  public boolean contains(Point2D p) {
    if (p == null) throw new NullPointerException();
    return points.contains(p);
  }

  public void draw() {
    for (Point2D point : points) 
      StdDraw.point(point.x(), point.y());
  }

  public Iterable<Point2D> range(RectHV rect) {
    if (rect == null) throw new NullPointerException();
    SET<Point2D> inRange = new SET<Point2D>();
    for (Point2D point : points) {
      if (rect.distanceTo(point) == 0.0) inRange.add(point);
    }
    return inRange;
  }

  public Point2D nearest(Point2D p) {
    if (p == null) throw new NullPointerException();
    Point2D nearest = null;
    for (Point2D point : points) {
      if (nearest == null) nearest = point;
      if (p.distanceTo(point) < p.distanceTo(nearest)) nearest = point;
    }
    return nearest;
  }

  public static void main(String[] args) { return; }
}