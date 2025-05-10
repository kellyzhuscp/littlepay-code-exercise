package com.scp.demo.service;

import java.io.FileReader;
import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.opencsv.bean.CsvToBeanBuilder;
import com.scp.demo.model.Tap;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
// parses taps.csv into a List<Tap>
public class TapCsvReaderService {
    public List<Tap> readTaps(String filePath) throws IOException {
        log.info("Reading taps from file: {}", filePath);
        List<Tap> taps = new ArrayList<>();

        try (FileReader reader = new FileReader(filePath)) {
            log.debug("Initializing CSV reader");
            var csvToBean = new CsvToBeanBuilder<Tap>(reader)
                    .withType(Tap.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreQuotations(true)
                    .build();

            var iterator = csvToBean.iterator();
            int lineNumber = 1;

            while (iterator.hasNext()) {
                try {
                    Tap tap = iterator.next();
                    if (tap.getPan() != null) {
                        tap.setPan(tap.getPan().trim());
                    }
                    taps.add(tap);
                    lineNumber++;
                } catch (RuntimeException ex) {
                    log.error("Failed to parse CSV line {}: {}", lineNumber, ex.getMessage());
                }
            }

            log.info("Successfully read {} taps from CSV", taps.size());
        } catch(DateTimeParseException ex) {
            log.error("Failed to parse date in CSV: {}", ex.getMessage());
        } catch (Exception e) {
            log.error("Failed to open or read file {}: {}", filePath, e.getMessage());
            throw new RuntimeException("Could not read input file", e);
        }

        return taps;
    }
    
}
