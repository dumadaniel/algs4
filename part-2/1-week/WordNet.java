import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class WordNet {
  private Digraph wordnet;
  private SAP sap;
  private Map<String, List<Integer>> nouns = new HashMap<String, List<Integer>>();
  private List<String> synsets = new ArrayList<String>();

  // constructor takes the name of the two input files
  public WordNet(String synsetsFile, String hypernymsFile) {
    //TODO: Detect non-rooted DAG

    if (synsetsFile == null || hypernymsFile == null) 
      throw new NullPointerException();

    /**
     * READ SYNSETS FILE
     */
    In syn = new In(synsetsFile);
    int count = 0;
    while (syn.hasNextLine()) {
      String[] line = syn.readLine().split(",");

      int synset = Integer.valueOf(line[0]); //synset id
      synsets.add(line[1]);
      
      String[] synsetNouns = line[1].split(" "); //array of nouns
      for (int i = 0; i < synsetNouns.length; i++) {
        String noun = synsetNouns[i];

        if (nouns.containsKey(noun)) {
          nouns.get(noun).add(synset);
        }
        else {
          List<Integer> synsetList = new ArrayList<Integer>();
          synsetList.add(synset);
          nouns.put(noun, synsetList);
        }
      }

      count++;
    }
    syn.close();

    /**
     * READ HYPERNYMS FILE
     */
    wordnet = new Digraph(count);

    In hyp = new In(hypernymsFile);
    while (hyp.hasNextLine()) {
      String[] line = hyp.readLine().split(",");
      int hypernym = Integer.valueOf(line[0]);

      for (int i = 1; i < line.length; i++)
        wordnet.addEdge(hypernym, Integer.valueOf(line[i]));
    }
    hyp.close();

    sap = new SAP(wordnet);
  }

  // returns all WordNet nouns
  public Iterable<String> nouns() {
    return nouns.keySet();
  }

  // is the word a WordNet noun?
  public boolean isNoun(String word) {
    if (word == null) throw new NullPointerException();
    return nouns.containsKey(word);
  }

  // distance between nounA and nounB as defined below
  public int distance(String nounA, String nounB) {
    if (nounA == null || nounB == null)
      throw new NullPointerException();

    if (!isNoun(nounA) || !isNoun(nounB))
      throw new IllegalArgumentException();

    return sap.length(nouns.get(nounA), nouns.get(nounB));
  }

  // a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
  // in a shortest ancestral path (defined below)
  public String sap(String nounA, String nounB) {
    if (nounA == null || nounB == null)
      throw new NullPointerException();

    if (!isNoun(nounA) || !isNoun(nounB))
      throw new IllegalArgumentException();

    return synsets.get(sap.ancestor(nouns.get(nounA), nouns.get(nounB)));
  }

  // unit testing
  public static void main(String[] args) {
    WordNet wn = new WordNet(args[0], args[1]);
  }
}