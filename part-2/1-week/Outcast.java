import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdOut;

public class Outcast {
  private WordNet wordnet;

  public Outcast(WordNet wordnet) {
    this.wordnet = wordnet;
  }

  // given an array of WordNet nouns, return an outcast
  public String outcast(String[] nouns) {
    int max = -1;
    String outcast = null;

    for (String a : nouns) {
      int dist = 0;

      for (String b : nouns)
        dist += wordnet.distance(a, b);

      if (dist > max) {
        max = dist;
        outcast = a;
      }
    }

    return outcast;
  }

  // synset file, hypernym file, outcast file(s)
  // print outcast in each file
  public static void main(String[] args) {
    WordNet wordnet = new WordNet(args[0], args[1]);
    Outcast outcast = new Outcast(wordnet);
    for (int t = 2; t < args.length; t++) {
      In in = new In(args[t]);
      String[] nouns = in.readAllStrings();
      StdOut.println(args[t] + ": " + outcast.outcast(nouns));
    }
  }
}