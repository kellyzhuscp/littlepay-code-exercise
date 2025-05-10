package com.scp.demo.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import com.scp.demo.model.Tap;
import com.scp.demo.model.Trip;
import com.scp.demo.model.enums.TapType;
import com.scp.demo.model.enums.TripStatus;

@ExtendWith(MockitoExtension.class)
class TripProcessorServiceTest {
    @InjectMocks
    private TripProcessorService tripProcessorService;

    @Mock
    private FareService fareService;


    @Test
    void shouldCreateCompletedTrip() {
        // Test a simple ON and OFF at different stops: Stop1 → Stop2
        // Expected: a completed trip with the correct fare
        List<Tap> taps = List.of(
            new Tap(1, LocalDateTime.parse("2023-01-22T13:00:00"), TapType.ON, "Stop1", "Company1", "Bus37", "5500005555555559"),
            new Tap(2, LocalDateTime.parse("2023-01-22T13:05:00"), TapType.OFF, "Stop2", "Company1", "Bus37", "5500005555555559")
        );

        List<Trip> trips = tripProcessorService.processTaps(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.get(0);

        assertEquals(TripStatus.COMPLETED, trip.getStatus());
        assertEquals(new BigDecimal("3.25"), trip.getChargeAmount());
    }

    @Test
    void shouldCreateIncompleteTripWhenTapOffIsMissing() {
        // Test a scenario with only a tap ON, no tap OFF
        // Expected: incomplete trip with max fare from Stop3

        List<Tap> taps = List.of(
            new Tap(3, LocalDateTime.parse("2023-01-22T09:20:00"), TapType.ON, "Stop3", "Company1", "Bus36", "4111111111111111")
        );

        when(fareService.getMaxFareFromStop("Stop3")).thenReturn(new BigDecimal("7.30"));

        List<Trip> trips = tripProcessorService.processTaps(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.get(0);
        assertEquals(TripStatus.INCOMPLETE, trip.getStatus());
        assertEquals(new BigDecimal("7.30"), trip.getChargeAmount());
        assertEquals("Stop3", trip.getFromStopId());
        assertNull(trip.getToStopId());
        assertEquals("4111111111111111", trip.getPan());
    }

    @Test
    void shouldCreateCancelledTripWhenSameStopTapped() {
        // Test ON and OFF at the same stop: Stop1 → Stop1
        // Expected: cancelled trip with $0 charge

        List<Tap> taps = List.of(
            new Tap(4, LocalDateTime.parse("2023-01-23T08:00:00"), TapType.ON, "Stop1", "Company1", "Bus37", "4111111111111111"),
            new Tap(5, LocalDateTime.parse("2023-01-23T08:02:00"), TapType.OFF, "Stop1", "Company1", "Bus37", "4111111111111111")
        );

        List<Trip> trips = tripProcessorService.processTaps(taps);

        assertEquals(1, trips.size());
        Trip trip = trips.get(0);
        assertEquals(TripStatus.CANCELLED, trip.getStatus());
        assertEquals(BigDecimal.ZERO, trip.getChargeAmount());
        assertEquals("Stop1", trip.getFromStopId());
        assertEquals("Stop1", trip.getToStopId());
    }

    @Test
    void shouldCreateTwoIncompleteTripsForTwoConsecutiveOns() {
        //  Test two ONs with no OFF in between
        // Expected: two separate incomplete trips, each charged max fare from respective stops

        List<Tap> taps = List.of(
            new Tap(6, LocalDateTime.parse("2023-01-24T10:00:00"), TapType.ON, "Stop1", "Company1", "Bus10", "4222222222222222"),
            new Tap(7, LocalDateTime.parse("2023-01-24T11:00:00"), TapType.ON, "Stop2", "Company1", "Bus11", "4222222222222222")
        );

        when(fareService.getMaxFareFromStop("Stop1")).thenReturn(new BigDecimal("7.30"));
        when(fareService.getMaxFareFromStop("Stop2")).thenReturn(new BigDecimal("5.50"));

        List<Trip> trips = tripProcessorService.processTaps(taps);

        assertEquals(2, trips.size());

        Trip firstTrip = trips.get(0);
        assertEquals(TripStatus.INCOMPLETE, firstTrip.getStatus());
        assertEquals("Stop1", firstTrip.getFromStopId());
        assertNull(firstTrip.getToStopId());
        assertEquals(new BigDecimal("7.30"), firstTrip.getChargeAmount());

        Trip secondTrip = trips.get(1);
        assertEquals(TripStatus.INCOMPLETE, secondTrip.getStatus());
        assertEquals("Stop2", secondTrip.getFromStopId());
        assertNull(secondTrip.getToStopId());
        assertEquals(new BigDecimal("5.50"), secondTrip.getChargeAmount());
    }

    @Test
    void shouldCreateCompletedAndIncompleteTripsForOnOffThenOn() {
        // Test a case with ON → OFF (completed), followed by another ON (incomplete)
        List<Tap> taps = List.of(
            new Tap(8, LocalDateTime.parse("2023-01-25T07:30:00"), TapType.ON, "Stop1", "Company1", "Bus20", "4333333333333333"),
            new Tap(9, LocalDateTime.parse("2023-01-25T07:45:00"), TapType.OFF, "Stop2", "Company1", "Bus20", "4333333333333333"),
            new Tap(10, LocalDateTime.parse("2023-01-25T08:10:00"), TapType.ON, "Stop3", "Company1", "Bus21", "4333333333333333")
        );

        when(fareService.getFare("Stop1", "Stop2")).thenReturn(new BigDecimal("3.25"));
        when(fareService.getMaxFareFromStop("Stop3")).thenReturn(new BigDecimal("7.30"));

        List<Trip> trips = tripProcessorService.processTaps(taps);

        assertEquals(2, trips.size());

        Trip firstTrip = trips.get(0);
        assertEquals(TripStatus.COMPLETED, firstTrip.getStatus());
        assertEquals("Stop1", firstTrip.getFromStopId());
        assertEquals("Stop2", firstTrip.getToStopId());
        assertEquals(new BigDecimal("3.25"), firstTrip.getChargeAmount());

        Trip secondTrip = trips.get(1);
        assertEquals(TripStatus.INCOMPLETE, secondTrip.getStatus());
        assertEquals("Stop3", secondTrip.getFromStopId());
        assertNull(secondTrip.getToStopId());
        assertEquals(new BigDecimal("7.30"), secondTrip.getChargeAmount());
    }
}
