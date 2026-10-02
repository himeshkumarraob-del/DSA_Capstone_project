package com.metropath.model;

/**
 * RouteStep represents one station along the calculated route,
 * including line information and whether an interchange occurs here.
 */
public class RouteStep {
    private int stationId;
    private String stationName;
    private String line;
    private double cumulativeDistance;
    private int cumulativeTime;
    private boolean interchange;
    private double latitude;
    private double longitude;

    public RouteStep() {
    }

    public RouteStep(int stationId, String stationName, String line, double cumulativeDistance, int cumulativeTime, boolean interchange, double latitude, double longitude) {
        this.stationId = stationId;
        this.stationName = stationName;
        this.line = line;
        this.cumulativeDistance = cumulativeDistance;
        this.cumulativeTime = cumulativeTime;
        this.interchange = interchange;
        this.latitude = latitude;
        this.longitude = longitude;
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

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public double getCumulativeDistance() {
        return cumulativeDistance;
    }

    public void setCumulativeDistance(double cumulativeDistance) {
        this.cumulativeDistance = cumulativeDistance;
    }

    public int getCumulativeTime() {
        return cumulativeTime;
    }

    public void setCumulativeTime(int cumulativeTime) {
        this.cumulativeTime = cumulativeTime;
    }

    public boolean isInterchange() {
        return interchange;
    }

    public void setInterchange(boolean interchange) {
        this.interchange = interchange;
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
}
