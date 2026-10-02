package com.metropath.model;

/**
 * Represents an item inside the algorithm's Queue or PriorityQueue at any step.
 */
public class QueueItem {
    private int stationId;
    private String stationName;
    private double cost;
    private int level;
    private String line;

    public QueueItem() {
    }

    public QueueItem(int stationId, String stationName, double cost, int level, String line) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.cost = cost;
        this.level = level;
        this.line = line;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }
}
