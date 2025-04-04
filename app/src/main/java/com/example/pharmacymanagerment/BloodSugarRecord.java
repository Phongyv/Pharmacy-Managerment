package com.example.pharmacymanagerment;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "blood_sugar_records")
public class BloodSugarRecord {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private float sugarLevel;
    private String measurementTime;
    private String status;
    private Date date;

    public BloodSugarRecord(float sugarLevel, String measurementTime, String status, Date date) {
        this.sugarLevel = sugarLevel;
        this.measurementTime = measurementTime;
        this.status = status;
        this.date = date;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public float getSugarLevel() { return sugarLevel; }
    public void setSugarLevel(float sugarLevel) { this.sugarLevel = sugarLevel; }
    public String getMeasurementTime() { return measurementTime; }
    public void setMeasurementTime(String measurementTime) { this.measurementTime = measurementTime; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}