package com.scp.demo.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.stereotype.Service;

@Service
// calculates trip price and max fare for incomplete trips.
public class FareService {
    final Map<Set<String>, BigDecimal> fareMap = new HashMap<>();
    
    // Initialize the fare data
    public void initializeFareMap() {
        fareMap.put(new HashSet<>(Arrays.asList("Stop1", "Stop2")), new BigDecimal("3.25"));
        fareMap.put(new HashSet<>(Arrays.asList("Stop2", "Stop3")), new BigDecimal("5.50"));
        fareMap.put(new HashSet<>(Arrays.asList("Stop1", "Stop3")), new BigDecimal("7.30"));
    }

    // Retrieve fare between two stops
    public BigDecimal getFare(String fromStop, String toStop) {
        return BigDecimal.ZERO;
    }

    // Get max fare from a stop to any other stop for incomplete trips
    public BigDecimal getMaxFareFromStop(String stopId) {
        return BigDecimal.ZERO;        
    }

}
