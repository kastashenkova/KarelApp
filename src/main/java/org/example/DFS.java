package org.example;

import java.util.*;

class DFS {
    private final boolean[] marked;
    private final int[] edgeTo;
    private final int s;

    private int targetV = -1;
    private int counter = 0;

    private final List<Integer> explorationOrder = new ArrayList<>();
    private final Set<Integer> deadEnds = new HashSet<>();

    public DFS(Graph G, int s, int target) {
        this.s = s;
        this.targetV = target;
        edgeTo = new int[G.getV()];
        marked = new boolean[G.getV()];
        dfs(G, s, target);
    }

    private void dfs(Graph G, int s, int target) {
        Stack<Integer> stack = new Stack<>(); // LIFO
        Stack<Integer> pathStack = new Stack<>(); // save current path

        stack.push(s);
        pathStack.push(s);

        while (!stack.isEmpty()) {
            int v = stack.pop();

            if (!marked[v]) {
                marked[v] = true;
                counter++;

                while (!pathStack.isEmpty() && !isAdj(G, pathStack.peek(), v)) {
                    int backtrack = pathStack.pop();
                    if (!pathStack.isEmpty()) {
                        explorationOrder.add(backtrack);
                        deadEnds.add(backtrack);
                    }
                }

                pathStack.push(v);
                explorationOrder.add(v);

                if (target != -1 && v == target) {
                    break;
                }

                List<Integer> nbrs = new ArrayList<>();
                for (int w : G.adj(v)) {
                    nbrs.add(w);
                }
                nbrs.sort(Collections.reverseOrder()); // sorting nodes in their numbers' ascending order

                boolean hasUnmarkedNbrs = false;
                for (int w : nbrs) {
                    if (!marked[w]) {
                        edgeTo[w] = v;
                        stack.push(w);
                        hasUnmarkedNbrs = true;
                    }
                }

                // detect "dead end" in the labyringth
                if (!hasUnmarkedNbrs && v != s) {
                    deadEnds.add(v);
                }
            }
        }
    }

    private boolean isAdj(Graph G, int v, int w) {
        if (v == w) return true;
        for (int nbr : G.adj(v)) {
            if (nbr == w) return true;
        }
        return false;
    }

    public boolean hasPathTo(int v) {
        return marked[v];
    }

    public Iterable<Integer> pathTo(int v) {
        if (!hasPathTo(v)) return null;
        LinkedList<Integer> path = new LinkedList<>();
        for (int x = v; x != s; x = edgeTo[x])
            path.addFirst(x);
        path.addFirst(s);
        return path;
    }

    public int pathLength(int v) {
        if (!hasPathTo(v)) return -1;
        int length = 0;
        for (int x = v; x != s; x = edgeTo[x]) {
            length++;
        }
        return length;
    }

    public int getCounter() {
        return counter;
    }

    public List<Integer> getExplorationOrder() {
        return explorationOrder;
    }

    public Set<Integer> getDeadEnds() {
        return deadEnds;
    }

    public int getTargetV() {
        return targetV;
    }

    public void setTargetV(int targetV) {
        this.targetV = targetV;
    }
}