# Karel the Robot - Maze Pathfinding Visualization

A Java application that visualizes BFS and DFS algorithms through an interactive maze where Karel the robot searches for a lamp.

## Features

- **Two Search Algorithms**: Compare BFS (Breadth-First Search) and DFS (Depth-First Search)
- **Interactive Visualization**: Watch Karel explore the maze in real-time
- **Multiple Input Formats**: Matrix-based mazes, edge lists, and adjacency lists
- **Performance Metrics**: View path length and number of visited nodes

## How It Works

**BFS** - Explores level by level, guarantees shortest path, no dead ends  
**DFS** - Explores deeply along branches, shows backtracking and dead ends

### Visualization
- **Gray**: Explored nodes
- **Red/Pink**: Dead ends (DFS only)
- **Green**: Final path to the lamp

## Input Formats

### Matrix Format
```
0 1 0 1
1 1 0 1
0 0 0 1
```

### Edge List Format
```
4
6
0 1
0 2
1 3
```

### Adjacency List Format
```
0: 1, 2
1: 0, 3
2: 0, 3
```

## Usage

```bash
javac org/example/*.java
java org.example.KarelApp <input_file>
```

Click **DFS** or **BFS** button to run the algorithm and watch the animation.

## Project Structure

```
org/example/
├── KarelApp.java          # Main application
├── Graph.java             # Graph data structure
├── BFS.java               # Breadth-First Search
├── DFS.java               # Depth-First Search
├── CreateLabyrinth.java   # Maze utilities
└── Bag.java               # Adjacency list collection
```

## Dependencies

- Princeton StdDraw library (`StdDraw.java`, `In.java`)
- Java Standard Library

## Key Classes

- **Graph**: Stores vertices and edges using adjacency lists
- **BFS**: Implements breadth-first traversal with shortest path tracking
- **DFS**: Implements depth-first traversal with dead-end detection
- **CreateLabyrinth**: Converts between matrix and graph representations
- **KarelApp**: Main visualization engine and user interface

## Performance Comparison

The app displays:
- Algorithm name (BFS/DFS)
- Path length (number of steps)
- Visited nodes (total explored)

Generally: BFS finds shorter paths, DFS may explore fewer nodes initially.

## Requirements

Place `karel.png` and `lamp.png` in `src/main/resources/` directory.

## Educational Value

Perfect for:
- Understanding graph traversal algorithms
- Visualizing search strategies
- Learning about queues vs stacks
- Comparing algorithm efficiency

---

**License**: Educational project - free to use and modify.
