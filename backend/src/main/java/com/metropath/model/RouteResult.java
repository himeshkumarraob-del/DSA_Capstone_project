package com.metropath.model;

import java.util.ArrayList;
import java.util.List;

/**
 * RouteResult contains the complete response payload for a route search.
 * It encapsulates the reconstructed path, stops, distance, time, interchanges,
 * traversed lines, and algorithm name.
 */
public class RouteResult {
    private String source;
    private String destination;
    private List<String> stations;
    private int stops;
    private double distance;
    private int time;
    private int interchanges;
    private List<String> lines;
    private String algorithmUsed;
    private List<RouteStep> routeSteps;

    public RouteResult() {
        this.stations = new ArrayList<>();
        this.lines = new ArrayList<>();
        this.routeSteps = new ArrayList<>();
    }

    public RouteResult(String source, String destination, List<String> stations, int stops,
                       double distance, int time, int interchanges, List<String> lines,
                       String algorithmUsed, List<RouteStep> routeSteps) {
        this.source = source;
        this.destination = destination;
        this.stations = stations;
        this.stops = stops;
        this.distance = distance;
        this.time = time;
        this.interchanges = interchanges;
        this.lines = lines;
        this.algorithmUsed = algorithmUsed;
        this.routeSteps = routeSteps;
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

    public List<String> getStations() {
        return stations;
    }

    public void setStations(List<String> stations) {
        this.stations = stations;
    }

    public int getStops() {
        return stops;
    }

    public void setStops(int stops) {
        this.stops = stops;
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

    public int getInterchanges() {
        return interchanges;
    }

    public void setInterchanges(int interchanges) {
        this.interchanges = interchanges;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    public String getAlgorithmUsed() {
        return algorithmUsed;
    }

    public void setAlgorithmUsed(String algorithmUsed) {
        this.algorithmUsed = algorithmUsed;
    }

    public List<RouteStep> getRouteSteps() {
        return routeSteps;
    }

    public void setRouteSteps(List<RouteStep> routeSteps) {
        this.routeSteps = routeSteps;
    }
}
