package com.example.map_proiect_extins.domain;

import java.util.ArrayList;

public class Graph {
    int V;
    ArrayList<ArrayList<Integer>> adjListArray;

    /**
     * Constructor for Graph (adjacent list)
     *
     * @param V
     */
    public Graph(int V) {
        this.V = V;
        adjListArray = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adjListArray.add(i, new ArrayList<>());
        }
    }

    /**
     * Adding edge to graph
     *
     * @param src
     * @param dest
     */
    public void addEdge(int src, int dest) {
        adjListArray.get(src).add(dest);
        adjListArray.get(dest).add(src);
    }

    /**
     * DFS algorithm
     *
     * @param v
     * @param visited
     * @param component
     * @return a connected component
     */
    private ArrayList<Integer> compDFS(int v, boolean[] visited, ArrayList<Integer> component) {
        visited[v] = true;
        component.add(v);
        for (int x : adjListArray.get(v)) {
            if (!visited[x])
                compDFS(x, visited, component);
        }
        return component;
    }


    /**
     * Determines the number of connected components
     *
     * @return the number of connected components with more than one vertex
     */
    public ArrayList<ArrayList<Integer>> connectedComponents() {
        ArrayList<ArrayList<Integer>> comp = new ArrayList<>();
        boolean[] visited = new boolean[V];
        for (int v = 0; v < V; ++v) {
            if (!visited[v]) {
                ArrayList<Integer> compSec = compDFS(v, visited, new ArrayList<Integer>());
                if (compSec.size() > 1)
                    comp.add(compSec);
            }
        }
        return comp;
    }

    /**
     * Determines the longest connected component
     *
     * @return longest connected component
     */
    public ArrayList<Integer> longestComp() {
        int maxL = 0;
        ArrayList<Integer> maxComp = new ArrayList<>();
        boolean[] visited = new boolean[V];
        for (int v = 0; v < V; ++v) {
            if (!visited[v]) {
                ArrayList<Integer> comp = compDFS(v, visited, new ArrayList<Integer>());
                if (comp.size() > maxL) {
                    maxL = comp.size();
                    maxComp = comp;
                }

            }
        }
        return maxComp;
    }
}
