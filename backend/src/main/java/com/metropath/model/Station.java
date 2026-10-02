package com.metropath.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Station represents a Vertex in the Metro Graph.
 * Holds station metadata, line assignment, and optional coordinates for visual mapping.
 */
public class Station {
    private int id;
    private String name;
    private String line;
    private double latitude;
    private double longitude;
    private boolean isInterchange;
    private List<String> lines;

    public Station() {
        this.lines = new ArrayList<>();
    }

    public Station(int id, String name, String line) {
        this.id = id;
        this.name = name;
        this.line = line;
        this.lines = new ArrayList<>();
        this.lines.add(line);
        this.isInterchange = false;
    }

    public Station(int id, String name, String line, double latitude, double longitude, boolean isInterchange, List<String> lines) {
        this.id = id;
        this.name = name;
        this.line = line;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isInterchange = isInterchange;
        this.lines = lines != null ? lines : new ArrayList<>();
        if (!this.lines.contains(line)) {
            this.lines.add(line);
        }
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isInterchange() {
        return isInterchange;
    }

    public void setInterchange(boolean interchange) {
        isInterchange = interchange;
    }

    public List<String> getLines() {
        return lines;
    }

    public void setLines(List<String> lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        return "Station{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", line='" + line + '\'' +
                ", isInterchange=" + isInterchange +
                '}';
    }
}
