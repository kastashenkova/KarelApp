package org.example;

import java.util.*;

class BFS {
    private final boolean[] marked;
    private final int[] edgeTo;
    private final int[] distTo;
    private final int s;
    private int targetV = -1;

    private int counter = 0;

    private final List<Integer> explorationOrder = new ArrayList<>();

    public BFS(Graph G, int s, int target) {
        this.s = s;
        this.targetV = target;
        marked = new boolean[G.getV()];
        edgeTo = new int[G.getV()];
        distTo = new int[G.getV()];

        for (int v = 0; v < G.getV(); v++) {
            distTo[v] = Integer.MAX_VALUE;
        }

        bfs(G, s, target);
    }

    private void bfs(Graph G, int s, int target) {
        Queue<Integer> queue = new LinkedList<>(); // FIFO
        marked[s] = true;
        distTo[s] = 0;
        counter++;
        explorationOrder.add(s);
        queue.offer(s);

        if (target != -1 && s == target) {
            return;
        }

        while (!queue.isEmpty()) {
            int v = queue.poll();

            List<Integer> nbrs = new ArrayList<>();
            for (int w : G.adj(v)) {
                nbrs.add(w);
            }
            Collections.sort(nbrs); // sorting nodes in their numbers' ascending order

            for (int w : nbrs) {
                if (!marked[w]) {
                    marked[w] = true;
                    counter++;
                    explorationOrder.add(w);
                    edgeTo[w] = v;
                    distTo[w] = distTo[v] + 1;
                    queue.offer(w);

                    if (target != -1 && w == target) {
                        return;
                    }
                }
            }
        }
    }

    public boolean hasPathTo(int v) {
        return marked[v];
    }

    public Iterable<Integer> pathTo(int v) {
        if (!hasPathTo(v)) return null;
        LinkedList<Integer> path = new LinkedList<>();
        for (int x = v; x != s; x = edgeTo[x]) {
            path.addFirst(x);
        }
        path.addFirst(s);
        return path;
    }

    public int pathLength(int v) {
        if (!hasPathTo(v)) return -1;
        return distTo[v];
    }

    public int getCounter() {
        return counter;
    }

    public List<Integer> getExplorationOrder() {
        return explorationOrder;
    }

    public Set<Integer> getDeadEnds() {
        return new HashSet<>();
    }

    public int getTargetV() {
        return targetV;
    }

    public void setTargetV(int targetV) {
        this.targetV = targetV;
    }
}