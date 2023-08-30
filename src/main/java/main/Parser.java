package main;

import cse332.graph.GraphUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser {

    /**
     * Parse an adjacency matrix into an adjacency list.
     * @param adjMatrix Adjacency matrix
     * @return Adjacency list of maps from node to weight
     */
    public static List<Map<Integer, Integer>> parse(int[][] adjMatrix) {
        List<Map<Integer, Integer>> adjList = new ArrayList<>();
        for (int i = 0; i < adjMatrix.length; i++) {
            Map<Integer, Integer> edges = new HashMap<>();
            for (int j = 0; j < adjMatrix[i].length; j++) {
                if (adjMatrix[i][j] != GraphUtil.INF) {
                    edges.put(j, adjMatrix[i][j]);
                }
            }
            adjList.add(i, edges);
        }
        return adjList;
    }

    /**
     * Parse an adjacency matrix into an adjacency list with incoming edges instead of outgoing edges.
     * @param adjMatrix Adjacency matrix
     * @return Adjacency list of maps from node to weight with incoming edges
     */
    public static List<Map<Integer, Integer>> parseInverse(int[][] adjMatrix) {
        List<Map<Integer, Integer>> adjList = new ArrayList<>();
        for (int i = 0; i < adjMatrix.length; i++) { // iterate through destinations
            Map<Integer, Integer> edges = new HashMap<>(); // dst : weight
            for (int j = 0; j < adjMatrix.length; j++) { // iterate through sources
                if (adjMatrix[j][i] != GraphUtil.INF) { // if edge exists
                    edges.put(j, adjMatrix[j][i]); // the given source leads to this node with given weight
                }
            }
            adjList.add(i, edges);
        }
        return adjList;
    }

}
