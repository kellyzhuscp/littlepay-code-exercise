package com.scp.demo.service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.scp.demo.model.Tap;
import com.scp.demo.model.Trip;
import com.scp.demo.model.enums.TapType;
import com.scp.demo.model.enums.TripStatus;

@Service
@Slf4j
public class TripProcessorService {
    private final FareService fareService;

    @Autowired
    public TripProcessorService(FareService fareService) {
        this.fareService = fareService;
    }

    public List<Trip> processTaps(List<Tap> taps) {
        log.info("Processing {} taps", taps.size());
        
        // Create a new modifiable list from the input
        List<Tap> modifiableTaps = new ArrayList<>(taps);
        
        // Ensure taps are sorted by time to maintain correct order
        modifiableTaps.sort(Comparator.comparing(Tap::getDateTimeUTC));
        log.debug("Sorted taps by timestamp");

        // Result list of trips
        List<Trip> trips = new ArrayList<>();

        // Map of active ON taps, keyed by PAN
        Map<String, Tap> activeOnTaps = new HashMap<>();

        for (Tap tap : modifiableTaps) {

            String pan = tap.getPan();
            log.debug("Processing tap: id={}, type={}, stop={}, PAN={}", 
                tap.getId(), tap.getTapType(), tap.getStopId(), pan);

            if (tap.getTapType() == TapType.ON) {
                // When the tap is ON, and the PAN is already in the activeOnTaps map
                if (activeOnTaps.containsKey(pan)) {
                    // Then we have an INCOMPLETE trip
                    Tap unmatchedOn = activeOnTaps.remove(pan);
                    log.info("Found incomplete trip for PAN={} at stop={}", 
                        unmatchedOn.getPan(), unmatchedOn.getStopId());
                    trips.add(createIncompleteTrip(unmatchedOn));
                }
                // Add the ON tap to the activeOnTaps map
                activeOnTaps.put(pan, tap);
                log.debug("Added ON tap to active taps for PAN={}", pan);
            } else if (tap.getTapType() == TapType.OFF) {
                // When the tap is OFF, a PAN must be in the activeOnTaps map
                if (activeOnTaps.containsKey(pan)) {
                    Tap onTap = activeOnTaps.remove(pan);
                    log.debug("Found matching ON tap for PAN={}", pan);

                    if (onTap.getStopId().equals(tap.getStopId())) {
                        log.info("Creating cancelled trip for PAN={} at stop={}", 
                            pan, tap.getStopId());
                        trips.add(createCancelledTrip(onTap, tap));
                    } else {
                        log.info("Creating completed trip for PAN={} from stop={} to stop={}", 
                            pan, onTap.getStopId(), tap.getStopId());
                        trips.add(createCompletedTrip(onTap, tap));
                    }
                } else {
                    log.error("Found OFF tap without matching ON tap for PAN={} at stop={}", 
                        pan, tap.getStopId());
                }
            }
        }

        // Handle remaining ON taps
        for (Tap onTap : activeOnTaps.values()) {
            log.info("Creating incomplete trip for remaining ON tap: PAN={} at stop={}", 
                onTap.getPan(), onTap.getStopId());
            trips.add(createIncompleteTrip(onTap));
        }

        log.info("Processed {} taps into {} trips", taps.size(), trips.size());
        return trips;
    }

    private Trip createCompletedTrip(Tap on, Tap off) {
        BigDecimal fare = fareService.getFare(on.getStopId(), off.getStopId());
        long duration = Duration.between(on.getDateTimeUTC(), off.getDateTimeUTC()).getSeconds();
        log.debug("Calculated fare={} and duration={}s for completed trip for PAN={}", 
            fare, duration, on.getPan());

        return Trip.builder()
                .started(on.getDateTimeUTC())
                .finished(off.getDateTimeUTC())
                .durationSecs(duration)
                .fromStopId(on.getStopId())
                .toStopId(off.getStopId())
                .chargeAmount(fare)
                .companyId(on.getCompanyId())
                .busId(on.getBusId())
                .pan(on.getPan())
                .status(TripStatus.COMPLETED)
                .build();
    }

    private Trip createIncompleteTrip(Tap on) {
        BigDecimal maxFare = fareService.getMaxFareFromStop(on.getStopId());
        log.debug("Using max fare={} for incomplete trip from stop={} for PAN={}", 
            maxFare, on.getStopId(), on.getPan());
        
        return Trip.builder()
                .started(on.getDateTimeUTC())
                .finished(null)
                .durationSecs(0L)
                .fromStopId(on.getStopId())
                .toStopId(null)
                .chargeAmount(maxFare)
                .companyId(on.getCompanyId())
                .busId(on.getBusId())
                .pan(on.getPan())
                .status(TripStatus.INCOMPLETE)
                .build();
    }

    private Trip createCancelledTrip(Tap on, Tap off) {
        long duration = Duration.between(on.getDateTimeUTC(), off.getDateTimeUTC()).getSeconds();
        log.debug("Calculated duration={}s for cancelled trip at stop={} for PAN={}", 
            duration, on.getStopId(), on.getPan());
        
        return Trip.builder()
                .started(on.getDateTimeUTC())
                .finished(off.getDateTimeUTC())
                .durationSecs(duration)
                .fromStopId(on.getStopId())
                .toStopId(off.getStopId())
                .chargeAmount(BigDecimal.ZERO)
                .companyId(on.getCompanyId())
                .busId(on.getBusId())
                .pan(on.getPan())
                .status(TripStatus.CANCELLED)
                .build();
    }
}
