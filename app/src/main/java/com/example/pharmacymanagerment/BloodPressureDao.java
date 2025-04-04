package com.example.pharmacymanagerment;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface BloodPressureDao {
    @Insert
    void insert(BloodPressureRecord record);

    @Query("SELECT * FROM blood_pressure_records ORDER BY date DESC")
    List<BloodPressureRecord> getAllRecords();

    @Query("DELETE FROM blood_pressure_records WHERE id = :id")
    void delete(int id);
}
