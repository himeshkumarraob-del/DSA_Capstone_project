package com.metropath.algorithm;

import com.metropath.graph.MetroGraph;
import com.metropath.model.AlgorithmEvent;
import com.metropath.model.RouteRequest;
import com.metropath.model.RouteResult;
import com.metropath.model.VisualizationResult;
import com.metropath.service.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RouteAlgorithmTest {

    private MetroGraph graph;
    private RouteService routeService;

    @BeforeEach
    void setUp() {
        graph = new MetroGraph();
        graph.init();
        routeService = new RouteService(graph);
    }

    @Test
    void test1_CentralToAirport_Fastest() {
        RouteRequest req = new RouteRequest("Chennai Central", "Chennai International Airport", "FASTEST");
        RouteResult res = routeService.calculateRoute(req);

        assertNotNull(res);
        assertEquals("Chennai Central", res.getSource());
        assertEquals("Chennai International Airport", res.getDestination());
        assertEquals("Dijkstra - Travel Time", res.getAlgorithmUsed());
        assertTrue(res.getStops() > 0);
        assertTrue(res.getTime() > 0);
        assertTrue(res.getDistance() > 0);
        assertTrue(res.getStations().contains("Egmore"), "Fastest route must pass through Egmore (Orange Line express)");
        assertTrue(res.getStations().contains("Alandur"));
        assertEquals(5, res.getStops());
        assertEquals(19, res.getTime());
        assertEquals(16.6, res.getDistance(), 0.1);
        System.out.println("Test 1 (Fastest): Time=" + res.getTime() + " mins, Stops=" + res.getStops() + ", Dist=" + res.getDistance() + "km, Path=" + res.getStations());
    }

    @Test
    void test2_CentralToAirport_ShortestDistance() {
        RouteRequest req = new RouteRequest("Chennai Central", "Chennai International Airport", "SHORTEST_DISTANCE");
        RouteResult res = routeService.calculateRoute(req);

        assertNotNull(res);
        assertEquals("Chennai Central", res.getSource());
        assertEquals("Chennai International Airport", res.getDestination());
        assertEquals("Dijkstra - Shortest Distance", res.getAlgorithmUsed());
        assertEquals(10, res.getStops());
        assertEquals(24, res.getTime());
        assertEquals(15.4, res.getDistance(), 0.1);
        System.out.println("Test 2 (Shortest Distance): Dist=" + res.getDistance() + " km, Time=" + res.getTime() + " mins");
    }

    @Test
    void test3_CentralToAirport_FewestStops() {
        RouteRequest req = new RouteRequest("Chennai Central", "Chennai International Airport", "FEWEST_STOPS");
        RouteResult res = routeService.calculateRoute(req);

        assertNotNull(res);
        assertEquals("Chennai Central", res.getSource());
        assertEquals("Chennai International Airport", res.getDestination());
        assertEquals("BFS - Fewest Stops", res.getAlgorithmUsed());
        assertEquals(5, res.getStops());
        assertEquals(19, res.getTime());
        assertEquals(16.6, res.getDistance(), 0.1);
        System.out.println("Test 3 (Fewest Stops): Stops=" + res.getStops() + ", Path=" + res.getStations());
    }

    @Test
    void test4_VadapalaniToThousandLights() {
        RouteRequest req = new RouteRequest("Vadapalani", "Thousand Lights", "FASTEST");
        RouteResult res = routeService.calculateRoute(req);

        assertNotNull(res);
        assertEquals("Vadapalani", res.getSource());
        assertEquals("Thousand Lights", res.getDestination());
        assertTrue(res.getStations().size() >= 3);
        System.out.println("Test 4 (Vadapalani -> Thousand Lights): Algorithm=" + res.getAlgorithmUsed() + ", Interchanges=" + res.getInterchanges() + ", Path=" + res.getStations());
    }

    @Test
    void test5_RouteWithInterchange_KoyambeduToAirport() {
        RouteRequest req = new RouteRequest("Koyambedu", "Chennai International Airport", "FASTEST");
        RouteResult res = routeService.calculateRoute(req);

        assertNotNull(res);
        assertEquals("Koyambedu", res.getSource());
        assertEquals("Chennai International Airport", res.getDestination());
        assertTrue(res.getInterchanges() >= 1, "Should have at least 1 interchange between Green and Orange/Blue lines");
        assertTrue(res.getStations().contains("Alandur"), "Route should pass through Alandur interchange");
        assertEquals(6, res.getStops());
        assertEquals(18, res.getTime());
        assertEquals(13.7, res.getDistance(), 0.1);
        System.out.println("Test 5 (Koyambedu -> Airport with Interchange): Interchanges=" + res.getInterchanges() + ", Lines=" + res.getLines() + ", Path=" + res.getStations());
    }

    @Test
    void testSameStationValidation() {
        RouteRequest req = new RouteRequest("Chennai Central", "Chennai Central", "FASTEST");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> routeService.calculateRoute(req));
        assertEquals("Source and destination must be different.", ex.getMessage());
    }

    @Test
    void test7_DijkstraModesDifferentRoutes_CentralToAirport() {
        RouteRequest fastReq = new RouteRequest("Chennai Central", "Chennai International Airport", "FASTEST");
        RouteResult fast = routeService.calculateRoute(fastReq);

        RouteRequest shortReq = new RouteRequest("Chennai Central", "Chennai International Airport", "SHORTEST_DISTANCE");
        RouteResult shortest = routeService.calculateRoute(shortReq);

        assertNotNull(fast);
        assertNotNull(shortest);

        assertNotEquals(fast.getStations(), shortest.getStations(),
            "FASTEST and SHORTEST_DISTANCE must return different station sequences for Central->Airport");

        assertTrue(fast.getTime() < shortest.getTime(),
            "Fastest route must have lower time than shortest-distance route");

        assertTrue(shortest.getDistance() < fast.getDistance(),
            "Shortest-distance route must have lower km than fastest route");
    }

    @Test
    void test8_DijkstraModesDifferentRoutes_KoyambeduToAirport() {
        RouteRequest fastReq = new RouteRequest("Koyambedu", "Chennai International Airport", "FASTEST");
        RouteResult fast = routeService.calculateRoute(fastReq);

        RouteRequest shortReq = new RouteRequest("Koyambedu", "Chennai International Airport", "SHORTEST_DISTANCE");
        RouteResult shortest = routeService.calculateRoute(shortReq);

        assertNotNull(fast);
        assertNotNull(shortest);

        assertNotEquals(fast.getStations(), shortest.getStations(),
            "FASTEST and SHORTEST_DISTANCE must return different routes for Koyambedu->Airport");

        assertTrue(fast.getTime() < shortest.getTime(),
            "Fastest route must have lower travel time");
        assertTrue(shortest.getDistance() < fast.getDistance(),
            "Shortest-distance route must have lower km");
    }

    // ==========================================
    // VISUALIZATION UNIT TESTS
    // ==========================================

    @Test
    void test9_DijkstraTimeVisualizationEvents() {
        RouteRequest req = new RouteRequest("Chennai Central", "Chennai International Airport", "FASTEST");
        VisualizationResult vis = routeService.visualizeRoute(req);

        assertNotNull(vis);
        assertEquals("Dijkstra", vis.getAlgorithm());
        assertEquals("Travel Time", vis.getOptimization());
        assertEquals("min", vis.getWeightUnit());
        assertEquals("Chennai Central", vis.getSource());
        assertEquals("Chennai International Airport", vis.getDestination());
        assertTrue(vis.getTotalSteps() > 10, "Should generate detailed execution steps");

        List<AlgorithmEvent> events = vis.getEvents();
        assertFalse(events.isEmpty());

        // Step 1 must be START with source station
        AlgorithmEvent firstEvent = events.get(0);
        assertEquals(AlgorithmEvent.EventType.START, firstEvent.getType());
        assertEquals("Chennai Central", firstEvent.getCurrentStationName());
        assertEquals(0.0, firstEvent.getCurrentCost());
        assertNotNull(firstEvent.getQueueSnapshot());
        assertEquals(1, firstEvent.getQueueSnapshot().size());

        // Must have relaxation events
        boolean hasRelaxation = events.stream().anyMatch(e -> e.getType() == AlgorithmEvent.EventType.RELAX_SUCCESS && e.isRelaxed());
        assertTrue(hasRelaxation, "Dijkstra visualization must contain RELAX_SUCCESS events");

        // Must reach destination
        boolean reachedDest = events.stream().anyMatch(e -> e.getType() == AlgorithmEvent.EventType.DESTINATION_REACHED);
        assertTrue(reachedDest, "Dijkstra visualization must record DESTINATION_REACHED");

        // Must backtrack
        boolean hasBacktrack = events.stream().anyMatch(e -> e.getType() == AlgorithmEvent.EventType.BACKTRACK_STEP);
        assertTrue(hasBacktrack, "Dijkstra visualization must contain BACKTRACK_STEP events");

        // Final event must be FINAL_PATH_READY
        AlgorithmEvent lastEvent = events.get(events.size() - 1);
        assertEquals(AlgorithmEvent.EventType.FINAL_PATH_READY, lastEvent.getType());
        assertNotNull(lastEvent.getCurrentPathSnapshot());
        assertEquals(vis.getFinalPath(), lastEvent.getCurrentPathSnapshot());

        // Final result matches standard calculation
        RouteResult standard = routeService.calculateRoute(req);
        assertEquals(standard.getStations(), vis.getFinalPath());
        assertEquals(standard.getTime(), vis.getRouteResult().getTime());
        assertEquals(standard.getDistance(), vis.getRouteResult().getDistance());
        assertEquals(standard.getStops(), vis.getRouteResult().getStops());

        // Verify backtrack path starts at Airport and ends at Central
        assertEquals("Chennai International Airport", vis.getBacktrackPath().get(0));
        assertEquals("Chennai Central", vis.getBacktrackPath().get(vis.getBacktrackPath().size() - 1));
    }

    @Test
    void test10_DijkstraDistanceVisualizationEvents() {
        RouteRequest req = new RouteRequest("Chennai Central", "Chennai International Airport", "SHORTEST_DISTANCE");
        VisualizationResult vis = routeService.visualizeRoute(req);

        assertNotNull(vis);
        assertEquals("Dijkstra", vis.getAlgorithm());
        assertEquals("Shortest Distance", vis.getOptimization());
        assertEquals("km", vis.getWeightUnit());

        RouteResult standard = routeService.calculateRoute(req);
        assertEquals(standard.getStations(), vis.getFinalPath());
        assertEquals(10, vis.getRouteResult().getStops());
        assertEquals(15.4, vis.getRouteResult().getDistance(), 0.1);
        assertEquals(24, vis.getRouteResult().getTime());
    }

    @Test
    void test11_BFSVisualizationEvents() {
        RouteRequest req = new RouteRequest("Chennai Central", "Chennai International Airport", "FEWEST_STOPS");
        VisualizationResult vis = routeService.visualizeRoute(req);

        assertNotNull(vis);
        assertEquals("BFS", vis.getAlgorithm());
        assertEquals("Fewest Stops", vis.getOptimization());
        assertEquals("stops", vis.getWeightUnit());

        List<AlgorithmEvent> events = vis.getEvents();
        assertFalse(events.isEmpty());

        AlgorithmEvent firstEvent = events.get(0);
        assertEquals(AlgorithmEvent.EventType.START, firstEvent.getType());
        assertEquals("Chennai Central", firstEvent.getCurrentStationName());
        assertEquals(0, firstEvent.getCurrentLevel());

        // Must have ENQUEUE events
        boolean hasEnqueue = events.stream().anyMatch(e -> e.getType() == AlgorithmEvent.EventType.ENQUEUE);
        assertTrue(hasEnqueue, "BFS visualization must emit ENQUEUE events");

        // Must reach destination
        boolean reachedDest = events.stream().anyMatch(e -> e.getType() == AlgorithmEvent.EventType.DESTINATION_REACHED);
        assertTrue(reachedDest, "BFS visualization must record DESTINATION_REACHED");

        // Verify backtrack
        boolean hasBacktrack = events.stream().anyMatch(e -> e.getType() == AlgorithmEvent.EventType.BACKTRACK_STEP);
        assertTrue(hasBacktrack, "BFS visualization must contain BACKTRACK_STEP events");

        RouteResult standard = routeService.calculateRoute(req);
        assertEquals(standard.getStations(), vis.getFinalPath());
        assertEquals(standard.getStops(), vis.getRouteResult().getStops());
    }

    @Test
    void test12_VisualizationErrorHandling() {
        RouteRequest sameReq = new RouteRequest("Guindy", "Guindy", "FASTEST");
        assertThrows(IllegalArgumentException.class, () -> routeService.visualizeRoute(sameReq));

        RouteRequest nullReq = new RouteRequest("", "Airport", "FASTEST");
        assertThrows(IllegalArgumentException.class, () -> routeService.visualizeRoute(nullReq));

        RouteRequest invalidStation = new RouteRequest("Atlantis", "Guindy", "FASTEST");
        assertThrows(IllegalArgumentException.class, () -> routeService.visualizeRoute(invalidStation));
    }

    @Test
    void test13_DemoCase2_KoyambeduToAirportVisualization() {
        RouteRequest fastReq = new RouteRequest("Koyambedu", "Chennai International Airport", "FASTEST");
        VisualizationResult fastVis = routeService.visualizeRoute(fastReq);
        assertEquals(6, fastVis.getRouteResult().getStops());
        assertEquals(18, fastVis.getRouteResult().getTime());
        assertEquals(13.7, fastVis.getRouteResult().getDistance(), 0.1);

        RouteRequest distReq = new RouteRequest("Koyambedu", "Chennai International Airport", "SHORTEST_DISTANCE");
        VisualizationResult distVis = routeService.visualizeRoute(distReq);
        assertEquals(8, distVis.getRouteResult().getStops());
        assertEquals(20, distVis.getRouteResult().getTime());
        assertEquals(12.3, distVis.getRouteResult().getDistance(), 0.1);

        RouteRequest bfsReq = new RouteRequest("Koyambedu", "Chennai International Airport", "FEWEST_STOPS");
        VisualizationResult bfsVis = routeService.visualizeRoute(bfsReq);
        assertEquals(6, bfsVis.getRouteResult().getStops());
        assertEquals(18, bfsVis.getRouteResult().getTime());
    }
}
