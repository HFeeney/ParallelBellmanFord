package paralleltasks;

import cse332.graph.GraphUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class RelaxOutTaskBad extends RecursiveAction {

    public static final ForkJoinPool pool = new ForkJoinPool();
    public static final int CUTOFF = 1;

    // needs reference to graph
    private final List<Map<Integer, Integer>> g;
    private final int[] dist;
    private final int[] distCopy;
    private final int[] pred; // need array references
    private final int lo;
    private final int hi; // start/end indices of List range


    public RelaxOutTaskBad(List<Map<Integer, Integer>> g, int[] dist, int[] distCopy, int[] pred, int lo, int hi) {
        this.g = g;
        this.dist = dist;
        this.distCopy = distCopy;
        this.pred = pred;
        this.lo = lo;
        this.hi = hi;
    }

    protected void compute() {
        if (hi - lo <= CUTOFF) {
            sequential(g, dist, distCopy, pred, lo, hi);
        } else {
            int mid = lo + (hi - lo) / 2;
            RelaxOutTaskBad left = new RelaxOutTaskBad(g, dist, distCopy, pred, lo, mid);
            RelaxOutTaskBad right = new RelaxOutTaskBad(g, dist, distCopy, pred, mid, hi);
            left.fork();
            right.compute();
            left.join();
        }
    }

    public static void sequential(List<Map<Integer, Integer>> g, int[] dist, int[] distCopy, int[] pred, int lo, int hi) {
        for (int v = lo; v < hi; v++) {
            if (distCopy[v] == GraphUtil.INF) continue;
            // get edges map associated with this vertex
            Map<Integer, Integer> edges = g.get(v);

            // look at every destination vertex w
            for (Integer w : edges.keySet()) {
                // cost of edge from (v, w)
                int cost = edges.get(w);
                if (distCopy[v] + cost < dist[w]) {
                    // a shorter path to w was found
                    dist[w] = distCopy[v] + cost;
                    pred[w] = v;
                }
            }
        }
    }

    public static void parallel(List<Map<Integer, Integer>> g, int[] dist, int[] distCopy, int[] pred) {
        pool.invoke(new RelaxOutTaskBad(g, dist, distCopy, pred, 0, g.size()));
    }

}
