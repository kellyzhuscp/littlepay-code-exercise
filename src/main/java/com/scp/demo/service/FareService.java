package com.scp.demo.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
// calculates trip price and max fare for incomplete trips.
public class FareService {
    final Map<Set<String>, BigDecimal> fareMap = new HashMap<>();
    
    @Autowired
    public FareService() {
        log.info("Initializing FareService");
        initializeFareMap();
    }

    // Initialize the fare data
    public void initializeFareMap() {
        log.debug("Initializing fare map with default values");
        fareMap.put(new HashSet<>(Arrays.asList("Stop1", "Stop2")), new BigDecimal("3.25"));
        fareMap.put(new HashSet<>(Arrays.asList("Stop2", "Stop3")), new BigDecimal("5.50"));
        fareMap.put(new HashSet<>(Arrays.asList("Stop1", "Stop3")), new BigDecimal("7.30"));
        log.info("Fare map initialized with {} routes", fareMap.size());
    }

    // Retrieve fare between two stops
    public BigDecimal getFare(String fromStop, String toStop) {
        Set<String> stopPair = new HashSet<>(Arrays.asList(fromStop, toStop));
        BigDecimal fare = fareMap.getOrDefault(stopPair, BigDecimal.ZERO);
        
        if (fare.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("No fare found for route from {} to {}", fromStop, toStop);
        } else {
            log.debug("Retrieved fare {} for route from {} to {}", fare, fromStop, toStop);
        }
        
        return fare;
    }

    // Get max fare from a stop to any other stop for incomplete trips
    public BigDecimal getMaxFareFromStop(String stopId) {
        log.debug("Calculating max fare from stop {}", stopId);
        
        BigDecimal maxFare = fareMap.keySet().stream()
                .filter(pair -> pair.contains(stopId)) // Only keep pairs containing the stopId
                .map(fareMap::get)
                .max(Comparator.naturalOrder()) // Find the max fare
                .orElse(BigDecimal.ZERO);
        
        if (maxFare.compareTo(BigDecimal.ZERO) == 0) {
            log.warn("No fares found for stop {}", stopId);
        } else {
            log.debug("Max fare from stop {} is {}", stopId, maxFare);
        }
        
        return maxFare;
    }
}
