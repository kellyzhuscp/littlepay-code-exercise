package com.scp.demo.service;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class FareServiceTest {

    @InjectMocks
    private FareService fareService;

    @Test
    void testFareBetweenStop1AndStop2() {
        BigDecimal fare = fareService.getFare("Stop1", "Stop2");
        assertEquals(new BigDecimal("3.25"), fare);
    }

    @Test
    void testFareBetweenStop2AndStop3() {
        BigDecimal fare = fareService.getFare("Stop2", "Stop3");
        assertEquals(new BigDecimal("5.50"), fare);
    }

    @Test
    void testFareBetweenStop1AndStop3() {
        BigDecimal fare = fareService.getFare("Stop1", "Stop3");
        assertEquals(new BigDecimal("7.30"), fare);
    }

    @Test
    void testFareWithUnknownStop() {
        BigDecimal fare = fareService.getFare("Stop1", "Stop4");
        assertEquals(BigDecimal.ZERO, fare);
    }

    @Test
    void testMaxFareFromStop2() {
        BigDecimal maxFare = fareService.getMaxFareFromStop("Stop2");
        assertEquals(new BigDecimal("5.50"), maxFare);
    }

    @Test
    void testMaxFareFromStop1() {
        BigDecimal maxFare = fareService.getMaxFareFromStop("Stop1");
        assertEquals(new BigDecimal("7.30"), maxFare);
    }

    @Test
    void testMaxFareFromUnknownStop() {
        BigDecimal maxFare = fareService.getMaxFareFromStop("StopX");
        assertEquals(BigDecimal.ZERO, maxFare);
    }

}

