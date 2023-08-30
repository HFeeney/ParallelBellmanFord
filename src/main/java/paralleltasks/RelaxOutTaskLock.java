package paralleltasks;

import cse332.graph.GraphUtil;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.locks.ReentrantLock;

public class RelaxOutTaskLock extends RecursiveAction {

    public static final ForkJoinPool pool = new ForkJoinPool();
    public static final int CUTOFF = 1;
    private final int lo;
    private final int hi;
    private final List<Map<Integer, Integer>> g;
    private final int[] dist;
    private final int[] distCopy;
    private final int[] pred;
    private final ReentrantLock[] distLocks;

    public RelaxOutTaskLock(List<Map<Integer, Integer>> g, int[] dist, int[] distCopy, int[] pred,
                            int lo, int hi, ReentrantLock[] distLocks) {
        this.g = g;
        this.dist = dist;
        this.distCopy = distCopy;
        this.pred = pred;
        this.lo = lo;
        this.hi = hi;
        this.distLocks = distLocks;
    }

    protected void compute() {
        if (hi - lo <= CUTOFF) {
            sequential(g, dist, distCopy, pred, lo, hi, distLocks);
        } else {
            int mid = lo + (hi - lo) / 2;
            RelaxOutTaskLock left = new RelaxOutTaskLock(g, dist, distCopy, pred, lo, mid, distLocks);
            RelaxOutTaskLock right = new RelaxOutTaskLock(g, dist, distCopy, pred, mid, hi, distLocks);
            left.fork();
            right.compute();
            left.join();
        }
    }

    public static void sequential(List<Map<Integer, Integer>> g, int[] dist, int[] distCopy, int[] pred,
                                  int lo, int hi, ReentrantLock[] distLocks) {
        // iterate through every source vertex v
        for (int v = lo; v < hi; v++) {
            if (distCopy[v] == GraphUtil.INF) continue;
            // get edges map associated with this vertex
            Map<Integer, Integer> edges = g.get(v);

            // look at every destination vertex w
            for (Integer w : edges.keySet()) {
                // cost of edge from (v, w)
                int cost = edges.get(w);

                distLocks[w].lock();
                if (distCopy[v] + cost < dist[w]) {
                    // a shorter path to w was found
                    dist[w] = distCopy[v] + cost;
                    pred[w] = v;
                }
                distLocks[w].unlock();
            }
        }
    }

    public static void parallel(List<Map<Integer, Integer>> g, int[] dist, int[] distCopy, int[] pred) {
        ReentrantLock[] distLocks = new ReentrantLock[dist.length];
        for (int i = 0; i < distLocks.length; i++) {
            distLocks[i] = new ReentrantLock();
        }
        pool.invoke(new RelaxOutTaskLock(g, dist, distCopy, pred, 0, g.size(), distLocks));
    }
}
