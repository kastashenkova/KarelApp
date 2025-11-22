package org.example;

import princeton.lib.StdDraw;
import princeton.lib.In;
import java.awt.Color;
import java.awt.Font;
import java.util.*;

// REALIZATION MAINLY FROM THE LECTURE
class Graph {
    private final int V;
    private final Bag<Integer>[] adj;

    public Graph(int V){
        this.V=V;
        adj = (Bag<Integer>[]) new Bag[V];
        for (int v=0; v<V; v++)
            adj[v] = new Bag<Integer>();
    }

    public Graph(In in) {
        this.V = in.readInt();
        adj = (Bag<Integer>[]) new Bag[V];
        for (int v=0; v<V; v++)
            adj[v] = new Bag<Integer>();

        int E = in.readInt();
        for (int i = 0; i < E; i++) {
            int v = in.readInt();
            int w = in.readInt();
            addEdge(v, w);
        }
    }

    public void addEdge(int v, int w){
        adj[v].add(w);
        adj[w].add(v);
    }

    public Iterable<Integer> adj(int v){
        List<Integer> nbrs = new ArrayList<>();
        for (int w : adj[v]) {
            nbrs.add(w);
        }
        Collections.sort(nbrs);
        return nbrs;
    }

    public int degree(int v){
        int degree = 0;
        for (int w : adj(v))
            degree++;
        return degree;
    }

    public int getV() {
        return V;
    }
}

// CONNECTING GRAPH AND LABYRINTH
class CreateLabyrinth {
    private final int rows;
    private final int cols;
    private final Cells[][] cells;

    public CreateLabyrinth(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.cells = new Cells[rows][cols];
        createCells();
    }

    private void createCells() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Cells(r, c);
            }
        }
    }

    public int convertCoordsToV(int r, int c) {
        return r * cols + c;
    }

    public int[] convertVToCoords(int v) {
        return new int[]{v / cols, v % cols};
    }

    public Cells getCell(int r, int c) {
        return cells[r][c];
    }

    public int getRows() {
        return rows;
    }

    public int getCols() {
        return cols;
    }

    public static class Cells {
        final int row;
        final int col;
        boolean top = true;
        boolean right = true;
        boolean bottom = true;
        boolean left = true;

        Cells(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public boolean isAccessible() {
            return !top && !right && !bottom && !left;
        }
    }

    public static CreateLabyrinth getFromMatrix(In in) {
        List<String[]> lines = new ArrayList<>();

        while (in.hasNextLine()) {
            String line = in.readLine().trim();
            if (!line.isEmpty()) {
                lines.add(line.split("\\s+"));
            }
        }

        int rows = lines.size();
        if (rows == 0) throw new RuntimeException("Empty labyrinth file");

        int cols = lines.get(0).length;
        CreateLabyrinth lab = new CreateLabyrinth(rows, cols);

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                String val = lines.get(r)[c];
                Cells cell = lab.getCell(r, c);
                if ("0".equals(val)) {
                    cell.top = false;
                    cell.right = false;
                    cell.bottom = false;
                    cell.left = false;
                }
            }
        }
        return lab;
    }

    public static CreateLabyrinth getFromGraph(Graph graph) {
        int V = graph.getV();
        int cols = (int) Math.ceil(Math.sqrt(V));
        int rows = (int) Math.ceil((double) V / cols);

        CreateLabyrinth lab = new CreateLabyrinth(rows, cols);

        for (int v = 0; v < graph.getV(); v++) {
            if (graph.degree(v) > 0 || v == 0) {
                int[] coords = lab.convertVToCoords(v);
                int r = coords[0];
                int c = coords[1];

                Cells cell = lab.cells[r][c];
                cell.top = false;
                cell.right = false;
                cell.bottom = false;
                cell.left = false;
            }
        }

        return lab;
    }

    public static Graph getFromAdjList(In in) {
        List<String> lines = new ArrayList<>();
        int maxV = -1;

        while (in.hasNextLine()) {
            String line = in.readLine().trim();
            if (line.isEmpty()) continue;
            lines.add(line);

            String[] parts = line.split(":");
            if (parts.length != 2) continue;

            try {
                int v = Integer.parseInt(parts[0].trim());
                if (v > maxV) maxV = v;

                String[] nbrsStr = parts[1].trim().split("[,\\s]+");
                for (String wStr : nbrsStr) {
                    if (wStr.isEmpty()) continue;
                    int w = Integer.parseInt(wStr.trim());
                    if (w > maxV) maxV = w;
                }
            } catch (NumberFormatException e) {
                System.err.println("Skipping malformed line on first pass: " + line);
            }
        }

        int V = maxV + 1;
        Graph g = new Graph(V);

        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length != 2) continue;

            try {
                int v = Integer.parseInt(parts[0].trim());
                String[] neighborsStr = parts[1].trim().split("[,\\s]+");

                for (String wStr : neighborsStr) {
                    if (wStr.isEmpty()) continue;
                    int w = Integer.parseInt(wStr.trim());
                    if (v < w) {
                        g.addEdge(v, w);
                    }
                }
            } catch (NumberFormatException e) {
                System.err.println("Skipping malformed line on second pass: " + line);
            }
        }

        return g;
    }

    public Graph convertToGraph() {
        Graph graph = new Graph(rows * cols);
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (!cells[r][c].isAccessible()) continue;

                int v = convertCoordsToV(r, c);

                if (r + 1 < rows && cells[r + 1][c].isAccessible()) {
                    graph.addEdge(v, convertCoordsToV(r + 1, c));
                }
                if (c + 1 < cols && cells[r][c + 1].isAccessible()) {
                    graph.addEdge(v, convertCoordsToV(r, c + 1));
                }
            }
        }
        return graph;
    }
}

public class KarelApp {
    String robotImage = "src/main/resources/karel.png";
    String lampImage = "src/main/resources/lamp.png";

    private CreateLabyrinth labyrinth;
    private Graph graph;
    private double cellSize;
    private double yOffset;

    private List<Integer> pathToShow;
    private List<Integer> exploredNodes;
    private Set<Integer> deadEndNodes;
    private int currStep = 0;

    private int currV;
    private int lampV;
    private final int startV = 0;

    private String algorithm;
    private int pathLength = 0;
    private int markedNodes = 0;

    private boolean showResults = false;
    private boolean isExploring = false;
    private boolean foundTheLamp = false;
    private boolean hasRunAlgorithm = false;
    private boolean isGraphMode = false;

    public KarelApp(String filename) {
        try {
            In in = new In(filename);
            String firstLine = in.readLine(); // read first line to indicate the format of the test file
            if (firstLine == null) throw new RuntimeException("Empty file: " + filename);
            firstLine = firstLine.trim();
            in.close();

            in = new In(filename);

            String[] parts = firstLine.split("\\s+");

            if (firstLine.contains(":")) {
                isGraphMode = true;
                graph = CreateLabyrinth.getFromAdjList(in);
                labyrinth = CreateLabyrinth.getFromGraph(graph);
            } else if (parts.length == 1) {
                isGraphMode = true;
                graph = new Graph(in);
                labyrinth = CreateLabyrinth.getFromGraph(graph);
            } else {
                isGraphMode = false;
                labyrinth = CreateLabyrinth.getFromMatrix(in);
                graph = labyrinth.convertToGraph();
            }

            /*Random random = new Random();
            lampV = random.nextInt(graph.getV());

            while (lampV == 0 || graph.degree(lampV) == 0) {
                lampV = random.nextInt(graph.getV());
                if (graph.getV() <= 1) break;
            }*/

            lampV = 1;

            currV = startV;

            setup();

        } catch (Exception e) {
            System.err.println("Can't read the file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setup() {
        StdDraw.setCanvasSize(700, 700);
        StdDraw.setXscale(0, 700);
        StdDraw.setYscale(0, 700);

        yOffset = 100;
        double availableHeight = 700 - yOffset;
        double availableWidth = 700;

        double cellHeight = availableHeight / labyrinth.getRows();
        double cellWidth = availableWidth / labyrinth.getCols();

        cellSize = Math.min(cellHeight, cellWidth);

        StdDraw.enableDoubleBuffering();
    }

    public void useBFS() {
        hasRunAlgorithm = true;

        algorithm = "BFS";
        BFS bfs = new BFS(graph, startV, lampV);

        if (bfs.hasPathTo(lampV)) {
            pathToShow = new ArrayList<>();
            for (int v : bfs.pathTo(lampV)) {
                pathToShow.add(v);
            }

            exploredNodes = bfs.getExplorationOrder();
            deadEndNodes = new HashSet<>();

            pathLength = bfs.pathLength(lampV);
            markedNodes = bfs.getCounter();
        } else {
            pathToShow = null;
            exploredNodes = bfs.getExplorationOrder();
            deadEndNodes = new HashSet<>();
        }
    }

    public void useDFS() {
        hasRunAlgorithm = true;

        algorithm = "DFS";
        DFS dfs = new DFS(graph, startV, lampV);

        if (dfs.hasPathTo(lampV)) {
            pathToShow = new ArrayList<>();
            for (int v : dfs.pathTo(lampV)) {
                pathToShow.add(v);
            }

            exploredNodes = dfs.getExplorationOrder();
            deadEndNodes = dfs.getDeadEnds();

            pathLength = dfs.pathLength(lampV);
            markedNodes = dfs.getCounter();
        } else {
            pathToShow = null;
            exploredNodes = dfs.getExplorationOrder();
            deadEndNodes = dfs.getDeadEnds();
        }
    }

    private void showExploration() {
        isExploring = true;
        for (int i = 0; i < exploredNodes.size(); i++) {
            currV = exploredNodes.get(i);
            currStep = i + 1;
            draw();
            StdDraw.pause(60);
        }
        isExploring = false;
    }

    public void startAnimation() {
        if (pathToShow == null || pathToShow.isEmpty()) {
            showExploration();
            showResults = true;
            draw();
            return;
        }

        showResults = false;
        showExploration();

        foundTheLamp = true;
        draw();
        StdDraw.pause(2500);
        foundTheLamp = false;

        currStep = 0;
        for (int i = 0; i < pathToShow.size(); i++) {
            currV = pathToShow.get(i);
            currStep = i + 1;
            draw();
            StdDraw.pause(70);
        }

        showResults = true;
        draw();
    }

    private void drawFoundStatus() {
        StdDraw.setPenColor(StdDraw.MAGENTA);
        StdDraw.setFont(new Font("Calibre", Font.BOLD, 14));
        StdDraw.text(350, 20, "WOW!!! WE FOUND THE LAMP! Let's see the correct path towards it!");
    }

    public void draw() {
        StdDraw.clear(StdDraw.WHITE);

        if (isGraphMode) {
            drawGraphEdges();
            drawGraphNodes();
        } else {
            drawWalls();
            drawCellDots();
        }

        if (isExploring) {
            drawExploration();
        } else if (foundTheLamp) {
            drawExploration();
            drawFoundStatus();
        } else if (showResults && (pathToShow == null || pathToShow.isEmpty())) {
            StdDraw.setPenColor(StdDraw.RED);
            StdDraw.setFont(new Font("Calibre", Font.BOLD, 14));
            StdDraw.text(350, 20, "No path towards the lamp!");
            drawLamp();
            createKarel();
            createButtons();
            StdDraw.show();
            return;
        } else {
            drawCorrectPath();
        }

        drawLamp();
        createKarel();
        createButtons();

        if (isExploring) {
            drawExplorationStatus();
        } else if (foundTheLamp){
            drawFoundStatus();
        } else if (showResults) {
            drawResults();
        }

        StdDraw.show();
    }

    private void drawGraphEdges() {
        StdDraw.setPenColor(new Color(200, 200, 200));
        StdDraw.setPenRadius(0.003);

        for (int v = 0; v < graph.getV(); v++) {
            int[] coordsV = labyrinth.convertVToCoords(v);
            double x1 = coordsV[1] * cellSize + cellSize / 2;
            double y1 = yOffset + coordsV[0] * cellSize + cellSize / 2;

            for (int w : graph.adj(v)) {
                if (w > v) {
                    int[] coordsW = labyrinth.convertVToCoords(w);
                    double x2 = coordsW[1] * cellSize + cellSize / 2;
                    double y2 = yOffset + coordsW[0] * cellSize + cellSize / 2;
                    StdDraw.line(x1, y1, x2, y2);
                }
            }
        }
    }

    private void drawGraphNodes() {
        StdDraw.setPenColor(new Color(100, 100, 100));

        for (int v = 0; v < graph.getV(); v++) {
            if (graph.degree(v) > 0 || v == 0) {
                int[] coords = labyrinth.convertVToCoords(v);
                double centerX = coords[1] * cellSize + cellSize / 2;
                double centerY = yOffset + coords[0] * cellSize + cellSize / 2;
                StdDraw.filledCircle(centerX, centerY, 4);
            }
        }
    }

    private void drawWalls() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(0.003);

        for (int r = 0; r < labyrinth.getRows(); r++) {
            for (int c = 0; c < labyrinth.getCols(); c++) {
                double x = c * cellSize;
                double y = yOffset + r * cellSize;

                CreateLabyrinth.Cells cell = labyrinth.getCell(r, c);

                if (cell.top) {
                    StdDraw.line(x, y + cellSize, x + cellSize, y + cellSize);
                }
                if (cell.left) {
                    StdDraw.line(x, y, x, y + cellSize);
                }
                if (cell.bottom) {
                    StdDraw.line(x, y, x + cellSize, y);
                }
                if (cell.right) {
                    StdDraw.line(x + cellSize, y, x + cellSize, y + cellSize);
                }
            }
        }
    }

    private void drawCellDots() {
        StdDraw.setPenColor(new Color(0, 0, 0, 98));

        for (int v = 0; v < graph.getV(); v++) {
            if (graph.degree(v) > 0 || v == 0) {
                int[] coords = labyrinth.convertVToCoords(v);
                double centerX = coords[1] * cellSize + cellSize / 2;
                double centerY = yOffset + coords[0] * cellSize + cellSize / 2;
                StdDraw.filledCircle(centerX, centerY, 2);
            }
        }
    }

    private void drawExploration() {
        if (exploredNodes == null) return;

        for (int i = 0; i < currStep && i < exploredNodes.size(); i++) {
            int v = exploredNodes.get(i);
            int[] cell = labyrinth.convertVToCoords(v);
            double x = cell[1] * cellSize + cellSize / 2;
            double y = yOffset + cell[0] * cellSize + cellSize / 2;

            boolean isBacktracking = false;
            for (int j = i + 1; j < currStep && j < exploredNodes.size(); j++) {
                if (exploredNodes.get(j).equals(v)) {
                    isBacktracking = true;
                    break;
                }
            }

            if (isBacktracking && deadEndNodes.contains(v)) {
                StdDraw.setPenColor(new Color(255, 100, 100, 208));
            } else {
                StdDraw.setPenColor(new Color(166, 166, 166, 120));
            }

            StdDraw.filledRectangle(x, y, cellSize/3, cellSize/3);
        }
    }

    private void drawCorrectPath() {
        if (exploredNodes == null) return;

        for (int v : exploredNodes) {
            int[] cell = labyrinth.convertVToCoords(v);
            double x = cell[1] * cellSize + cellSize / 2;
            double y = yOffset + cell[0] * cellSize + cellSize / 2;

            if (deadEndNodes.contains(v)) {
                StdDraw.setPenColor(new Color(255, 150, 150, 150));
            } else {
                StdDraw.setPenColor(new Color(220, 220, 220, 120));
            }

            StdDraw.filledRectangle(x, y, cellSize/3, cellSize/3);

        }

        if (pathToShow == null) return;

        if (isGraphMode) {
            StdDraw.setPenColor(new Color(50, 205, 50, 220));
            StdDraw.setPenRadius(0.005);

            for (int i = 0; i < currStep && i < pathToShow.size() - 1; i++) {
                int v = pathToShow.get(i);
                int w = pathToShow.get(i + 1);

                int[] coordsV = labyrinth.convertVToCoords(v);
                int[] coordsW = labyrinth.convertVToCoords(w);

                double x1 = coordsV[1] * cellSize + cellSize / 2;
                double y1 = yOffset + coordsV[0] * cellSize + cellSize / 2;
                double x2 = coordsW[1] * cellSize + cellSize / 2;
                double y2 = yOffset + coordsW[0] * cellSize + cellSize / 2;

                StdDraw.line(x1, y1, x2, y2);
            }
        }

        StdDraw.setPenColor(new Color(50, 205, 50, 220));
        for (int i = 0; i < currStep && i < pathToShow.size(); i++) {
            int v = pathToShow.get(i);
            int[] cell = labyrinth.convertVToCoords(v);
            double x = cell[1] * cellSize + cellSize / 2;
            double y = yOffset + cell[0] * cellSize + cellSize / 2;
            StdDraw.filledRectangle(x, y, cellSize/3, cellSize/3);
        }
    }

    private void drawLamp() {
        int[] cell = labyrinth.convertVToCoords(lampV);
        double x = cell[1] * cellSize + cellSize / 2;
        double y = yOffset + cell[0] * cellSize + cellSize / 2;

        StdDraw.picture(x, y, lampImage, cellSize * 0.7, cellSize * 0.7);
    }

    private void createKarel() {
        int[] cell = labyrinth.convertVToCoords(currV);
        double x = cell[1] * cellSize + cellSize / 2;
        double y = yOffset + cell[0] * cellSize + cellSize / 2;

        StdDraw.picture(x, y, robotImage, cellSize * 0.7, cellSize * 0.7);
    }

    private void createButtons() {
        drawButton(250, "DFS");
        drawButton(450, "BFS");
    }

    private void drawButton(double x, String text) {
        StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
        StdDraw.filledRectangle(x, 60, 40, 15);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.rectangle(x, 60, 40, 15);
        StdDraw.setFont(new Font("Calibre", Font.BOLD, 14));
        StdDraw.text(x, 60, text);
    }

    private void drawResults() {
        StdDraw.setPenColor(StdDraw.GREEN);
        StdDraw.setFont(new Font("Calibre", Font.BOLD, 16));
        String results = String.format("Algorithm: %s      Path length: %d      Visited nodes: %d",
                algorithm, pathLength, markedNodes);
        StdDraw.text(350, 20, results);
    }

    private void drawExplorationStatus() {
        StdDraw.setPenColor(new Color(0, 0, 0));
        StdDraw.setFont(new Font("Calibre", Font.BOLD, 18));
        StdDraw.text(350, 20, "Exploration...");
    }

    public void run() {
        draw();
        boolean pressed = false;

        while (true) {
            if (StdDraw.isMousePressed() && !pressed) {
                pressed = true;
                double x = StdDraw.mouseX();
                double y = StdDraw.mouseY();

                if (Math.abs(x - 250) < 40 && Math.abs(y - 60) < 15) {
                    useDFS();
                    startAnimation();
                }
                else if (Math.abs(x - 450) < 40 && Math.abs(y - 60) < 15) {
                    useBFS();
                    startAnimation();
                }
            }

            if (!StdDraw.isMousePressed()) {
                pressed = false;
            }
        }
    }

    public static void main(String[] args) {
        KarelApp app = new KarelApp(args[0]);
        app.run();
    }
}