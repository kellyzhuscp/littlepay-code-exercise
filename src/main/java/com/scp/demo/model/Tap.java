package com.scp.demo.model;

import java.time.LocalDateTime;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;

import com.scp.demo.model.enums.TapType;

public class Tap {
    @CsvBindByName(column = "ID")
    private int id;
    // TODO think about timezone. 
    @CsvBindByName(column = "DateTimeUTC")
    @CsvDate("yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dateTimeUTC;
    @CsvBindByName(column = "TapType")
    private TapType tapType; // ON or OFF
    @CsvBindByName(column = "StopId")
    private String stopId;
    @CsvBindByName(column = "CompanyId")
    private String companyId;
    @CsvBindByName(column = "BusID")
    private String busId;
    @CsvBindByName(column = "PAN")
    private String pan;

    // Default constructor required by OpenCSV
    public Tap() {}

    public Tap(int id, LocalDateTime dateTimeUTC, TapType tapType, String stopId, String companyId, String busId, String pan) {
        this.id = id;
        this.dateTimeUTC = dateTimeUTC;
        this.tapType = tapType;
        this.stopId = stopId;
        this.companyId = companyId;
        this.busId = busId;
        this.pan = pan;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public LocalDateTime getDateTimeUTC() { return dateTimeUTC; }
    public void setDateTimeUTC(LocalDateTime dateTimeUTC) { this.dateTimeUTC = dateTimeUTC; }

    public TapType getTapType() { return tapType; }
    public void setTapType(TapType tapType) { this.tapType = tapType; }

    public String getStopId() { return stopId; }
    public void setStopId(String stopId) { this.stopId = stopId; }

    public String getCompanyId() { return companyId; }
    public void setCompanyId(String companyId) { this.companyId = companyId; }

    public String getBusId() { return busId; }
    public void setBusId(String busId) { this.busId = busId; }

    public String getPan() { return pan; }
    public void setPan(String pan) { this.pan = pan; }
}