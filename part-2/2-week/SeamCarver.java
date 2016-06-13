import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.StdOut;

import java.awt.Color;
import java.util.Arrays;

public class SeamCarver {
  private int[][] colors;
  private double[][] energies;
  private int width, height;

  /*************
   *
   * Seam
   *
   * [ ][ ][x][ ][ ][ ][ ]
   * [ ][ ][ ][x][ ][ ][ ]
   * [ ][ ][ ][ ][x][ ][ ]
   * [ ][ ][ ][ ][ ][x][ ]
   * [ ][ ][ ][ ][x][ ][ ]
   * [ ][ ][ ][x][ ][ ][ ]
   *
   *************/

  public SeamCarver(Picture picture) {
    if (picture == null) throw new NullPointerException();
    
    width = picture.width();
    height = picture.height();

    colors = new int[height()][width()];
    
    for (int row = 0; row < height(); row++)
      for (int col = 0; col < width(); col++)
        colors[row][col] = picture.get(col, row).getRGB();

    energies  = new double [height()][width()];
    for (int row = 0; row < height(); row++)
      for (int col = 0; col < width(); col++)
        energies[row][col] = energy(col, row);
  }

  public Picture picture() {
    Picture picture = new Picture(width(), height());

    for (int row = 0; row < height(); row++)
      for (int col = 0; col < width(); col++)
        picture.set(col, row, new Color(colors[row][col]));

    return picture;
  }

  public int width()  { return width; } //!flipped ? width : height; } // width  = col = x
  public int height() { return height; } //!flipped ? height : width; } // height = row = y

  // energy of pixel at (x,y) --> (col, row) --> [row][col]
  public double energy(int x, int y) {
    if (x < 0 || x >= width() || y < 0 || y >= height())
      throw new IndexOutOfBoundsException();

    if (x == 0|| x == width() - 1 || y == 0  || y == height() - 1)
      return 1000.0;

    Color left, right, up, down;
    left  = new Color(colors[y][x-1]);
    right = new Color(colors[y][x+1]);
    up    = new Color(colors[y-1][x]);
    down  = new Color(colors[y+1][x]);

    double xGradient = Math.pow(right.getRed() - left.getRed(), 2) + 
                       Math.pow(right.getGreen() - left.getGreen(), 2) + 
                       Math.pow(right.getBlue() - left.getBlue(), 2);
    double yGradient = Math.pow(down.getRed() - up.getRed(), 2) + 
                       Math.pow(down.getGreen() - up.getGreen(), 2) + 
                       Math.pow(down.getBlue() - up.getBlue(), 2);

    /******
     **
     ** int is 4 bytes = 32 bits
     ** color is 0-255, ie can be represented in 8 bits
     ** store blue in bits 0-7
     ** store green in bits 8-15
     ** store red in bits 16-23
     **
    int value = ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                ((b & 0xFF) << 0);
     *******/

    // int left, right, up, down;
    // left  = colors[y][x-1];
    // right = colors[y][x+1];
    // up    = colors[y-1][x];
    // down  = colors[y+1][x];

    // double xGradient = Math.pow((right >> 16) & 0xFF - (left >> 16) & 0xFF, 2) + 
    //                    Math.pow((right >> 8)  & 0xFF - (left >> 8)  & 0xFF, 2) + 
    //                    Math.pow((right >> 0)  & 0xFF - (left >> 0)  & 0xFF, 2);
    // double yGradient = Math.pow((down >> 16) & 0xFF - (up >> 16) & 0xFF, 2) + 
    //                    Math.pow((down >> 8)  & 0xFF - (up >> 8)  & 0xFF, 2) + 
    //                    Math.pow((down >> 0)  & 0xFF - (up >> 0)  & 0xFF, 2);

    return Math.sqrt(xGradient + yGradient);
  }

  // sequence of indices for vertical seam
  public int[] findVerticalSeam() {
    int[][]    pixelFrom = new int    [height()][width()];
    double[][] distTo    = new double [height()][width()];

    // initialize distTo
    for (int i = 0; i < height(); i++) {
      for (int j = 0; j < width(); j++) {
        distTo[i][j] = Double.POSITIVE_INFINITY;
      }
    }

    int champion = 0;
    double shortest = Double.POSITIVE_INFINITY;
    for (int row = 0; row < height(); row++) {
      for (int col = 0; col < width(); col++) {
        if (row == 0) {
          pixelFrom[row][col] = col;
          distTo[row][col] = energies[row][col];
        }

        if (row == height()-1) {
          if (distTo[row][col] < shortest) {
            shortest = distTo[row][col];
            champion = col;
          }
        }
        else {
          for (int adj : adj(col, row)) {
            int fromCol = col;
            int fromRow = row;
            int toCol   = adj;
            int toRow   = row+1;

            if (distTo[toRow][toCol] > distTo[fromRow][fromCol] + energies[toRow][toCol]) {
              distTo[toRow][toCol] = distTo[fromRow][fromCol] + energies[toRow][toCol];
              pixelFrom[toRow][toCol] = col;
            }
          }
        }
      }
    }

    int[] seam = new int[height()];
    int curr = champion;
    for (int h = height()-1; h >= 0; h--) {
      seam[h] = curr;
      curr = pixelFrom[h][curr];
    }

    return seam;
  }

  // find downard, adjacent pixels
  private Iterable<Integer> adj(int x, int y) {
    Bag<Integer> adj = new Bag<Integer>();

    if (y != height()-1) {
      adj.add(x);
      if (x != 0)         adj.add(x-1);
      if (x != width()-1) adj.add(x+1);
    }

    return adj;
  }

  // sequence of indices for horizontal seam
  public int[] findHorizontalSeam() {
    transpose();
    int[] seam = findVerticalSeam();
    transpose();

    return seam;
  }

  // remove vertical seam from current picture
  public void removeVerticalSeam(int[] seam) {
    if (seam == null) 
      throw new NullPointerException();

    if (seam.length != height() || width() <= 1) 
      throw new IllegalArgumentException();

    for (int i = 0; i < seam.length; i++) {
      if (seam[i] < 0 || seam[i] >= width())        throw new IllegalArgumentException();
      if (i > 0 && Math.abs(seam[i]-seam[i-1]) > 1) throw new IllegalArgumentException();

      // remove seam from colors array
      int[] newRow = new int[colors[i].length];
      System.arraycopy(colors[i], 0, newRow, 0, seam[i]);
      System.arraycopy(colors[i], seam[i]+1, newRow, seam[i], colors[i].length-seam[i]-1);
      colors[i] = newRow;

      // remove seam from energies array
      double[] energyRow = new double[energies[i].length];
      System.arraycopy(energies[i], 0, energyRow, 0, seam[i]);
      System.arraycopy(energies[i], seam[i]+1, energyRow, seam[i], energies[i].length-seam[i]-1);
      energies[i] = energyRow;
    }

    width -= 1;

    // recalculate energy for pixels to left and right of pixels in seam
    for (int i = 0; i < seam.length; i++) {
      if (seam[i] > 0)       energies[i][seam[i]-1] = energy(seam[i]-1, i);
      if (seam[i] < width()) energies[i][seam[i]] = energy(seam[i], i);
    }
  }

  // remove horizontal seam from current picture
  public void removeHorizontalSeam(int[] seam) {
    transpose();

    try {
      removeVerticalSeam(seam);
    }  
    finally {
      transpose();
    }
  }

  private void transpose() {
    int[][] colorsTranspose = new int[width()][height()];
    double[][] energiesTranspose = new double[width()][height()];

    for (int row = 0; row < height(); row++) {
      for (int col = 0; col < width(); col++) {
        colorsTranspose[col][row] = colors[row][col];
        energiesTranspose[col][row] = energies[row][col];
      }
    }

    colors = colorsTranspose;
    energies = energiesTranspose;

    int temp = width;
    width = height;
    height = temp;
  }

  public static void main(String[] args) {
    SeamCarver sc = new SeamCarver(new Picture(args[0]));

    /*int[] test = new int[3];
    test[0] = 6;
    test[1] = 1;
    test[2] = 2;
    sc.removeHorizontalSeam(test);*/

    //sc.findVerticalSeam();
  }
}