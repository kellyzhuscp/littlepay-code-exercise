package com.scp.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.scp.demo.model.Tap;
import com.scp.demo.model.enums.TapType;

@SpringBootTest
class TapCsvReaderServiceTest {

    @Autowired
    private TapCsvReaderService tapCsvReaderService;

    @Test
    void shouldReadTapsFromCsv() throws IOException {
        // Given
        String filePath = "src/test/resources/taps.csv";

        // When
        List<Tap> taps = tapCsvReaderService.readTaps(filePath);

        // Then
        assertNotNull(taps);
        assertEquals(6, taps.size());

        // Verify first tap
        Tap firstTap = taps.get(0);
        assertEquals(1, firstTap.getId());
        assertEquals(TapType.ON, firstTap.getTapType());
        assertEquals("Stop1", firstTap.getStopId());
        assertEquals("Company1", firstTap.getCompanyId());
        assertEquals("Bus37", firstTap.getBusId());
        assertEquals("5500005555555559", firstTap.getPan());

        // Verify last tap
        Tap lastTap = taps.get(5);
        assertEquals(6, lastTap.getId());
        assertEquals(TapType.OFF, lastTap.getTapType());
        assertEquals("Stop2", lastTap.getStopId());
        assertEquals("Company1", lastTap.getCompanyId());
        assertEquals("Bus37", lastTap.getBusId());
        assertEquals("5500005555555559", lastTap.getPan());
    }
} 