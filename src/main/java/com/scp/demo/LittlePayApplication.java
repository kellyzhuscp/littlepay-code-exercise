package com.scp.demo;

import java.io.IOException;
import java.util.List;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.scp.demo.model.Tap;
import com.scp.demo.model.Trip;
import com.scp.demo.service.TapCsvReaderService;
import com.scp.demo.service.TripCsvWriterService;
import com.scp.demo.service.TripProcessorService;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class LittlePayApplication {

	public static void main(String[] args) {
		SpringApplication.run(LittlePayApplication.class, args);
	}

	@Bean
    public CommandLineRunner run(TapCsvReaderService tapCsvReaderService,
                                 TripProcessorService tripProcessorService,
                                 TripCsvWriterService tripCsvWriterService,
								 ApplicationArguments args) {
        return arg -> {
            // Default file paths
            String inputFilePath = "src/main/resources/taps.csv";
            String outputFilePath = "src/main/resources/trips.csv";

            // Check if there are arguments provided and override the defaults
            if (args.containsOption("input")) {
                inputFilePath = args.getOptionValues("input").get(0);
                log.info("Using custom input file: {}", inputFilePath);
            } else {
                log.info("Using default input file: {}", inputFilePath);
            }
            
            if (args.containsOption("output")) {
                outputFilePath = args.getOptionValues("output").get(0);
                log.info("Using custom output file: {}", outputFilePath);
            } else {
                log.info("Using default output file: {}", outputFilePath);
            }

            try {
                // Step 1: Read the input file (taps.csv)
                log.info("Step 1: Reading taps from CSV");
                List<Tap> taps = tapCsvReaderService.readTaps(inputFilePath);
                log.info("Read {} taps from input file", taps.size());

                // Step 2: Process the records (taps) using the TripProcessorService
                log.info("Step 2: Processing taps into trips");
                List<Trip> trips = tripProcessorService.processTaps(taps);
                log.info("Processed {} taps into {} trips", taps.size(), trips.size());

                // Step 3: Write the processed records to the output file (trips.csv)
                log.info("Step 3: Writing trips to CSV");
                tripCsvWriterService.writeTrips(trips, outputFilePath);
                log.info("Successfully wrote {} trips to output file", trips.size());

                log.info("Processing completed successfully");
            } catch (IOException e) {
                log.error("Error processing files: {}", e.getMessage(), e);
                System.err.println("Error processing the file: " + e.getMessage());
            }        
        };
    }

}
