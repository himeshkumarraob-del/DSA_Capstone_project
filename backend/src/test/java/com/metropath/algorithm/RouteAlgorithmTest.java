package com.metropath.algorithm;

import com.metropath.graph.MetroGraph;
import com.metropath.model.RouteRequest;
import com.metropath.model.RouteResult;
import com.metropath.service.RouteService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        // FASTEST now routes via Egmore->Guindy Orange Line express (19 min, 16.6 km)
        // instead of the Blue Line local path (24 min, 15.4 km)
        assertTrue(res.getStations().contains("Egmore"), "Fastest route must pass through Egmore (Orange Line express)");
        assertTrue(res.getStations().contains("Alandur"));
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
        assertTrue(res.getDistance() > 0);
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
        // Orange Line express gives a 5-hop path (Central->Egmore->Guindy->Alandur->Meenambakkam->Airport)
        // which is fewer than the 10-hop Blue Line path
        assertEquals(5, res.getStops());
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
        // Koyambedu is on Green Line, Airport is on Blue Line. Interchange at Alandur!
        RouteRequest req = new RouteRequest("Koyambedu", "Chennai International Airport", "FASTEST");
        RouteResult res = routeService.calculateRoute(req);

        assertNotNull(res);
        assertEquals("Koyambedu", res.getSource());
        assertEquals("Chennai International Airport", res.getDestination());
        assertTrue(res.getInterchanges() >= 1, "Should have at least 1 interchange between Green and Orange/Blue lines");
        assertTrue(res.getStations().contains("Alandur"), "Route should pass through Alandur interchange");
        System.out.println("Test 5 (Koyambedu -> Airport with Interchange): Interchanges=" + res.getInterchanges() + ", Lines=" + res.getLines() + ", Path=" + res.getStations());
    }

    @Test
    void testSameStationValidation() {
        RouteRequest req = new RouteRequest("Chennai Central", "Chennai Central", "FASTEST");
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> routeService.calculateRoute(req));
        assertEquals("Source and destination must be different.", ex.getMessage());
    }

    /**
     * CORE VIVA TEST: Verifies that FASTEST and SHORTEST_DISTANCE produce DIFFERENT routes.
     * This is the key demonstration that Dijkstra correctly uses different edge weights.
     * Central -> Airport:
     *   FASTEST:  Orange Line express  -> 5 stops, ~19 min, ~16.6 km (faster but longer)
     *   SHORTEST: Blue Line local      -> 10 stops, ~24 min, ~15.4 km (slower but shorter)
     */
    @Test
    void test7_DijkstraModesDifferentRoutes_CentralToAirport() {
        RouteRequest fastReq = new RouteRequest("Chennai Central", "Chennai International Airport", "FASTEST");
        RouteResult fast = routeService.calculateRoute(fastReq);

        RouteRequest shortReq = new RouteRequest("Chennai Central", "Chennai International Airport", "SHORTEST_DISTANCE");
        RouteResult shortest = routeService.calculateRoute(shortReq);

        assertNotNull(fast);
        assertNotNull(shortest);

        // THE KEY ASSERTION: the two modes must produce genuinely different routes
        assertNotEquals(fast.getStations(), shortest.getStations(),
            "FASTEST and SHORTEST_DISTANCE must return different station sequences for Central->Airport");

        // FASTEST is quicker in time
        assertTrue(fast.getTime() < shortest.getTime(),
            "Fastest route must have lower time than shortest-distance route");

        // SHORTEST_DISTANCE is shorter in km
        assertTrue(shortest.getDistance() < fast.getDistance(),
            "Shortest-distance route must have lower km than fastest route");

        System.out.println("=== VIVA DEMO: Dijkstra Mode Comparison (Central -> Airport) ===");
        System.out.println("FASTEST  : " + fast.getStops() + " stops, " + fast.getTime() + " min, " + fast.getDistance() + " km | Path: " + fast.getStations());
        System.out.println("SHORTEST : " + shortest.getStops() + " stops, " + shortest.getTime() + " min, " + shortest.getDistance() + " km | Path: " + shortest.getStations());
        System.out.println("Time savings  (Fastest - Shortest): " + (shortest.getTime() - fast.getTime()) + " min");
        System.out.println("Distance saved (Shortest - Fastest): " + Math.round((fast.getDistance() - shortest.getDistance()) * 10.0) / 10.0 + " km");
    }

    /**
     * VIVA TEST 2: Verifies trade-off for Koyambedu -> Airport.
     * FASTEST:  Koyambedu->...->Vadapalani->Alandur(Orange)     -> fewer stops, faster
     * SHORTEST: Koyambedu->...->Vadapalani->Ashok Nagar->...->Airport -> shorter distance
     */
    @Test
    void test8_DijkstraModesDifferentRoutes_KoyambeduToAirport() {
        RouteRequest fastReq = new RouteRequest("Koyambedu", "Chennai International Airport", "FASTEST");
        RouteResult fast = routeService.calculateRoute(fastReq);

        RouteRequest shortReq = new RouteRequest("Koyambedu", "Chennai International Airport", "SHORTEST_DISTANCE");
        RouteResult shortest = routeService.calculateRoute(shortReq);

        assertNotNull(fast);
        assertNotNull(shortest);

        // The two modes must produce different routes
        assertNotEquals(fast.getStations(), shortest.getStations(),
            "FASTEST and SHORTEST_DISTANCE must return different routes for Koyambedu->Airport");

        assertTrue(fast.getTime() < shortest.getTime(),
            "Fastest route must have lower travel time");
        assertTrue(shortest.getDistance() < fast.getDistance(),
            "Shortest-distance route must have lower km");

        System.out.println("=== VIVA DEMO: Dijkstra Mode Comparison (Koyambedu -> Airport) ===");
        System.out.println("FASTEST  : " + fast.getStops() + " stops, " + fast.getTime() + " min, " + fast.getDistance() + " km | Path: " + fast.getStations());
        System.out.println("SHORTEST : " + shortest.getStops() + " stops, " + shortest.getTime() + " min, " + shortest.getDistance() + " km | Path: " + shortest.getStations());
    }
}


