package com.metropath.model;

/**
 * Node represents a state element used in Dijkstra's algorithm.
 * Implements Comparable to allow PriorityQueue to poll the station
 * with the lowest accumulated cost (time or distance) first.
 */
public class Node implements Comparable<Node> {
    private int stationId;
    private double cost;

    public Node() {
    }

    public Node(int stationId, double cost) {
        this.stationId = stationId;
        this.cost = cost;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Min-Heap ordering: node with smaller cost has higher priority.
     */
    @Override
    public int compareTo(Node other) {
        return Double.compare(this.cost, other.cost);
    }

    @Override
    public String toString() {
        return "Node{stationId=" + stationId + ", cost=" + cost + '}';
    }
}
