# LittlePay Code Exercise

## Overview

This application reads a CSV file of tap events (`taps.csv`), processes them into bus trips based on business rules, and writes the output to `trips.csv`.

---

## Assumptions

- Taps are ordered chronologically in the input CSV file.
- A **COMPLETED** trip is an ON tap followed by an OFF tap from the same PAN and BusID within a reasonable time window.
- If a tap OFF is missing, the trip is **INCOMPLETE**, and the **maximum fare** from the starting stop is charged.
- If a trip starts and ends at the same stop, it's considered **CANCELED**, and the fare is zero.
- A PAN can tap ON multiple times a day on the same bus — trip key is built using `PAN-BusID-DateTime`.

---

## How to Run

### Prerequisites
- Java 17+
- Gradle 7+

### Steps

```bash
# Build the application
./gradlew clean build

# Run with default file paths (src/main/resources/taps.csv → src/main/resources/trips.csv)
./gradlew bootRun

# Or specify custom input/output paths
./gradlew bootRun --args="--input=your_input.csv --output=your_output.csv"

# Run all unit tests with:
./gradlew test

```

Example Output (trips.csv)

## Example Output (`trips.csv`)

| Started             | Finished            | DurationSecs | FromStopId | ToStopId | ChargeAmount | CompanyId | BusID  | PAN              | Status     |
|---------------------|---------------------|---------------|------------|----------|---------------|-----------|--------|------------------|------------|
| 22-01-2023 13:00:00 | 22-01-2023 13:05:00 | 300           | Stop1      | Stop2    | 3.25          | Company1  | Bus37  | 5500005555555559 | COMPLETED  |
| 22-01-2023 09:20:00 |                     | 0             | Stop3      |          | 7.30          | Company1  | Bus36  | 4111111111111111 | INCOMPLETE |
| 23-01-2023 08:00:00 | 23-01-2023 08:02:00 | 120           | Stop1      | Stop1    | 0.00          | Company1  | Bus37  | 4111111111111111 | CANCELLED  |
