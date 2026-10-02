package com.metropath.controller;

import com.metropath.model.RouteRequest;
import com.metropath.model.RouteResult;
import com.metropath.model.Station;
import com.metropath.model.VisualizationResult;
import com.metropath.service.RouteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * RouteController exposes REST APIs for MetroPath:
 * - GET  /api/health
 * - GET  /api/stations
 * - POST /api/route
 * - POST /api/route/visualize
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    /**
     * Health check endpoint.
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        return ResponseEntity.ok(status);
    }

    /**
     * Returns all registered metro stations.
     */
    @GetMapping("/stations")
    public ResponseEntity<List<Station>> getAllStations() {
        List<Station> stations = routeService.getAllStations();
        return ResponseEntity.ok(stations);
    }

    /**
     * Calculates the optimal route using DSA algorithms.
     */
    @PostMapping("/route")
    public ResponseEntity<?> calculateRoute(@RequestBody RouteRequest request) {
        try {
            RouteResult result = routeService.calculateRoute(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (IllegalStateException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Executes the actual Java DSA algorithm and returns an ordered stream of algorithm events
     * for step-by-step educational visualization in the UI.
     */
    @PostMapping("/route/visualize")
    public ResponseEntity<?> visualizeRoute(@RequestBody RouteRequest request) {
        try {
            VisualizationResult result = routeService.visualizeRoute(request);
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        } catch (IllegalStateException ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        } catch (Exception ex) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An unexpected error occurred: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}
