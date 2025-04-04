package com.example.pharmacymanagerment;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "period_records")
public class PeriodRecord {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private Date startDate;
    private Date endDate;
    private int cycleLength;
    private int periodLength;
    private String symptoms;
    private String mood;

    // Constructor
    public PeriodRecord(Date startDate, Date endDate, int cycleLength, int periodLength) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.cycleLength = cycleLength;
        this.periodLength = periodLength;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public Date getEndDate() { return endDate; }
    public void setEndDate(Date endDate) { this.endDate = endDate; }
    public int getCycleLength() { return cycleLength; }
    public void setCycleLength(int cycleLength) { this.cycleLength = cycleLength; }
    public int getPeriodLength() { return periodLength; }
    public void setPeriodLength(int periodLength) { this.periodLength = periodLength; }
    public String getSymptoms() { return symptoms; }
    public void setSymptoms(String symptoms) { this.symptoms = symptoms; }
    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }
}