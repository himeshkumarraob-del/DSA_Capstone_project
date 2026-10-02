package com.metropath.algorithm;

import com.metropath.graph.MetroGraph;
import com.metropath.model.Edge;
import com.metropath.model.RouteResult;
import com.metropath.model.RouteStep;
import com.metropath.model.Station;

import java.util.*;

/**
 * Breadth-First Search (BFS) Implementation.
 * 
 * DSA Concepts:
 * 1. Queue<Integer>: FIFO structure to explore stations level-by-level (unweighted shortest path).
 * 2. boolean[] visited: Prevents cyclic traversals and redundant station checks.
 * 3. int[] parent: Predecessor array storing how each station was reached for path reconstruction.
 * 
 * Objective:
 * Guarantees finding the path with the FEWEST STOPS between source and destination stations.
 * Time Complexity: O(V + E)
 * Space Complexity: O(V)
 */
public class BFS {

    /**
     * Executes BFS to find the route with the minimum number of station hops (fewest stops).
     */
    public static RouteResult findFewestStopsRoute(MetroGraph graph, int sourceId, int destinationId) {
        int maxId = graph.getMaxStationId();

        // Queue for level-by-level BFS traversal
        Queue<Integer> queue = new LinkedList<>();

        // Visited array to track explored stations
        boolean[] visited = new boolean[maxId + 1];

        // Parent / Predecessor array for route reconstruction
        int[] parent = new int[maxId + 1];
        Arrays.fill(parent, -1);

        // Edge tracker: destination station -> Edge traversed to reach it
        Edge[] edgeUsed = new Edge[maxId + 1];

        // Initialize BFS from source
        visited[sourceId] = true;
        queue.add(sourceId);

        boolean destinationFound = false;

        while (!queue.isEmpty()) {
            int current = queue.poll();

            // Destination reached: stop early as BFS guarantees minimal hops
            if (current == destinationId) {
                destinationFound = true;
                break;
            }

            // Traverse all adjacent stations (neighbors)
            for (Edge edge : graph.getNeighbors(current)) {
                int neighbor = edge.getDestination();

                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    parent[neighbor] = current;       // Track predecessor
                    edgeUsed[neighbor] = edge;        // Track edge used
                    queue.add(neighbor);              // Enqueue for next level
                }
            }
        }

        if (!destinationFound) {
            return null; // No route exists
        }

        // --- Route Reconstruction using Parent Array ---
        List<Integer> stationPath = new ArrayList<>();
        int curr = destinationId;
        while (curr != -1) {
            stationPath.add(curr);
            curr = parent[curr];
        }
        Collections.reverse(stationPath);

        // Compile RouteResult details
        return buildRouteResult(graph, stationPath, edgeUsed, "BFS - Fewest Stops");
    }

    /**
     * Builds RouteResult metadata and RouteStep list from the reconstructed path.
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
                // For starting station, line will be determined by next edge
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
