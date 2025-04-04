package com.example.pharmacymanagerment;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface BloodSugarDao {
    @Insert
    void insert(BloodSugarRecord record);

    @Query("SELECT * FROM blood_sugar_records ORDER BY date DESC")
    List<BloodSugarRecord> getAllRecords();

    @Query("DELETE FROM blood_sugar_records WHERE id = :id")
    void delete(int id);
}