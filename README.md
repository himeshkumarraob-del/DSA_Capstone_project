# MetroPath — Smart Metro Route Finder
### DSA Capstone Project | README

---

## 📁 Project Structure

```
DSA_capstone/
│
├── start-backend.bat        ← Double-click to start Java backend
├── start-frontend.bat       ← Double-click to start React frontend
│
├── backend/
│   ├── pom.xml
│   └── src/main/java/com/metropath/
│       ├── MetroPathApplication.java      ← Spring Boot Entry Point
│       ├── model/
│       │   ├── Station.java               ← Graph Vertex
│       │   ├── Edge.java                  ← Graph Edge (weight: time/distance/line)
│       │   ├── Node.java                  ← Dijkstra PriorityQueue element
│       │   ├── RouteResult.java           ← API Response
│       │   ├── RouteStep.java             ← Per-station route step
│       │   └── RouteRequest.java          ← API Request
│       ├── graph/
│       │   └── MetroGraph.java            ← Adjacency List Graph (35 stations)
│       ├── algorithm/
│       │   ├── BFS.java                   ← Breadth-First Search (Fewest Stops)
│       │   └── Dijkstra.java              ← Dijkstra with PriorityQueue
│       ├── service/
│       │   └── RouteService.java          ← Orchestration layer
│       └── controller/
│           └── RouteController.java       ← REST API endpoints
│
└── frontend/
    └── src/
        ├── App.tsx                        ← Main app with state management
        ├── services/api.ts                ← Backend REST communication
        └── components/
            ├── Header.tsx                 ← Top bar + backend status
            ├── RouteSelector.tsx          ← Station dropdowns + preference
            ├── RouteResult.tsx            ← Route timeline + stats cards
            ├── MetroMap.tsx               ← SVG metro schematic
            └── AlgorithmInfo.tsx          ← DSA cards + complexity table
```

---

## 🚀 How to Run

### Step 1: Start the Java Backend

```bash
cd backend
mvn spring-boot:run
```

**Or** double-click `start-backend.bat`

Backend runs on: **http://localhost:8080**

### Step 2: Start the React Frontend

```bash
cd frontend
npm run dev
```

**Or** double-click `start-frontend.bat`

Frontend runs on: **http://localhost:5173**

---

## 🔌 REST API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | /api/health | Health check — returns `{"status":"UP"}` |
| GET | /api/stations | All 35 stations in JSON |
| POST | /api/route | Calculate route (source, destination, preference) |

### POST /api/route — Request Body
```json
{
  "source": "Chennai Central",
  "destination": "Chennai International Airport",
  "preference": "FASTEST"
}
```
Valid preferences: `FASTEST` | `SHORTEST_DISTANCE` | `FEWEST_STOPS`

---

## 🗺️ Demo Metro Dataset

**Academic Demo Dataset** — Chennai Metro-inspired network

| Line | Stations | Key Hubs |
|------|----------|----------|
| Blue Line | 15 stations | Wimco Nagar → Airport |
| Green Line | 16 stations | Central → Koyambedu → St. Thomas Mount |
| Purple Line | 7 stations | Madhavaram → Adyar Depot |

**Interchange Stations:**
- **Chennai Central** — Blue + Green
- **Alandur** — Blue + Green
- **Egmore** — Green + Purple
- **Thousand Lights** — Blue + Purple

---

## 📚 DSA Components

| Data Structure / Algorithm | Java Implementation | Used For |
|---|---|---|
| Graph | `MetroGraph.java` | Overall network structure |
| Adjacency List | `Map<Integer, List<Edge>>` | Neighbor traversal |
| BFS | `BFS.java` — `Queue<Integer>` | Fewest Stops mode |
| Dijkstra | `Dijkstra.java` — `PriorityQueue<Node>` | Fastest & Shortest |
| Priority Queue | `PriorityQueue<Node>` (min-heap) | Dijkstra O(log V) extraction |
| HashMap | `HashMap<String, Integer>` | Station name → ID lookup |
| ArrayList | `List<Edge>`, `List<Station>` | Adjacency lists, collections |
| Parent Array | `int[] parent` | Route backtracking & reconstruction |

---

## 🎓 Viva Preparation

### DSA Questions
1. **Q: Why use an Adjacency List instead of Adjacency Matrix?**  
   A: Adjacency List uses O(V+E) space vs O(V²) for matrix. The metro graph is sparse (35 vertices, 36 edges), so adjacency list is more memory-efficient.

2. **Q: Why does BFS guarantee fewest stops but not fastest route?**  
   A: BFS explores level-by-level (uniform cost=1 per hop). All edges have weight 1, so the first time it reaches the destination is the shortest hop path. But edges have different time weights, so BFS ignores them.

3. **Q: How does Dijkstra's PriorityQueue improve on naive Dijkstra?**  
   A: A min-heap PriorityQueue extracts the minimum-cost vertex in O(log V) vs O(V) for a linear scan, giving O((V+E) log V) total vs O(V²).

4. **Q: How is the route path reconstructed?**  
   A: A `parent[]` array stores which vertex discovered each vertex. After reaching the destination, we backtrack: `dest → parent[dest] → parent[parent[dest]] → ... → source`, then reverse the sequence.

5. **Q: What is the time complexity of BFS and Dijkstra?**  
   A: BFS is O(V+E). Dijkstra with binary heap PriorityQueue is O((V+E) log V).

### Java Implementation Questions
1. **Q: How is Comparable implemented for PriorityQueue ordering?**  
   A: `Node` implements `Comparable<Node>`. `compareTo()` returns `Double.compare(this.cost, other.cost)`, making the PriorityQueue a min-heap by accumulated cost.

2. **Q: Why does `MetroGraph.addEdge()` add two entries?**  
   A: Metro tracks are bidirectional — you can travel A→B or B→A. So one `addEdge(A,B,...)` call adds B to A's adjacency list AND A to B's adjacency list.

3. **Q: What does `@PostConstruct` do in MetroGraph?**  
   A: Spring calls `init()` automatically after the bean is created and injected. This populates the graph with all 35 stations and 36 connections before any HTTP request is received.

4. **Q: Why is HashMap used for station name lookup?**  
   A: The REST API receives station names like "Chennai Central" as strings. HashMap provides O(1) average lookup to convert them to integer IDs for graph traversal.

5. **Q: How are different Dijkstra modes (time vs distance) handled?**  
   A: The `Mode` enum (TIME/DISTANCE) selects the edge weight: `edge.getTime()` for FASTEST or `edge.getDistance()` for SHORTEST_DISTANCE. One algorithm, two optimization targets.

### Application Questions
1. **Q: Why is CORS enabled with `@CrossOrigin(origins = "*")`?**  
   A: The frontend runs on port 5173 and backend on 8080. Without CORS configuration, browsers block cross-origin requests. `*` allows all origins for development.

2. **Q: What happens if the user selects the same source and destination?**  
   A: Both frontend (client validation) and backend (`RouteService.calculateRoute()`) detect this case and throw/return an `IllegalArgumentException` with a clear user message.

3. **Q: How does the metro map highlight the active route?**  
   A: The `routeSteps` array from the API contains station IDs. The SVG MetroMap converts lat/lon to pixel coordinates and draws a yellow highlighted path through those points.

4. **Q: What is the algorithm for the Koyambedu → Airport route?**  
   A: Koyambedu is on Green Line. Airport is on Blue Line. The route goes Green Line → Alandur (interchange hub) → Blue Line → Airport with 1 interchange.

5. **Q: Why is no database used?**  
   A: The project demonstrates in-memory DSA. All graph data is initialized in Java objects at startup via `MetroGraph.populateChennaiMetroDemoNetwork()`. This also makes the project fast and portable with no DB setup required.
