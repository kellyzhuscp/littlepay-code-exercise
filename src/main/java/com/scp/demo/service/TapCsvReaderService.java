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
        List<Tap> taps = new ArrayList<>();

        try (FileReader reader = new FileReader(filePath)) {
            var csvToBean = new CsvToBeanBuilder<Tap>(reader)
                    .withType(Tap.class)
                    .withIgnoreLeadingWhiteSpace(true)
                    .withIgnoreQuotations(true)
                    .build();

            var iterator = csvToBean.iterator();

            while (iterator.hasNext()) {
                try {
                    Tap tap = iterator.next();
                    if (tap.getPan() != null) {
                        tap.setPan(tap.getPan().trim());
                    }
                    taps.add(tap);
                } catch (RuntimeException ex) {
                    log.error("Failed to parse CSV line: {}", ex.getMessage());
                }
            }

        } catch(DateTimeParseException ex) {
            log.error("Failed to parse CSV line: {}", ex.getMessage());
        } catch (Exception e) {
            log.error("Failed to open or read file: {}", e.getMessage());
            throw new RuntimeException("Could not read input file", e);
        }

        return taps;
    }
    
}
