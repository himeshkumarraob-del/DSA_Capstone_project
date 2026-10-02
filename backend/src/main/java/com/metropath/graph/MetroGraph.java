package com.metropath.graph;

import com.metropath.model.Edge;
import com.metropath.model.Station;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;

/**
 * MetroGraph represents the metro network as an Undirected Weighted Graph.
 * Data Structures Used:
 * 1. HashMap<Integer, List<Edge>>: Adjacency list storing edges for each station ID.
 * 2. HashMap<String, Integer>: Maps station names (case-insensitive) to their Station IDs for O(1) lookups.
 * 3. HashMap<Integer, Station>: Quick access to Station metadata by ID.
 * 4. ArrayList: Used to hold adjacency lists and collections of stations/lines.
 */
@Component
public class MetroGraph {

    // Adjacency List: Station ID -> List of outgoing track connections
    private final Map<Integer, List<Edge>> adjacencyList = new HashMap<>();

    // HashMap for O(1) lookup: Station Name (lowercase) -> Station ID
    private final Map<String, Integer> stationNameToId = new HashMap<>();

    // Station Registry: Station ID -> Station metadata object
    private final Map<Integer, Station> stationsById = new HashMap<>();

    private int maxStationId = 0;

    @PostConstruct
    public void init() {
        populateChennaiMetroDemoNetwork();
    }

    /**
     * Adds a station (Vertex) to the graph.
     */
    public void addStation(Station station) {
        stationsById.put(station.getId(), station);
        stationNameToId.put(station.getName().trim().toLowerCase(), station.getId());
        adjacencyList.putIfAbsent(station.getId(), new ArrayList<>());
        if (station.getId() > maxStationId) {
            maxStationId = station.getId();
        }
    }

    /**
     * Adds an undirected edge (bi-directional metro connection) between two stations.
     */
    public void addEdge(int srcId, int destId, double distance, int time, String line) {
        if (!stationsById.containsKey(srcId) || !stationsById.containsKey(destId)) {
            throw new IllegalArgumentException("Station ID " + srcId + " or " + destId + " does not exist in graph.");
        }
        adjacencyList.get(srcId).add(new Edge(destId, distance, time, line));
        adjacencyList.get(destId).add(new Edge(srcId, distance, time, line));
    }

    public List<Edge> getNeighbors(int stationId) {
        return adjacencyList.getOrDefault(stationId, Collections.emptyList());
    }

    public Station getStation(int id) {
        return stationsById.get(id);
    }

    public Integer getStationId(String name) {
        if (name == null) return null;
        return stationNameToId.get(name.trim().toLowerCase());
    }

    public List<Station> getAllStations() {
        List<Station> list = new ArrayList<>(stationsById.values());
        list.sort(Comparator.comparing(Station::getName));
        return list;
    }

    public int getMaxStationId() {
        return maxStationId;
    }

    public int getVertexCount() {
        return stationsById.size();
    }

    public int getEdgeCount() {
        int total = 0;
        for (List<Edge> edges : adjacencyList.values()) {
            total += edges.size();
        }
        return total / 2; // Undirected graph
    }

    /**
     * Initializes an academic demo dataset based on the Chennai Metro network (35 stations).
     * Featuring Blue Line, Green Line, Purple Line, and Orange Line express corridor.
     *
     * The Orange Line express connections intentionally have higher track distance but
     * lower travel time than the equivalent local route, creating genuine trade-offs
     * between Dijkstra(TIME) and Dijkstra(DISTANCE) for several route pairs:
     *
     *   Egmore(16) -> Guindy(12):    9.2 km,  8 min  (vs local Blue: 9.8 km, 16 min)
     *   Vadapalani(27) -> Alandur(13): 6.5 km, 7 min  (vs local Green: 5.1 km,  9 min)
     *
     * Demo trade-off example — Chennai Central -> Airport:
     *   FASTEST:  via Orange express  = 16.6 km, 19 min  (faster, longer)
     *   SHORTEST: via Blue Line local = 15.4 km, 24 min  (shorter, slower)
     */
    private void populateChennaiMetroDemoNetwork() {
        // --- 1. Station Definitions ---
        // Blue Line Stations (North to South corridor)
        addStation(new Station(1, "Wimco Nagar Depot", "Blue Line", 13.1670, 80.3015, false, Arrays.asList("Blue Line")));
        addStation(new Station(2, "Wimco Nagar", "Blue Line", 13.1590, 80.3010, false, Arrays.asList("Blue Line")));
        addStation(new Station(3, "Tollgate", "Blue Line", 13.1310, 80.2940, false, Arrays.asList("Blue Line")));
        addStation(new Station(4, "Washermanpet", "Blue Line", 13.1090, 80.2830, false, Arrays.asList("Blue Line")));
        // Central: Major Interchange (Blue Line + Green Line)
        addStation(new Station(5, "Chennai Central", "Blue Line", 13.0820, 80.2750, true, Arrays.asList("Blue Line", "Green Line")));
        addStation(new Station(6, "LIC", "Blue Line", 13.0640, 80.2640, false, Arrays.asList("Blue Line")));
        // Thousand Lights: Major Interchange (Blue Line + Purple Line)
        addStation(new Station(7, "Thousand Lights", "Blue Line", 13.0550, 80.2520, true, Arrays.asList("Blue Line", "Purple Line")));
        addStation(new Station(8, "AG-DMS", "Blue Line", 13.0450, 80.2470, false, Arrays.asList("Blue Line")));
        addStation(new Station(9, "Teynampet", "Blue Line", 13.0380, 80.2440, false, Arrays.asList("Blue Line")));
        addStation(new Station(10, "Nandanam", "Blue Line", 13.0290, 80.2390, false, Arrays.asList("Blue Line")));
        addStation(new Station(11, "Saidapet", "Blue Line", 13.0200, 80.2280, false, Arrays.asList("Blue Line")));
        // Guindy: Interchange (Blue Line + Orange Line express)
        addStation(new Station(12, "Guindy", "Blue Line", 13.0080, 80.2130, true, Arrays.asList("Blue Line", "Orange Line")));
        // Alandur: Major Interchange (Blue Line + Green Line + Orange Line express)
        addStation(new Station(13, "Alandur", "Blue Line", 12.9970, 80.2010, true, Arrays.asList("Blue Line", "Green Line", "Orange Line")));
        addStation(new Station(14, "Meenambakkam", "Blue Line", 12.9860, 80.1800, false, Arrays.asList("Blue Line")));
        addStation(new Station(15, "Chennai International Airport", "Blue Line", 12.9780, 80.1650, false, Arrays.asList("Blue Line")));

        // Green Line Stations (Central -> Loop via Anna Nagar & Koyambedu -> Alandur -> St. Thomas Mount)
        // Egmore: Major Interchange (Green Line + Purple Line + Orange Line express)
        addStation(new Station(16, "Egmore", "Green Line", 13.0780, 80.2610, true, Arrays.asList("Green Line", "Purple Line", "Orange Line")));
        addStation(new Station(17, "Nehru Park", "Green Line", 13.0780, 80.2490, false, Arrays.asList("Green Line")));
        addStation(new Station(18, "Kilpauk", "Green Line", 13.0790, 80.2390, false, Arrays.asList("Green Line")));
        addStation(new Station(19, "Pachaiyappa's College", "Green Line", 13.0770, 80.2280, false, Arrays.asList("Green Line")));
        addStation(new Station(20, "Shenoy Nagar", "Green Line", 13.0790, 80.2200, false, Arrays.asList("Green Line")));
        addStation(new Station(21, "Anna Nagar East", "Green Line", 13.0850, 80.2170, false, Arrays.asList("Green Line")));
        addStation(new Station(22, "Anna Nagar Tower", "Green Line", 13.0860, 80.2100, false, Arrays.asList("Green Line")));
        addStation(new Station(23, "Thirumangalam", "Green Line", 13.0850, 80.1980, false, Arrays.asList("Green Line")));
        addStation(new Station(24, "Koyambedu", "Green Line", 13.0720, 80.1950, false, Arrays.asList("Green Line")));
        addStation(new Station(25, "CMBT", "Green Line", 13.0670, 80.2030, false, Arrays.asList("Green Line")));
        addStation(new Station(26, "Arumbakkam", "Green Line", 13.0600, 80.2110, false, Arrays.asList("Green Line")));
        // Vadapalani: Interchange (Green Line + Orange Line express)
        addStation(new Station(27, "Vadapalani", "Green Line", 13.0500, 80.2120, true, Arrays.asList("Green Line", "Orange Line")));
        addStation(new Station(28, "Ashok Nagar", "Green Line", 13.0360, 80.2110, false, Arrays.asList("Green Line")));
        addStation(new Station(29, "Ekkattuthangal", "Green Line", 13.0180, 80.2050, false, Arrays.asList("Green Line")));
        addStation(new Station(30, "St. Thomas Mount", "Green Line", 12.9940, 80.1990, false, Arrays.asList("Green Line")));

        // Purple Line Stations (Cross-town connector demo line)
        addStation(new Station(31, "Madhavaram", "Purple Line", 13.1480, 80.2310, false, Arrays.asList("Purple Line")));
        addStation(new Station(32, "Perambur", "Purple Line", 13.1110, 80.2440, false, Arrays.asList("Purple Line")));
        addStation(new Station(33, "Sterling Road", "Purple Line", 13.0620, 80.2410, false, Arrays.asList("Purple Line")));
        addStation(new Station(34, "Mylapore", "Purple Line", 13.0330, 80.2680, false, Arrays.asList("Purple Line")));
        addStation(new Station(35, "Adyar Depot", "Purple Line", 13.0060, 80.2560, false, Arrays.asList("Purple Line")));

        // --- 2. Track Connections (Edges) ---
        // Blue Line Connections (Wimco Nagar to Chennai International Airport)
        addEdge(1, 2, 1.3, 2, "Blue Line");   // Wimco Nagar Depot - Wimco Nagar
        addEdge(2, 3, 3.1, 4, "Blue Line");   // Wimco Nagar - Tollgate
        addEdge(3, 4, 2.4, 3, "Blue Line");   // Tollgate - Washermanpet
        addEdge(4, 5, 2.9, 4, "Blue Line");   // Washermanpet - Chennai Central
        addEdge(5, 6, 2.1, 3, "Blue Line");   // Chennai Central - LIC
        addEdge(6, 7, 1.2, 2, "Blue Line");   // LIC - Thousand Lights
        addEdge(7, 8, 1.1, 2, "Blue Line");   // Thousand Lights - AG-DMS
        addEdge(8, 9, 0.9, 2, "Blue Line");   // AG-DMS - Teynampet
        addEdge(9, 10, 1.3, 2, "Blue Line");  // Teynampet - Nandanam
        addEdge(10, 11, 1.4, 2, "Blue Line"); // Nandanam - Saidapet
        addEdge(11, 12, 1.8, 3, "Blue Line"); // Saidapet - Guindy
        addEdge(12, 13, 1.9, 3, "Blue Line"); // Guindy - Alandur
        addEdge(13, 14, 2.1, 3, "Blue Line"); // Alandur - Meenambakkam
        addEdge(14, 15, 1.6, 2, "Blue Line"); // Meenambakkam - Chennai International Airport

        // Green Line Connections (Chennai Central -> Koyambedu -> Alandur -> St. Thomas Mount)
        addEdge(5, 16, 1.8, 3, "Green Line");  // Chennai Central - Egmore
        addEdge(16, 17, 1.3, 2, "Green Line"); // Egmore - Nehru Park
        addEdge(17, 18, 1.1, 2, "Green Line"); // Nehru Park - Kilpauk
        addEdge(18, 19, 1.2, 2, "Green Line"); // Kilpauk - Pachaiyappa's College
        addEdge(19, 20, 1.0, 2, "Green Line"); // Pachaiyappa's - Shenoy Nagar
        addEdge(20, 21, 1.1, 2, "Green Line"); // Shenoy Nagar - Anna Nagar East
        addEdge(21, 22, 0.9, 2, "Green Line"); // Anna Nagar East - Anna Nagar Tower
        addEdge(22, 23, 1.4, 2, "Green Line"); // Anna Nagar Tower - Thirumangalam
        addEdge(23, 24, 1.5, 3, "Green Line"); // Thirumangalam - Koyambedu
        addEdge(24, 25, 1.1, 2, "Green Line"); // Koyambedu - CMBT
        addEdge(25, 26, 1.0, 2, "Green Line"); // CMBT - Arumbakkam
        addEdge(26, 27, 1.4, 2, "Green Line"); // Arumbakkam - Vadapalani
        addEdge(27, 28, 1.6, 3, "Green Line"); // Vadapalani - Ashok Nagar
        addEdge(28, 29, 1.8, 3, "Green Line"); // Ashok Nagar - Ekkattuthangal
        addEdge(29, 13, 1.7, 3, "Green Line"); // Ekkattuthangal - Alandur
        addEdge(13, 30, 1.4, 2, "Green Line"); // Alandur - St. Thomas Mount

        // Purple Line Connections (Madhavaram -> Perambur -> Egmore -> Thousand Lights -> Adyar Depot)
        addEdge(31, 32, 4.2, 6, "Purple Line"); // Madhavaram - Perambur
        addEdge(32, 16, 3.8, 5, "Purple Line"); // Perambur - Egmore (Interchange with Green Line)
        addEdge(16, 33, 2.2, 3, "Purple Line"); // Egmore - Sterling Road
        addEdge(33, 7, 2.0, 3, "Purple Line");  // Sterling Road - Thousand Lights (Interchange with Blue Line)
        addEdge(7, 34, 2.7, 4, "Purple Line");  // Thousand Lights - Mylapore
        addEdge(34, 35, 3.1, 5, "Purple Line"); // Mylapore - Adyar Depot

        // --- 4. Orange Line Express Connections ---
        // These two diagonal express segments are FASTER but LONGER than the local route.
        // This creates a genuine Dijkstra(TIME) vs Dijkstra(DISTANCE) trade-off.
        //
        // Egmore -> Guindy express:
        //   Orange Line: 9.2 km, 8 min  (high-speed diagonal)
        //   Local via Blue Line: 9.8 km, 16 min  (many stops)
        //   => FASTEST takes Orange (saves 8 min), SHORTEST takes Blue (saves 1.4 km)
        addEdge(16, 12, 9.2, 8, "Orange Line");   // Egmore - Guindy Express

        // Vadapalani -> Alandur express:
        //   Orange Line: 6.5 km, 7 min  (cross-town skip)
        //   Local via Green Line (27->28->29->13): 5.1 km, 9 min
        //   => FASTEST takes Orange (saves 2 min), SHORTEST takes Green local (saves 1.4 km)
        addEdge(27, 13, 6.5, 7, "Orange Line");   // Vadapalani - Alandur Express
    }
}
