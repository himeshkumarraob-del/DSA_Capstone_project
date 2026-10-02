package com.metropath.model;

/**
 * RouteRequest encapsulates the user input from the frontend.
 * preference: "FASTEST", "SHORTEST_DISTANCE", or "FEWEST_STOPS"
 */
public class RouteRequest {
    private String source;
    private String destination;
    private String preference;

    public RouteRequest() {
    }

    public RouteRequest(String source, String destination, String preference) {
        this.source = source;
        this.destination = destination;
        this.preference = preference;
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

    public String getPreference() {
        return preference;
    }

    public void setPreference(String preference) {
        this.preference = preference;
    }

    @Override
    public String toString() {
        return "RouteRequest{" +
                "source='" + source + '\'' +
                ", destination='" + destination + '\'' +
                ", preference='" + preference + '\'' +
                '}';
    }
}
