package com.scp.demo.service;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import com.scp.demo.model.Trip;
import com.scp.demo.model.enums.TripStatus;

@SpringBootTest
class TripCsvWriterServiceTest {

    @Autowired
    private TripCsvWriterService tripCsvWriterService;

    @Test
    void shouldWriteTripsToCsvFile() throws IOException {
        List<Trip> trips = List.of(
            Trip.builder()
                .started(LocalDateTime.of(2023, 1, 22, 13, 0))
                .finished(LocalDateTime.of(2023, 1, 22, 13, 5))
                .durationSecs(300L)
                .fromStopId("Stop1")
                .toStopId("Stop2")
                .chargeAmount(new BigDecimal("3.25"))
                .companyId("Company1")
                .busId("Bus37")
                .pan("5500005555555559")
                .status(TripStatus.COMPLETED)
                .build()
        );

        // Create a temporary file for testing
        Path tempFile = Files.createTempFile("trips", ".csv");

        // Call the method to write trips
        tripCsvWriterService.writeTrips(trips, tempFile.toString());

        // Read the file and assert
        List<String> lines = Files.readAllLines(tempFile);
        assertTrue(lines.size() >= 2); // header + at least one trip

        // Check if the line contains expected data
        assertTrue(lines.get(1).contains("Stop1"));

        // Clean up: delete the temporary file
        Files.deleteIfExists(tempFile);
    }
}

