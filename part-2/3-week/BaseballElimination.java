import edu.princeton.cs.algs4.FlowEdge;
import edu.princeton.cs.algs4.FlowNetwork;
import edu.princeton.cs.algs4.FordFulkerson;
import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.List;
import java.util.ArrayList;

public class BaseballElimination {
  private int numTeams;
  private List<String> teams;
  private int[] wins, losses, left;
  private int[][] games;
  private List<List<String>> certificates;
  private boolean[] evaluated, eliminated;

  // create a baseball division from given filename in format specified below
  public BaseballElimination(String filename) {
    In in = new In(filename);
    numTeams = in.readInt();

    teams        = new ArrayList<String>();
    wins         = new int[numTeams];
    losses       = new int[numTeams];
    left         = new int[numTeams];
    games        = new int[numTeams][numTeams];
    evaluated    = new boolean[numTeams];
    eliminated   = new boolean[numTeams];
    certificates = new ArrayList<List<String>>();

    for (int team = 0; team < numTeams; team++) {
      teams.add(in.readString());

      wins[team]   = in.readInt();
      losses[team] = in.readInt();
      left[team]   = in.readInt();

      for (int opp = 0; opp < numTeams; opp++)
        games[team][opp] = in.readInt();

      certificates.add(null);
    }
  }

  public int numberOfTeams() {
    return numTeams;
  }

  public Iterable<String> teams() {
    return teams;
  }

  public int wins(String team) {
    if (teams.indexOf(team) == -1) throw new IllegalArgumentException();
    return wins[teams.indexOf(team)];
  }

  public int losses(String team) {
    if (teams.indexOf(team) == -1) throw new IllegalArgumentException();
    return losses[teams.indexOf(team)];
  }

  public int remaining(String team) {
    if (teams.indexOf(team) == -1) throw new IllegalArgumentException();
    return left[teams.indexOf(team)];
  }

  public int against(String team1, String team2) {
    if (teams.indexOf(team1) == -1 || teams.indexOf(team2) == -1) throw new IllegalArgumentException();
    return games[teams.indexOf(team1)][teams.indexOf(team2)];
  }

  public boolean isEliminated(String targetTeam) {
    if (teams.indexOf(targetTeam) == -1) throw new IllegalArgumentException();
    
    if (numTeams == 1) 
      return false;
    
    int team = teams.indexOf(targetTeam);
    if (evaluated(targetTeam)) 
      return eliminated(targetTeam);
    else 
      evaluated[team] = true;

    int teamMaxWins = wins[team] + left[team];

    // check for trivial elimination
    List<String> r = new ArrayList<String>();
    boolean trivialElim = false;
    for (int t = 0; t < wins.length; t++) {
      if (t != team && teamMaxWins < wins[t]) {
        r.add(teams.get(t));
        trivialElim = true;
      }
    }

    if (trivialElim) {
      certificates.add(team, r);
      eliminated[team] = true;
      return eliminated[team];
    }

    // 1 source vertex, (n choose k) "series" vertices, numTeams team vertices, 1 sink vertex
    FlowNetwork network = new FlowNetwork((int) (1 + bc(numTeams-1,2) + (numTeams-1) + 1));
    int teamsStartIndex = network.V() - numTeams;

    // connect source vertex to "series" vertices
    // connect "series" vertices to team vertices
    int series = 1;
    int totalGames = 0;
    for (int team1 = 0; team1 < games.length; team1++) {
      if (team1 == team) continue;
      int oneIndex = team1 > team ? team1-1 : team1;
      for (int team2 = 0; team2 < games[0].length; team2++) {
        if (team2 == team || team1 == team2 || team1 > team2) continue;
        int twoIndex = team2 > team ? team2-1 : team2;

        network.addEdge(new FlowEdge(0, series, games[team1][team2], 0));
        network.addEdge(new FlowEdge(series, teamsStartIndex+oneIndex, Double.POSITIVE_INFINITY, 0));
        network.addEdge(new FlowEdge(series, teamsStartIndex+twoIndex, Double.POSITIVE_INFINITY, 0));

        totalGames += games[team1][team2];
        series++;
      }
    }

    // connect team vertices to sink vertex
    for (int t = 0; t < wins.length; t++) {
      if (t == team) continue;
      int teamVertex = t > team ? t-1 : t;
      network.addEdge(new FlowEdge(teamsStartIndex + teamVertex, network.V()-1, teamMaxWins - wins[t], 0));
    }

    FordFulkerson ff = new FordFulkerson(network, 0, network.V()-1);

    if (ff.value() < totalGames) {
      for (int t = teamsStartIndex; t < network.V(); t++) {
        if (ff.inCut(t))
          r.add(teams.get((t - teamsStartIndex) >= team ? t - teamsStartIndex + 1 : t - teamsStartIndex));
      }
      certificates.add(team, r);
      eliminated[team] = true;
    }

    return eliminated[team];
  }

  // subset R of teams that eliminates given team; null if not eliminated
  public Iterable<String> certificateOfElimination(String team) {
    if (teams.indexOf(team) == -1) throw new IllegalArgumentException();
    if (!evaluated(team)) isEliminated(team);
    return certificates.get(teams.indexOf(team));
  }

  private boolean evaluated(String team) {
    return evaluated[teams.indexOf(team)];
  }

  private boolean eliminated(String team) {
    return eliminated[teams.indexOf(team)];
  }

  // calculate number of series played amongst n teams
  private int bc(int n, int k) {
    if (k>n-k)
      k=n-k;

    long b=1;
    for (int i=1, m=n; i<=k; i++, m--)
      b=b*m/i;

    return Long.valueOf(b).intValue();
  }

  public static void main(String[] args) {
    BaseballElimination division = new BaseballElimination(args[0]);
    for (String team : division.teams()) {
      if (division.isEliminated(team)) {
        StdOut.print(team + " is eliminated by the subset R = { ");
        for (String t : division.certificateOfElimination(team)) {
          StdOut.print(t + " ");
        }
        StdOut.println("}");
      }
      else {
        StdOut.println(team + " is not eliminated");
      }
    }
    
  }
}