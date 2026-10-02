package com.metropath.service;

import com.metropath.algorithm.BFS;
import com.metropath.algorithm.Dijkstra;
import com.metropath.graph.MetroGraph;
import com.metropath.model.RouteRequest;
import com.metropath.model.RouteResult;
import com.metropath.model.Station;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * RouteService orchestrates route calculation requests.
 * Connects controller endpoints to core DSA algorithms (BFS and Dijkstra).
 */
@Service
public class RouteService {

    private final MetroGraph metroGraph;

    @Autowired
    public RouteService(MetroGraph metroGraph) {
        this.metroGraph = metroGraph;
    }

    /**
     * Returns list of all stations registered in the graph.
     */
    public List<Station> getAllStations() {
        return metroGraph.getAllStations();
    }

    /**
     * Resolves the requested route by selecting the appropriate DSA algorithm.
     */
    public RouteResult calculateRoute(RouteRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request body cannot be null.");
        }

        String sourceName = request.getSource();
        String destName = request.getDestination();
        String preference = request.getPreference();

        if (sourceName == null || sourceName.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a starting station.");
        }

        if (destName == null || destName.trim().isEmpty()) {
            throw new IllegalArgumentException("Please select a destination station.");
        }

        Integer sourceId = metroGraph.getStationId(sourceName.trim());
        if (sourceId == null) {
            throw new IllegalArgumentException("Source station '" + sourceName + "' was not found in the metro network.");
        }

        Integer destId = metroGraph.getStationId(destName.trim());
        if (destId == null) {
            throw new IllegalArgumentException("Destination station '" + destName + "' was not found in the metro network.");
        }

        if (sourceId.equals(destId)) {
            throw new IllegalArgumentException("Source and destination must be different.");
        }

        if (preference == null || preference.trim().isEmpty()) {
            preference = "FASTEST";
        }

        RouteResult result;

        switch (preference.trim().toUpperCase()) {
            case "FEWEST_STOPS":
                // BFS level-by-level traversal guarantees unweighted minimal hops
                result = BFS.findFewestStopsRoute(metroGraph, sourceId, destId);
                break;

            case "SHORTEST_DISTANCE":
                // Dijkstra using track distance (in km) as edge weight
                result = Dijkstra.findShortestRoute(metroGraph, sourceId, destId, Dijkstra.Mode.DISTANCE);
                break;

            case "FASTEST":
            default:
                // Dijkstra using transit time (in minutes) as edge weight
                result = Dijkstra.findShortestRoute(metroGraph, sourceId, destId, Dijkstra.Mode.TIME);
                break;
        }

        if (result == null) {
            throw new IllegalStateException("No route could be found between these stations.");
        }

        return result;
    }
}
