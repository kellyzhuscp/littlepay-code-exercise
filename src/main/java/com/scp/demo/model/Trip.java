package com.scp.demo.model;

import java.time.LocalDateTime;
import java.math.BigDecimal;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import com.opencsv.bean.CsvDate;
import com.scp.demo.model.enums.TripStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Trip {
    @CsvDate("dd-MM-yyyy HH:mm:ss")
    private LocalDateTime started;

    @CsvDate("dd-MM-yyyy HH:mm:ss")
    private LocalDateTime finished;

    private Long durationSecs;

    private String fromStopId;

    private String toStopId;

    private BigDecimal chargeAmount;

    private String companyId;

    private String busId;

    private String pan;

    private TripStatus status;
}
