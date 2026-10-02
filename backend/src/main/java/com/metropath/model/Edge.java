package com.metropath.model;

/**
 * Edge represents a direct track connection between two Metro Stations.
 * Destination: target station ID
 * Distance: track length in kilometers
 * Time: estimated travel time in minutes
 * Line: metro line name (e.g., "Blue Line", "Green Line")
 */
public class Edge {
    private int destination;
    private double distance;
    private int time;
    private String line;

    public Edge() {
    }

    public Edge(int destination, double distance, int time, String line) {
        this.destination = destination;
        this.distance = distance;
        this.time = time;
        this.line = line;
    }

    public int getDestination() {
        return destination;
    }

    public void setDestination(int destination) {
        this.destination = destination;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    @Override
    public String toString() {
        return "Edge{" +
                "destination=" + destination +
                ", distance=" + distance + "km" +
                ", time=" + time + "min" +
                ", line='" + line + '\'' +
                '}';
    }
}
