package solvers;

import cse332.graph.GraphUtil;
import cse332.interfaces.BellmanFordSolver;
import main.Parser;
import paralleltasks.ArrayCopyTask;
import paralleltasks.RelaxOutTaskLock;

import java.util.List;
import java.util.Map;

public class OutParallelLock implements BellmanFordSolver {

    public List<Integer> solve(int[][] adjMatrix, int source) {
        List<Map<Integer, Integer>> g = Parser.parse(adjMatrix);

        int numVertices = g.size();
        int[] dist = new int[numVertices];
        int[] pred = new int[numVertices];
        for (int i = 0; i < numVertices; i++) {
            dist[i] = GraphUtil.INF;
            pred[i] = -1;
        }
        dist[source] = 0;

        // main algorithm
        int[] distCopy = new int[numVertices];
        for (int i = 0; i < numVertices; i++) {
            // copy distances to distCopy
            ArrayCopyTask.copy(dist, distCopy);

            // iterate through every source vertex v
            RelaxOutTaskLock.parallel(g, dist, distCopy, pred);
        }
        return GraphUtil.getCycle(pred);
    }
}
