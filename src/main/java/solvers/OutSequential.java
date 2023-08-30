package solvers;

import cse332.graph.GraphUtil;
import cse332.interfaces.BellmanFordSolver;
import main.Parser;

import java.util.List;
import java.util.Map;

public class OutSequential implements BellmanFordSolver {

    public List<Integer> solve(int[][] adjMatrix, int source) {
        // initialization
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
            for (int j = 0; j < numVertices; j++) {
                distCopy[j] = dist[j];
            }

            // iterate through every source vertex v
            for (int v = 0; v < numVertices; v++) {
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
        return GraphUtil.getCycle(pred);
    }

}
