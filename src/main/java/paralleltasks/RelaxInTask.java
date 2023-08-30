package paralleltasks;

import cse332.graph.GraphUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class RelaxInTask extends RecursiveAction {

    public static final ForkJoinPool pool = new ForkJoinPool();
    public static final int CUTOFF = 1;
    final int lo, hi;
    private final List<Map<Integer, Integer>> g;
    private final int[] dist;
    private final int[] distCopy;
    private final int[] pred; // need array references

    public RelaxInTask(List<Map<Integer, Integer>> g, int[] dist, int[] distCopy, int[] pred, int lo, int hi) {
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
            RelaxInTask left = new RelaxInTask(g, dist, distCopy, pred, lo, mid);
            RelaxInTask right = new RelaxInTask(g, dist, distCopy, pred, mid, hi);
            left.fork();
            right.compute();
            left.join();
        }
    }

    public static void sequential(List<Map<Integer, Integer>> g, int[] dist, int[] distCopy, int[] pred, int lo, int hi) {
        for (int w = lo; w < hi; w++) {
            // get edges map associated with this vertex
            Map<Integer, Integer> edges = g.get(w);

            // look at every source vertex w
            for (Integer v : edges.keySet()) {

                if (distCopy[v] == GraphUtil.INF) continue;

                // cost of edge from (v, w)
                int cost = edges.get(v);

                if (distCopy[v] + cost < dist[w]) {
                    // a shorter path to w was found
                    dist[w] = distCopy[v] + cost;
                    pred[w] = v;
                }
            }
        }
    }

    public static void parallel(List<Map<Integer, Integer>> g, int[] dist, int[] distCopy, int[] pred) {
        pool.invoke(new RelaxInTask(g, dist, distCopy, pred, 0, g.size()));
    }

}
