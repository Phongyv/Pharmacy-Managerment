package com.example.pharmacymanagerment;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.Date;

@Entity(tableName = "blood_pressure_records")
public class BloodPressureRecord {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private int systolic;
    private int diastolic;
    private int heartRate;
    private String status;
    private Date date;

    public BloodPressureRecord(int systolic, int diastolic, int heartRate, String status, Date date) {
        this.systolic = systolic;
        this.diastolic = diastolic;
        this.heartRate = heartRate;
        this.status = status;
        this.date = date;
    }

    // Getters and setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSystolic() { return systolic; }
    public void setSystolic(int systolic) { this.systolic = systolic; }
    public int getDiastolic() { return diastolic; }
    public void setDiastolic(int diastolic) { this.diastolic = diastolic; }
    public int getHeartRate() { return heartRate; }
    public void setHeartRate(int heartRate) { this.heartRate = heartRate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
}