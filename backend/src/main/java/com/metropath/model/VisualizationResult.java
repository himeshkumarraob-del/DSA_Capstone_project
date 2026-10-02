package com.metropath.model;

import java.util.List;

/**
 * Encapsulates the complete result of an algorithm visualization run,
 * including metadata, list of events, backtrack sequence, and final route result.
 */
public class VisualizationResult {

    private String algorithm;           // "Dijkstra" or "BFS"
    private String optimization;        // "Travel Time", "Shortest Distance", "Fewest Stops"
    private String weightUnit;          // "min", "km", "stops"
    private String source;
    private String destination;
    private int totalSteps;
    private List<AlgorithmEvent> events;
    private List<String> backtrackPath; // Reconstructed path from Destination -> ... -> Source
    private List<String> finalPath;     // Forward path from Source -> ... -> Destination
    private RouteResult routeResult;
    private String timeComplexity;
    private String spaceComplexity;

    public VisualizationResult() {
    }

    public VisualizationResult(String algorithm, String optimization, String weightUnit, String source,
                               String destination, int totalSteps, List<AlgorithmEvent> events,
                               List<String> backtrackPath, List<String> finalPath, RouteResult routeResult,
                               String timeComplexity, String spaceComplexity) {
        this.algorithm = algorithm;
        this.optimization = optimization;
        this.weightUnit = weightUnit;
        this.source = source;
        this.destination = destination;
        this.totalSteps = totalSteps;
        this.events = events;
        this.backtrackPath = backtrackPath;
        this.finalPath = finalPath;
        this.routeResult = routeResult;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getOptimization() {
        return optimization;
    }

    public void setOptimization(String optimization) {
        this.optimization = optimization;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(String weightUnit) {
        this.weightUnit = weightUnit;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public int getTotalSteps() {
        return totalSteps;
    }

    public void setTotalSteps(int totalSteps) {
        this.totalSteps = totalSteps;
    }

    public List<AlgorithmEvent> getEvents() {
        return events;
    }

    public void setEvents(List<AlgorithmEvent> events) {
        this.events = events;
    }

    public List<String> getBacktrackPath() {
        return backtrackPath;
    }

    public void setBacktrackPath(List<String> backtrackPath) {
        this.backtrackPath = backtrackPath;
    }

    public List<String> getFinalPath() {
        return finalPath;
    }

    public void setFinalPath(List<String> finalPath) {
        this.finalPath = finalPath;
    }

    public RouteResult getRouteResult() {
        return routeResult;
    }

    public void setRouteResult(RouteResult routeResult) {
        this.routeResult = routeResult;
    }

    public String getTimeComplexity() {
        return timeComplexity;
    }

    public void setTimeComplexity(String timeComplexity) {
        this.timeComplexity = timeComplexity;
    }

    public String getSpaceComplexity() {
        return spaceComplexity;
    }

    public void setSpaceComplexity(String spaceComplexity) {
        this.spaceComplexity = spaceComplexity;
    }
}
