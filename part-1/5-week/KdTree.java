public class KdTree {
  private static final boolean VERTICAL = true;
  private static final boolean HORIZONTAL = false;
  private Node root;

  public boolean isEmpty() { 
    return root == null;
  }

  public int size() { 
    if (isEmpty()) return 0;
    return root.size;
  }

  /***********************************************************************
    *  Insert
    ***********************************************************************/

  public void insert(Point2D p) {
    if (p == null) throw new NullPointerException();
    root = insert(root, p, VERTICAL);
  }

  private Node insert(Node x, Point2D p, boolean orientation) {
    if (x == null) return new Node(p, orientation, 1);
    if (x.point.equals(p)) return x;
    if (x.vertical) {
      if (p.x() < x.point.x()) x.left  = insert(x.left, p, HORIZONTAL);
      else                     x.right = insert(x.right, p, HORIZONTAL);
    } else {
      if (p.y() < x.point.y()) x.left  = insert(x.left, p, VERTICAL);
      else                     x.right = insert(x.right, p, VERTICAL);
    }
    x.size = 1 + size(x.left) + size(x.right);
    return x;
  }

  /***********************************************************************
    *  Contains
    ***********************************************************************/

  public boolean contains(Point2D p) {
    if (p == null) throw new NullPointerException();
    Node curr = root;
    while (curr != null) {
      if (p.equals(curr.point)) return true;
      if (curr.vertical) {
        if (p.x() < curr.point.x()) curr = curr.left;
        else                        curr = curr.right;
      } else {
        if (p.y() < curr.point.y()) curr = curr.left;
        else                        curr = curr.right;
      }
    }
    return false;
  }

  /***********************************************************************
    *  Draw
    ***********************************************************************/

  public void draw() {
    StdDraw.setXscale(0, 1);
    StdDraw.setYscale(0, 1);
    draw(root, 0.0, 1.0);
  }

  private void draw(Node x, double min, double max) {
    if (x == null) return;
    Point2D p = x.point;

    StdDraw.setPenColor();
    StdDraw.setPenRadius(0.006);
    StdDraw.point(p.x(), p.y());

    //draw vertical or horizontal line through x
    StdDraw.setPenRadius();
    if (x.vertical) {
      StdDraw.setPenColor(StdDraw.RED);
      StdDraw.line(p.x(), p.y(), p.x(), min);
      StdDraw.line(p.x(), p.y(), p.x(), max);
      draw(x.left, 0, p.x());
      draw(x.right, p.x(), 1);
    } else {
      StdDraw.setPenColor(StdDraw.BLUE);
      StdDraw.line(p.x(), p.y(), min, p.y());
      StdDraw.line(p.x(), p.y(), max, p.y());
      draw(x.left, 0, p.y());
      draw(x.right, p.y(), 1);
    }
  }

  /***********************************************************************
    *  Range
    ***********************************************************************/

  public Iterable<Point2D> range(RectHV rect) {
    if (rect == null) throw new NullPointerException();
    return findRange(rect, root, new Queue<Point2D>());
  }

  private Queue<Point2D> findRange(RectHV rect, Node x, Queue<Point2D> inRange) {
    if (x == null) return inRange;
    if (rect.contains(x.point)) inRange.enqueue(x.point);
    
    if (x.vertical) {
      if (rect.xmax() < x.point.x()) 
        findRange(rect, x.left, inRange);
      else if (rect.xmin() >= x.point.x()) 
        findRange(rect, x.right, inRange);
      else {
        findRange(rect, x.left, inRange);
        findRange(rect, x.right, inRange);
      }
    } else {
      if (rect.ymax() < x.point.y()) 
        findRange(rect, x.left, inRange);
      else if (rect.ymin() >= x.point.y()) 
        findRange(rect, x.right, inRange);
      else {
        findRange(rect, x.left, inRange);
        findRange(rect, x.right, inRange);
      }
    }
    return inRange;
  }

  /***********************************************************************
    *  Nearest
    ***********************************************************************/

  public Point2D nearest(Point2D p) {
    if (p == null) throw new NullPointerException();
    if (isEmpty()) return null;
    return nearest(p, root, root.point);
  }

  private Point2D nearest(Point2D p, Node x, Point2D closest) {
    if (x == null) return closest;

    if (p.distanceSquaredTo(x.point) < p.distanceSquaredTo(closest))
      closest = x.point;

    //use orthogonal intersection point of p and splitting line through 
    //x to test if it's worth checking the other half of the tree
    if (x.vertical) {
      if (p.x() < x.point.x()) {
        closest = nearest(p, x.left, closest);
        if (p.distanceSquaredTo(new Point2D(x.point.x(), p.y())) < p.distanceSquaredTo(closest))
          closest = nearest(p, x.right, closest);
      } else {
        closest = nearest(p, x.right, closest);
        if (p.distanceSquaredTo(new Point2D(x.point.x(), p.y())) < p.distanceSquaredTo(closest))
          closest = nearest(p, x.left, closest);
      }
    } else {
      if (p.y() < x.point.y()) {
        closest = nearest(p, x.left, closest);
        if (p.distanceSquaredTo(new Point2D(p.x(), x.point.y())) < p.distanceSquaredTo(closest))
          closest = nearest(p, x.right, closest);
      } else {
        closest = nearest(p, x.right, closest);
        if (p.distanceSquaredTo(new Point2D(p.x(), x.point.y())) < p.distanceSquaredTo(closest))
          closest = nearest(p, x.left, closest);
      }
    }

    return closest;
  }

  public static void main(String[] args) {
    KdTree tree = new KdTree();
    tree.insert(new Point2D(0.1, 0.1));
    tree.insert(new Point2D(0.2, 0.2));
    tree.insert(new Point2D(0.3, 0.3));
    tree.insert(new Point2D(0.4, 0.4));
    tree.insert(new Point2D(0.5, 0.5));
    tree.insert(new Point2D(0.1, 0.1));
  }

  private static class Node {
    private Point2D point;
    private boolean vertical;
    private int size;
    private Node left, right;

    private Node(Point2D point, boolean vertical, int size) {
      this.point = point;
      this.vertical = vertical;
      this.size = size;
    }
  }
}