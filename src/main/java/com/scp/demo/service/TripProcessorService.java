package com.scp.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scp.demo.model.Tap;
import com.scp.demo.model.Trip;

@Service
public class TripProcessorService {
    private final FareService fareService;

    @Autowired
    public TripProcessorService(FareService fareService) {
        this.fareService = fareService;
    }

    public List<Trip> processTaps(List<Tap> taps) {
        return List.of();
    }
}
