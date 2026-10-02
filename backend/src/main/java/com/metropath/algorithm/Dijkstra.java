package com.metropath.algorithm;

import com.metropath.graph.MetroGraph;
import com.metropath.model.AlgorithmEvent;
import com.metropath.model.Edge;
import com.metropath.model.Node;
import com.metropath.model.QueueItem;
import com.metropath.model.RouteResult;
import com.metropath.model.RouteStep;
import com.metropath.model.Station;
import com.metropath.model.VisualizationResult;

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
 * - TIME: Edge weight = edge.getTime() in minutes (Fastest Route)
 * - DISTANCE: Edge weight = edge.getDistance() in km (Shortest Physical Distance)
 * 
 * Time Complexity: O((V + E) log V)
 * Space Complexity: O(V + E)
 * 
 * Note: Core algorithm is the single source of truth for both normal route finding
 * and step-by-step visual exploration.
 */
public class Dijkstra {

    public enum Mode {
        TIME,
        DISTANCE
    }

    /**
     * Standard execution: Finds shortest route and returns RouteResult.
     */
    public static RouteResult findShortestRoute(MetroGraph graph, int sourceId, int destinationId, Mode mode) {
        VisualizationResult visResult = executeDijkstra(graph, sourceId, destinationId, mode, false);
        return visResult != null ? visResult.getRouteResult() : null;
    }

    /**
     * Visualization execution: Executes the exact same algorithm and records step-by-step events.
     */
    public static VisualizationResult findShortestRouteWithVisualization(MetroGraph graph, int sourceId, int destinationId, Mode mode) {
        return executeDijkstra(graph, sourceId, destinationId, mode, true);
    }

    /**
     * Single core implementation of Dijkstra's algorithm.
     * When recordEvents is true, emits detailed AlgorithmEvent frames at each algorithmic state transition.
     */
    private static VisualizationResult executeDijkstra(MetroGraph graph, int sourceId, int destinationId, Mode mode, boolean recordEvents) {
        int maxId = graph.getMaxStationId();
        String weightUnit = (mode == Mode.TIME) ? "min" : "km";
        String optimizationName = (mode == Mode.TIME) ? "Travel Time" : "Shortest Distance";
        String algorithmFullName = (mode == Mode.TIME) ? "Dijkstra - Travel Time" : "Dijkstra - Shortest Distance";

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

        // Event list for visualization
        List<AlgorithmEvent> events = new ArrayList<>();
        int stepCounter = 1;

        // Initialize source
        cost[sourceId] = 0.0;
        pq.add(new Node(sourceId, 0.0));

        Station sourceStation = graph.getStation(sourceId);
        Station destStation = graph.getStation(destinationId);

        if (recordEvents) {
            AlgorithmEvent startEvent = new AlgorithmEvent();
            startEvent.setStep(stepCounter++);
            startEvent.setType(AlgorithmEvent.EventType.START);
            startEvent.setCurrentStationId(sourceId);
            startEvent.setCurrentStationName(sourceStation.getName());
            startEvent.setCurrentCost(0.0);
            startEvent.setQueueSnapshot(capturePQSnapshot(pq, graph));
            startEvent.setVisitedStations(Collections.emptyList());
            startEvent.setParentMap(Collections.emptyMap());
            startEvent.setDescription("Algorithm started. Initialized source station '" + sourceStation.getName() + "' with tentative cost 0.0 " + weightUnit + " and inserted into Min-Heap PriorityQueue.");
            events.add(startEvent);
        }

        boolean reachedDestination = false;
        List<String> visitedStationNames = new ArrayList<>();

        while (!pq.isEmpty()) {
            // Greedy choice: select vertex with smallest tentative cost
            Node current = pq.poll();
            int u = current.getStationId();

            if (visited[u]) {
                continue; // Skip if already finalized
            }
            visited[u] = true;
            Station uStation = graph.getStation(u);
            if (uStation != null) {
                visitedStationNames.add(uStation.getName());
            }

            if (recordEvents) {
                AlgorithmEvent processEvent = new AlgorithmEvent();
                processEvent.setStep(stepCounter++);
                processEvent.setType(AlgorithmEvent.EventType.PROCESS_STATION);
                processEvent.setCurrentStationId(u);
                processEvent.setCurrentStationName(uStation != null ? uStation.getName() : "Station " + u);
                processEvent.setCurrentCost(Math.round(cost[u] * 100.0) / 100.0);
                processEvent.setQueueSnapshot(capturePQSnapshot(pq, graph));
                processEvent.setVisitedStations(new ArrayList<>(visitedStationNames));
                processEvent.setParentMap(captureParentMap(parent, graph));
                processEvent.setDescription("Extracted minimum cost station '" + (uStation != null ? uStation.getName() : u) + "' from PriorityQueue (Cost: " + formatCost(cost[u], weightUnit) + "). Processing outgoing tracks.");
                events.add(processEvent);
            }

            // Early exit once target destination vertex is finalized
            if (u == destinationId) {
                reachedDestination = true;

                if (recordEvents) {
                    AlgorithmEvent destEvent = new AlgorithmEvent();
                    destEvent.setStep(stepCounter++);
                    destEvent.setType(AlgorithmEvent.EventType.DESTINATION_REACHED);
                    destEvent.setCurrentStationId(u);
                    destEvent.setCurrentStationName(uStation != null ? uStation.getName() : "Station " + u);
                    destEvent.setCurrentCost(Math.round(cost[u] * 100.0) / 100.0);
                    destEvent.setQueueSnapshot(capturePQSnapshot(pq, graph));
                    destEvent.setVisitedStations(new ArrayList<>(visitedStationNames));
                    destEvent.setParentMap(captureParentMap(parent, graph));
                    destEvent.setDescription("Destination '" + destStation.getName() + "' reached with finalized minimal cost of " + formatCost(cost[u], weightUnit) + "! Commencing path reconstruction via parent array.");
                    events.add(destEvent);
                }
                break;
            }

            // Relax all outgoing edges from vertex u
            for (Edge edge : graph.getNeighbors(u)) {
                int v = edge.getDestination();
                Station vStation = graph.getStation(v);
                String vName = vStation != null ? vStation.getName() : "Station " + v;
                double edgeWeight = (mode == Mode.TIME) ? edge.getTime() : edge.getDistance();
                double candidateCost = cost[u] + edgeWeight;

                if (!visited[v]) {
                    if (candidateCost < cost[v]) {
                        double oldCost = cost[v];
                        cost[v] = candidateCost;
                        parent[v] = u;
                        edgeUsed[v] = edge;
                        pq.add(new Node(v, candidateCost));

                        if (recordEvents) {
                            AlgorithmEvent relaxEvent = new AlgorithmEvent();
                            relaxEvent.setStep(stepCounter++);
                            relaxEvent.setType(AlgorithmEvent.EventType.RELAX_SUCCESS);
                            relaxEvent.setCurrentStationId(u);
                            relaxEvent.setCurrentStationName(uStation != null ? uStation.getName() : "Station " + u);
                            relaxEvent.setCurrentCost(Math.round(cost[u] * 100.0) / 100.0);
                            relaxEvent.setNeighborStationId(v);
                            relaxEvent.setNeighborStationName(vName);
                            relaxEvent.setEdgeWeight(Math.round(edgeWeight * 100.0) / 100.0);
                            relaxEvent.setEdgeLine(edge.getLine());
                            relaxEvent.setOldCost(oldCost == Double.MAX_VALUE ? null : Math.round(oldCost * 100.0) / 100.0);
                            relaxEvent.setNewCost(Math.round(candidateCost * 100.0) / 100.0);
                            relaxEvent.setRelaxed(true);
                            relaxEvent.setQueueSnapshot(capturePQSnapshot(pq, graph));
                            relaxEvent.setVisitedStations(new ArrayList<>(visitedStationNames));
                            relaxEvent.setParentMap(captureParentMap(parent, graph));
                            relaxEvent.setDescription("Relaxing edge: " + uStation.getName() + " ➔ " + vName + " (" + edge.getLine() + ", " + formatCost(edgeWeight, weightUnit) + "). Shorter path found! Updated cost to " + formatCost(candidateCost, weightUnit) + " and set parent[" + vName + "] = " + uStation.getName() + ".");
                            events.add(relaxEvent);
                        }
                    } else {
                        if (recordEvents) {
                            AlgorithmEvent skipEvent = new AlgorithmEvent();
                            skipEvent.setStep(stepCounter++);
                            skipEvent.setType(AlgorithmEvent.EventType.RELAX_SKIPPED);
                            skipEvent.setCurrentStationId(u);
                            skipEvent.setCurrentStationName(uStation != null ? uStation.getName() : "Station " + u);
                            skipEvent.setCurrentCost(Math.round(cost[u] * 100.0) / 100.0);
                            skipEvent.setNeighborStationId(v);
                            skipEvent.setNeighborStationName(vName);
                            skipEvent.setEdgeWeight(Math.round(edgeWeight * 100.0) / 100.0);
                            skipEvent.setEdgeLine(edge.getLine());
                            skipEvent.setOldCost(Math.round(cost[v] * 100.0) / 100.0);
                            skipEvent.setNewCost(Math.round(candidateCost * 100.0) / 100.0);
                            skipEvent.setRelaxed(false);
                            skipEvent.setQueueSnapshot(capturePQSnapshot(pq, graph));
                            skipEvent.setVisitedStations(new ArrayList<>(visitedStationNames));
                            skipEvent.setParentMap(captureParentMap(parent, graph));
                            skipEvent.setDescription("Examined neighbor: " + uStation.getName() + " ➔ " + vName + ". Candidate cost (" + formatCost(candidateCost, weightUnit) + ") is not better than existing cost (" + formatCost(cost[v], weightUnit) + "). Relaxation skipped.");
                            events.add(skipEvent);
                        }
                    }
                } else {
                    if (recordEvents) {
                        AlgorithmEvent visitedEvent = new AlgorithmEvent();
                        visitedEvent.setStep(stepCounter++);
                        visitedEvent.setType(AlgorithmEvent.EventType.ALREADY_VISITED);
                        visitedEvent.setCurrentStationId(u);
                        visitedEvent.setCurrentStationName(uStation != null ? uStation.getName() : "Station " + u);
                        visitedEvent.setCurrentCost(Math.round(cost[u] * 100.0) / 100.0);
                        visitedEvent.setNeighborStationId(v);
                        visitedEvent.setNeighborStationName(vName);
                        visitedEvent.setEdgeWeight(Math.round(edgeWeight * 100.0) / 100.0);
                        visitedEvent.setEdgeLine(edge.getLine());
                        visitedEvent.setRelaxed(false);
                        visitedEvent.setQueueSnapshot(capturePQSnapshot(pq, graph));
                        visitedEvent.setVisitedStations(new ArrayList<>(visitedStationNames));
                        visitedEvent.setParentMap(captureParentMap(parent, graph));
                        visitedEvent.setDescription("Neighbor '" + vName + "' is already visited and finalized. Skipped.");
                        events.add(visitedEvent);
                    }
                }
            }
        }

        if (!reachedDestination) {
            return null; // Target unreachable
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
                backtrackEvent.setCurrentCost(Math.round(cost[stId] * 100.0) / 100.0);
                backtrackEvent.setCurrentPathSnapshot(new ArrayList<>(backtrackNames.subList(0, i + 1)));
                backtrackEvent.setParentMap(captureParentMap(parent, graph));
                backtrackEvent.setDescription(pSt != null
                        ? "Backtracking: At '" + st.getName() + "' ➔ Tracing parent pointer to '" + pSt.getName() + "'"
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

        RouteResult routeResult = buildRouteResult(graph, stationPath, edgeUsed, algorithmFullName);

        if (recordEvents) {
            AlgorithmEvent finalEvent = new AlgorithmEvent();
            finalEvent.setStep(stepCounter++);
            finalEvent.setType(AlgorithmEvent.EventType.FINAL_PATH_READY);
            finalEvent.setCurrentStationId(destinationId);
            finalEvent.setCurrentStationName(destStation.getName());
            finalEvent.setCurrentCost(Math.round(cost[destinationId] * 100.0) / 100.0);
            finalEvent.setCurrentPathSnapshot(finalPathNames);
            finalEvent.setVisitedStations(new ArrayList<>(visitedStationNames));
            finalEvent.setParentMap(captureParentMap(parent, graph));
            finalEvent.setDescription("Optimal shortest path finalized: " + String.join(" ➔ ", finalPathNames) + " (" + routeResult.getStops() + " stops, " + routeResult.getTime() + " min, " + routeResult.getDistance() + " km).");
            events.add(finalEvent);
        }

        return new VisualizationResult(
                "Dijkstra",
                optimizationName,
                weightUnit,
                sourceStation.getName(),
                destStation.getName(),
                events.size(),
                events,
                backtrackNames,
                finalPathNames,
                routeResult,
                "O((V + E) log V)",
                "O(V + E)"
        );
    }

    private static String formatCost(double val, String unit) {
        return (Math.round(val * 10.0) / 10.0) + " " + unit;
    }

    private static List<QueueItem> capturePQSnapshot(PriorityQueue<Node> pq, MetroGraph graph) {
        List<QueueItem> list = new ArrayList<>();
        PriorityQueue<Node> copy = new PriorityQueue<>(pq);
        while (!copy.isEmpty()) {
            Node node = copy.poll();
            Station st = graph.getStation(node.getStationId());
            list.add(new QueueItem(
                    node.getStationId(),
                    st != null ? st.getName() : "Station " + node.getStationId(),
                    Math.round(node.getCost() * 100.0) / 100.0,
                    0,
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
