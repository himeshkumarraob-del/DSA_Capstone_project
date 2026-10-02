package com.metropath;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MetroPathApplication - Entry point for the MetroPath Spring Boot Backend.
 * DSA Capstone Project: Smart Metro Route Finder
 */
@SpringBootApplication
public class MetroPathApplication {

    public static void main(String[] args) {
        SpringApplication.run(MetroPathApplication.class, args);
        System.out.println("=================================================");
        System.out.println("🚇 MetroPath Backend is running on port 8080!");
        System.out.println("DSA Engine: Graph + Adjacency List + BFS + Dijkstra");
        System.out.println("API available at: http://localhost:8080/api/stations");
        System.out.println("=================================================");
    }
}
