package com.metropath.algorithm;

import com.metropath.graph.MetroGraph;
import com.metropath.model.Edge;
import com.metropath.model.Node;
import com.metropath.model.RouteResult;
import com.metropath.model.RouteStep;
import com.metropath.model.Station;

import java.util.*;

/**
 * Dijkstra's Shortest Path Algorithm Implementation.
 * 
 * DSA Concepts:
 * 1. PriorityQueue<Node>: Min-Heap used to greedily select the unvisited station
 *    with the smallest known accumulated weight (O(log V) extract-min).
 * 2. double[] cost: Stores the minimum calculated cost from source to every station.
 * 3. int[] parent: Predecessor array storing how each station was reached for path reconstruction.
 * 4. boolean[] visited: Ensures each vertex is finalized at most once.
 * 
 * Optimization Modes:
 * - TIME: Edge weight = edge.getTime() (Fastest Route)
 * - DISTANCE: Edge weight = edge.getDistance() (Shortest Physical Distance)
 * 
 * Time Complexity: O((V + E) log V)
 * Space Complexity: O(V + E)
 */
public class Dijkstra {

    public enum Mode {
        TIME,
        DISTANCE
    }

    /**
     * Executes Dijkstra's algorithm from source station to destination station
     * optimizing either for travel time or physical distance.
     */
    public static RouteResult findShortestRoute(MetroGraph graph, int sourceId, int destinationId, Mode mode) {
        int maxId = graph.getMaxStationId();

        // Min-Heap priority queue holding (stationId, accumulatedCost)
        PriorityQueue<Node> pq = new PriorityQueue<>();

        // Distance / Cost array tracking minimal cost from source
        double[] cost = new double[maxId + 1];
        Arrays.fill(cost, Double.MAX_VALUE);

        // Visited array marking finalized vertices
        boolean[] visited = new boolean[maxId + 1];

        // Parent array for backtracking the optimal route
        int[] parent = new int[maxId + 1];
        Arrays.fill(parent, -1);

        // Edge tracker: destination station -> Edge traversed to reach it
        Edge[] edgeUsed = new Edge[maxId + 1];

        // Initialize source
        cost[sourceId] = 0.0;
        pq.add(new Node(sourceId, 0.0));

        boolean reachedDestination = false;

        while (!pq.isEmpty()) {
            // Greedy choice: select vertex with smallest tentative cost
            Node current = pq.poll();
            int u = current.getStationId();

            if (visited[u]) {
                continue; // Skip if already finalized
            }
            visited[u] = true;

            // Early exit once target destination vertex is finalized
            if (u == destinationId) {
                reachedDestination = true;
                break;
            }

            // Relax all outgoing edges from vertex u
            for (Edge edge : graph.getNeighbors(u)) {
                int v = edge.getDestination();

                if (!visited[v]) {
                    double edgeWeight = (mode == Mode.TIME) ? edge.getTime() : edge.getDistance();
                    double newCost = cost[u] + edgeWeight;

                    // Relaxation step
                    if (newCost < cost[v]) {
                        cost[v] = newCost;
                        parent[v] = u;
                        edgeUsed[v] = edge;
                        pq.add(new Node(v, newCost));
                    }
                }
            }
        }

        if (!reachedDestination) {
            return null; // Target unreachable
        }

        // --- Route Reconstruction using Parent Array ---
        List<Integer> stationPath = new ArrayList<>();
        int curr = destinationId;
        while (curr != -1) {
            stationPath.add(curr);
            curr = parent[curr];
        }
        Collections.reverse(stationPath);

        String algorithmName = (mode == Mode.TIME)
                ? "Dijkstra - Travel Time"
                : "Dijkstra - Shortest Distance";

        return buildRouteResult(graph, stationPath, edgeUsed, algorithmName);
    }

    /**
     * Reconstructs human-readable route information and metadata.
     */
    private static RouteResult buildRouteResult(MetroGraph graph, List<Integer> stationPath, Edge[] edgeUsed, String algorithmName) {
        List<String> stationNames = new ArrayList<>();
        List<String> linesUsed = new ArrayList<>();
        List<RouteStep> routeSteps = new ArrayList<>();

        double totalDistance = 0.0;
        int totalTime = 0;
        int interchanges = 0;
        String previousLine = null;

        for (int i = 0; i < stationPath.size(); i++) {
            int stationId = stationPath.get(i);
            Station station = graph.getStation(stationId);
            stationNames.add(station.getName());

            String currentLine = null;
            boolean isInterchangeAtThisStep = false;

            if (i > 0) {
                Edge edge = edgeUsed[stationId];
                if (edge != null) {
                    totalDistance += edge.getDistance();
                    totalTime += edge.getTime();
                    currentLine = edge.getLine();

                    if (!linesUsed.contains(currentLine)) {
                        linesUsed.add(currentLine);
                    }

                    if (previousLine != null && !previousLine.equalsIgnoreCase(currentLine)) {
                        interchanges++;
                        isInterchangeAtThisStep = true;
                    }
                    previousLine = currentLine;
                }
            } else {
                if (stationPath.size() > 1) {
                    Edge firstEdge = edgeUsed[stationPath.get(1)];
                    currentLine = (firstEdge != null) ? firstEdge.getLine() : station.getLine();
                    if (!linesUsed.contains(currentLine)) {
                        linesUsed.add(currentLine);
                    }
                    previousLine = currentLine;
                } else {
                    currentLine = station.getLine();
                    linesUsed.add(currentLine);
                }
            }

            routeSteps.add(new RouteStep(
                    station.getId(),
                    station.getName(),
                    currentLine != null ? currentLine : station.getLine(),
                    Math.round(totalDistance * 10.0) / 10.0,
                    totalTime,
                    isInterchangeAtThisStep,
                    station.getLatitude(),
                    station.getLongitude()
            ));
        }

        int stops = Math.max(0, stationNames.size() - 1);
        double roundedDistance = Math.round(totalDistance * 10.0) / 10.0;

        return new RouteResult(
                stationNames.get(0),
                stationNames.get(stationNames.size() - 1),
                stationNames,
                stops,
                roundedDistance,
                totalTime,
                interchanges,
                linesUsed,
                algorithmName,
                routeSteps
        );
    }
}
