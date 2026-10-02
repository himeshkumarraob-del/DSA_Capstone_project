package com.metropath.algorithm;

import com.metropath.graph.MetroGraph;
import com.metropath.model.AlgorithmEvent;
import com.metropath.model.Edge;
import com.metropath.model.QueueItem;
import com.metropath.model.RouteResult;
import com.metropath.model.RouteStep;
import com.metropath.model.Station;
import com.metropath.model.VisualizationResult;

import java.util.*;

/**
 * Breadth-First Search (BFS) Implementation.
 * 
 * DSA Concepts:
 * 1. Queue<Integer>: FIFO structure to explore stations level-by-level (unweighted shortest path).
 * 2. boolean[] visited: Prevents cyclic traversals and redundant station checks.
 * 3. int[] parent: Predecessor array storing how each station was reached for path reconstruction.
 * 4. int[] level: Tracks hop distance / tree depth from source.
 * 
 * Objective:
 * Guarantees finding the path with the FEWEST STOPS between source and destination stations.
 * Time Complexity: O(V + E)
 * Space Complexity: O(V)
 */
public class BFS {

    /**
     * Standard execution: Finds fewest stops route and returns RouteResult.
     */
    public static RouteResult findFewestStopsRoute(MetroGraph graph, int sourceId, int destinationId) {
        VisualizationResult visResult = executeBFS(graph, sourceId, destinationId, false);
        return visResult != null ? visResult.getRouteResult() : null;
    }

    /**
     * Visualization execution: Executes the exact same algorithm and records step-by-step events.
     */
    public static VisualizationResult findFewestStopsRouteWithVisualization(MetroGraph graph, int sourceId, int destinationId) {
        return executeBFS(graph, sourceId, destinationId, true);
    }

    /**
     * Single core implementation of BFS algorithm.
     * When recordEvents is true, emits detailed AlgorithmEvent frames at each algorithmic state transition.
     */
    private static VisualizationResult executeBFS(MetroGraph graph, int sourceId, int destinationId, boolean recordEvents) {
        int maxId = graph.getMaxStationId();

        // Queue for level-by-level BFS traversal
        Queue<Integer> queue = new LinkedList<>();

        // Visited array to track explored stations
        boolean[] visited = new boolean[maxId + 1];

        // Parent / Predecessor array for route reconstruction
        int[] parent = new int[maxId + 1];
        Arrays.fill(parent, -1);

        // Level tracking for BFS depth
        int[] level = new int[maxId + 1];
        Arrays.fill(level, -1);

        // Edge tracker: destination station -> Edge traversed to reach it
        Edge[] edgeUsed = new Edge[maxId + 1];

        // Event list for visualization
        List<AlgorithmEvent> events = new ArrayList<>();
        int stepCounter = 1;

        // Initialize BFS from source
        visited[sourceId] = true;
        level[sourceId] = 0;
        queue.add(sourceId);

        Station sourceStation = graph.getStation(sourceId);
        Station destStation = graph.getStation(destinationId);

        List<String> visitedStationNames = new ArrayList<>();
        if (sourceStation != null) {
            visitedStationNames.add(sourceStation.getName());
        }

        if (recordEvents) {
            AlgorithmEvent startEvent = new AlgorithmEvent();
            startEvent.setStep(stepCounter++);
            startEvent.setType(AlgorithmEvent.EventType.START);
            startEvent.setCurrentStationId(sourceId);
            startEvent.setCurrentStationName(sourceStation.getName());
            startEvent.setCurrentCost(0.0);
            startEvent.setCurrentLevel(0);
            startEvent.setQueueSnapshot(captureQueueSnapshot(queue, level, graph));
            startEvent.setVisitedStations(new ArrayList<>(visitedStationNames));
            startEvent.setParentMap(Collections.emptyMap());
            startEvent.setDescription("BFS started. Initialized source station '" + sourceStation.getName() + "' at Level 0 and enqueued into FIFO Queue.");
            events.add(startEvent);
        }

        boolean destinationFound = false;

        while (!queue.isEmpty()) {
            int current = queue.poll();
            Station currentStation = graph.getStation(current);
            String currentName = currentStation != null ? currentStation.getName() : "Station " + current;

            if (recordEvents) {
                AlgorithmEvent processEvent = new AlgorithmEvent();
                processEvent.setStep(stepCounter++);
                processEvent.setType(AlgorithmEvent.EventType.PROCESS_STATION);
                processEvent.setCurrentStationId(current);
                processEvent.setCurrentStationName(currentName);
                processEvent.setCurrentCost(level[current]);
                processEvent.setCurrentLevel(level[current]);
                processEvent.setQueueSnapshot(captureQueueSnapshot(queue, level, graph));
                processEvent.setVisitedStations(new ArrayList<>(visitedStationNames));
                processEvent.setParentMap(captureParentMap(parent, graph));
                processEvent.setDescription("Dequeued '" + currentName + "' at Level " + level[current] + " (FIFO order). Checking adjacent neighbor stations.");
                events.add(processEvent);
            }

            // Destination reached: stop early as BFS guarantees minimal hops
            if (current == destinationId) {
                destinationFound = true;

                if (recordEvents) {
                    AlgorithmEvent destEvent = new AlgorithmEvent();
                    destEvent.setStep(stepCounter++);
                    destEvent.setType(AlgorithmEvent.EventType.DESTINATION_REACHED);
                    destEvent.setCurrentStationId(current);
                    destEvent.setCurrentStationName(currentName);
                    destEvent.setCurrentCost(level[current]);
                    destEvent.setCurrentLevel(level[current]);
                    destEvent.setQueueSnapshot(captureQueueSnapshot(queue, level, graph));
                    destEvent.setVisitedStations(new ArrayList<>(visitedStationNames));
                    destEvent.setParentMap(captureParentMap(parent, graph));
                    destEvent.setDescription("Destination station '" + destStation.getName() + "' reached at Level " + level[current] + " (" + level[current] + " hops)! BFS ensures minimum stops.");
                    events.add(destEvent);
                }
                break;
            }

            // Traverse all adjacent stations (neighbors)
            for (Edge edge : graph.getNeighbors(current)) {
                int neighbor = edge.getDestination();
                Station neighborStation = graph.getStation(neighbor);
                String neighborName = neighborStation != null ? neighborStation.getName() : "Station " + neighbor;

                if (!visited[neighbor]) {
                    visited[neighbor] = true;
                    parent[neighbor] = current;       // Track predecessor
                    level[neighbor] = level[current] + 1;
                    edgeUsed[neighbor] = edge;        // Track edge used
                    queue.add(neighbor);              // Enqueue for next level
                    if (neighborStation != null) {
                        visitedStationNames.add(neighborStation.getName());
                    }

                    if (recordEvents) {
                        AlgorithmEvent enqueueEvent = new AlgorithmEvent();
                        enqueueEvent.setStep(stepCounter++);
                        enqueueEvent.setType(AlgorithmEvent.EventType.ENQUEUE);
                        enqueueEvent.setCurrentStationId(current);
                        enqueueEvent.setCurrentStationName(currentName);
                        enqueueEvent.setCurrentCost(level[current]);
                        enqueueEvent.setCurrentLevel(level[current]);
                        enqueueEvent.setNeighborStationId(neighbor);
                        enqueueEvent.setNeighborStationName(neighborName);
                        enqueueEvent.setEdgeWeight((double) edge.getTime());
                        enqueueEvent.setEdgeLine(edge.getLine());
                        enqueueEvent.setNewCost((double) level[neighbor]);
                        enqueueEvent.setQueueSnapshot(captureQueueSnapshot(queue, level, graph));
                        enqueueEvent.setVisitedStations(new ArrayList<>(visitedStationNames));
                        enqueueEvent.setParentMap(captureParentMap(parent, graph));
                        enqueueEvent.setDescription("Discovered unvisited neighbor: " + currentName + " ➔ " + neighborName + " (" + edge.getLine() + "). Set parent[" + neighborName + "] = " + currentName + " and added to Queue at Level " + level[neighbor] + ".");
                        events.add(enqueueEvent);
                    }
                } else {
                    if (recordEvents) {
                        AlgorithmEvent visitedEvent = new AlgorithmEvent();
                        visitedEvent.setStep(stepCounter++);
                        visitedEvent.setType(AlgorithmEvent.EventType.ALREADY_VISITED);
                        visitedEvent.setCurrentStationId(current);
                        visitedEvent.setCurrentStationName(currentName);
                        visitedEvent.setCurrentCost(level[current]);
                        visitedEvent.setCurrentLevel(level[current]);
                        visitedEvent.setNeighborStationId(neighbor);
                        visitedEvent.setNeighborStationName(neighborName);
                        visitedEvent.setEdgeWeight((double) edge.getTime());
                        visitedEvent.setEdgeLine(edge.getLine());
                        visitedEvent.setQueueSnapshot(captureQueueSnapshot(queue, level, graph));
                        visitedEvent.setVisitedStations(new ArrayList<>(visitedStationNames));
                        visitedEvent.setParentMap(captureParentMap(parent, graph));
                        visitedEvent.setDescription("Neighbor '" + neighborName + "' is already visited/enqueued. Skipped to prevent cycles.");
                        events.add(visitedEvent);
                    }
                }
            }
        }

        if (!destinationFound) {
            return null; // No route exists
        }

        // --- Route Reconstruction using Parent Array ---
        List<Integer> stationPath = new ArrayList<>();
        List<String> backtrackNames = new ArrayList<>();
        int curr = destinationId;
        while (curr != -1) {
            stationPath.add(curr);
            Station st = graph.getStation(curr);
            backtrackNames.add(st != null ? st.getName() : "Station " + curr);
            curr = parent[curr];
        }

        if (recordEvents) {
            // Emit backtracking steps
            for (int i = 0; i < stationPath.size(); i++) {
                int stId = stationPath.get(i);
                Station st = graph.getStation(stId);
                int pId = parent[stId];
                Station pSt = (pId != -1) ? graph.getStation(pId) : null;

                AlgorithmEvent backtrackEvent = new AlgorithmEvent();
                backtrackEvent.setStep(stepCounter++);
                backtrackEvent.setType(AlgorithmEvent.EventType.BACKTRACK_STEP);
                backtrackEvent.setCurrentStationId(stId);
                backtrackEvent.setCurrentStationName(st.getName());
                backtrackEvent.setCurrentLevel(level[stId]);
                backtrackEvent.setCurrentCost(level[stId]);
                backtrackEvent.setCurrentPathSnapshot(new ArrayList<>(backtrackNames.subList(0, i + 1)));
                backtrackEvent.setParentMap(captureParentMap(parent, graph));
                backtrackEvent.setDescription(pSt != null
                        ? "Backtracking: At '" + st.getName() + "' ➔ Following predecessor pointer to '" + pSt.getName() + "'"
                        : "Backtracking complete: Reached source station '" + st.getName() + "'!");
                events.add(backtrackEvent);
            }
        }

        Collections.reverse(stationPath);
        List<String> finalPathNames = new ArrayList<>();
        for (int id : stationPath) {
            Station st = graph.getStation(id);
            finalPathNames.add(st != null ? st.getName() : "Station " + id);
        }

        // Compile RouteResult details
        RouteResult routeResult = buildRouteResult(graph, stationPath, edgeUsed, "BFS - Fewest Stops");

        if (recordEvents) {
            AlgorithmEvent finalEvent = new AlgorithmEvent();
            finalEvent.setStep(stepCounter++);
            finalEvent.setType(AlgorithmEvent.EventType.FINAL_PATH_READY);
            finalEvent.setCurrentStationId(destinationId);
            finalEvent.setCurrentStationName(destStation.getName());
            finalEvent.setCurrentLevel(level[destinationId]);
            finalEvent.setCurrentCost(level[destinationId]);
            finalEvent.setCurrentPathSnapshot(finalPathNames);
            finalEvent.setVisitedStations(new ArrayList<>(visitedStationNames));
            finalEvent.setParentMap(captureParentMap(parent, graph));
            finalEvent.setDescription("Final fewest-stops route discovered: " + String.join(" ➔ ", finalPathNames) + " (" + routeResult.getStops() + " stops, " + routeResult.getTime() + " min, " + routeResult.getDistance() + " km).");
            events.add(finalEvent);
        }

        return new VisualizationResult(
                "BFS",
                "Fewest Stops",
                "stops",
                sourceStation.getName(),
                destStation.getName(),
                events.size(),
                events,
                backtrackNames,
                finalPathNames,
                routeResult,
                "O(V + E)",
                "O(V)"
        );
    }

    private static List<QueueItem> captureQueueSnapshot(Queue<Integer> queue, int[] level, MetroGraph graph) {
        List<QueueItem> list = new ArrayList<>();
        for (int id : queue) {
            Station st = graph.getStation(id);
            list.add(new QueueItem(
                    id,
                    st != null ? st.getName() : "Station " + id,
                    level[id],
                    level[id],
                    st != null ? st.getLine() : "Unknown"
            ));
        }
        return list;
    }

    private static Map<String, String> captureParentMap(int[] parent, MetroGraph graph) {
        Map<String, String> map = new LinkedHashMap<>();
        for (int i = 1; i < parent.length; i++) {
            if (parent[i] != -1) {
                Station child = graph.getStation(i);
                Station par = graph.getStation(parent[i]);
                if (child != null && par != null) {
                    map.put(child.getName(), par.getName());
                }
            }
        }
        return map;
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
