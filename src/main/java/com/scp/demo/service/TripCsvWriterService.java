package com.scp.demo.service;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.stereotype.Service;

import com.opencsv.CSVWriter;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.scp.demo.model.Trip;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
// writes List<Trip> into trips.csv
public class TripCsvWriterService {

    public void writeTrips(List<Trip> trips, String filePath) throws IOException {
        log.info("Writing {} trips to file: {}", trips.size(), filePath);
        try (Writer writer = Files.newBufferedWriter(Path.of(filePath))) {
            // Write CSV header manually
            writer.write("Started,Finished,DurationSecs,FromStopId,ToStopId,ChargeAmount,CompanyId,BusID,PAN,Status\n");
            log.debug("Wrote CSV header");

            // Define the column order mapping
            ColumnPositionMappingStrategy<Trip> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Trip.class);
            strategy.setColumnMapping(
                "started", "finished", "durationSecs", "fromStopId", "toStopId",
                "chargeAmount", "companyId", "busId", "pan", "status"
            );
            log.debug("Configured column mapping strategy");

            // Configure the CSV writer
            StatefulBeanToCsv<Trip> beanToCsv = new StatefulBeanToCsvBuilder<Trip>(writer)
                    .withMappingStrategy(strategy)
                    .withQuotechar(CSVWriter.NO_QUOTE_CHARACTER)
                    .withOrderedResults(true)
                    .withApplyQuotesToAll(false)
                    .build();
            log.debug("Configured CSV writer");

            // Write the data
            beanToCsv.write(trips);
            log.info("Successfully wrote {} trips to CSV", trips.size());
        } catch (Exception e) {
            log.error("Failed to write trips to CSV: {}", e.getMessage(), e);
            throw new IOException("Failed to write trips to CSV", e);
        }
    }


    
}
