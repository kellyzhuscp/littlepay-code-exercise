package com.scp.demo.util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;

public class TapTestDataGenerator {
    public static void main(String[] args) throws IOException {
        List<String> stops = List.of("Stop1", "Stop2", "Stop3");
        List<String> busIds = List.of("Bus10", "Bus20", "Bus30");
        List<String> companies = List.of("Company1", "Company2");
        List<String> pans = List.of("4000000000000001", "4000000000000002", "4000000000000003");

        try (BufferedWriter writer = Files.newBufferedWriter(Path.of("taps-test.csv"))) {
            writer.write("ID,DateTimeUTC,TAPType,StopId,CompanyId,BusID,PAN\n");

            int id = 1;
            LocalDateTime baseTime = LocalDateTime.of(2025, 1, 1, 8, 0);
            Random random = new Random();

            for (int i = 0; i < 50; i++) { // 50 trips → 100 rows
                String stopOn = stops.get(random.nextInt(stops.size()));
                String stopOff;
                do {
                    stopOff = stops.get(random.nextInt(stops.size()));
                } while (stopOff.equals(stopOn));

                String busId = busIds.get(random.nextInt(busIds.size()));
                String company = companies.get(random.nextInt(companies.size()));
                String pan = pans.get(random.nextInt(pans.size()));

                LocalDateTime onTime = baseTime.plusMinutes(i * 5);
                LocalDateTime offTime = onTime.plusMinutes(3 + random.nextInt(10));

                writer.write(String.format(
                        "%d,%s,ON,%s,%s,%s,%s\n",
                        id++, onTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                        stopOn, company, busId, pan
                ));

                writer.write(String.format(
                        "%d,%s,OFF,%s,%s,%s,%s\n",
                        id++, offTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")),
                        stopOff, company, busId, pan
                ));
            }
        }

        System.out.println("Generated taps.csv with 100 rows.");
    }
}
