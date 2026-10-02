package com.metropath.model;

import java.util.List;
import java.util.Map;

/**
 * Encapsulates a discrete operational step executed by BFS or Dijkstra.
 * Used by the visualization engine to replay actual graph algorithm behavior.
 */
public class AlgorithmEvent {

    public enum EventType {
        START,                  // Source initialized and inserted into Queue / PriorityQueue
        PROCESS_STATION,        // Station extracted (poll / dequeue)
        EXAMINE_NEIGHBOR,       // Adjacent vertex being evaluated
        RELAX_SUCCESS,          // Dijkstra: shorter path found, distance relaxed and parent updated
        RELAX_SKIPPED,          // Dijkstra: path not shorter or vertex already visited
        ENQUEUE,                // BFS: unvisited neighbor enqueued
        ALREADY_VISITED,        // BFS: neighbor already visited, skipped
        DESTINATION_REACHED,    // Destination node popped/discovered
        BACKTRACK_STEP,         // Parent pointer step from destination toward source
        FINAL_PATH_READY        // Reconstructed forward path ready to highlight
    }

    private int step;
    private EventType type;
    private int currentStationId;
    private String currentStationName;
    private double currentCost;
    private int currentLevel;

    private Integer neighborStationId;
    private String neighborStationName;
    private Double edgeWeight;
    private String edgeLine;

    private Double oldCost;
    private Double newCost;
    private boolean relaxed;

    private List<QueueItem> queueSnapshot;
    private List<String> visitedStations;
    private Map<String, String> parentMap;
    private List<String> currentPathSnapshot;

    private String description;

    public AlgorithmEvent() {
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public int getCurrentStationId() {
        return currentStationId;
    }

    public void setCurrentStationId(int currentStationId) {
        this.currentStationId = currentStationId;
    }

    public String getCurrentStationName() {
        return currentStationName;
    }

    public void setCurrentStationName(String currentStationName) {
        this.currentStationName = currentStationName;
    }

    public double getCurrentCost() {
        return currentCost;
    }

    public void setCurrentCost(double currentCost) {
        this.currentCost = currentCost;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
    }

    public Integer getNeighborStationId() {
        return neighborStationId;
    }

    public void setNeighborStationId(Integer neighborStationId) {
        this.neighborStationId = neighborStationId;
    }

    public String getNeighborStationName() {
        return neighborStationName;
    }

    public void setNeighborStationName(String neighborStationName) {
        this.neighborStationName = neighborStationName;
    }

    public Double getEdgeWeight() {
        return edgeWeight;
    }

    public void setEdgeWeight(Double edgeWeight) {
        this.edgeWeight = edgeWeight;
    }

    public String getEdgeLine() {
        return edgeLine;
    }

    public void setEdgeLine(String edgeLine) {
        this.edgeLine = edgeLine;
    }

    public Double getOldCost() {
        return oldCost;
    }

    public void setOldCost(Double oldCost) {
        this.oldCost = oldCost;
    }

    public Double getNewCost() {
        return newCost;
    }

    public void setNewCost(Double newCost) {
        this.newCost = newCost;
    }

    public boolean isRelaxed() {
        return relaxed;
    }

    public void setRelaxed(boolean relaxed) {
        this.relaxed = relaxed;
    }

    public List<QueueItem> getQueueSnapshot() {
        return queueSnapshot;
    }

    public void setQueueSnapshot(List<QueueItem> queueSnapshot) {
        this.queueSnapshot = queueSnapshot;
    }

    public List<String> getVisitedStations() {
        return visitedStations;
    }

    public void setVisitedStations(List<String> visitedStations) {
        this.visitedStations = visitedStations;
    }

    public Map<String, String> getParentMap() {
        return parentMap;
    }

    public void setParentMap(Map<String, String> parentMap) {
        this.parentMap = parentMap;
    }

    public List<String> getCurrentPathSnapshot() {
        return currentPathSnapshot;
    }

    public void setCurrentPathSnapshot(List<String> currentPathSnapshot) {
        this.currentPathSnapshot = currentPathSnapshot;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
