package com.example.pharmacymanagerment;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface PeriodDao {
    @Insert
    void insert(PeriodRecord record);

    @Query("SELECT * FROM period_records ORDER BY startDate DESC")
    List<PeriodRecord> getAllRecords();

    @Query("DELETE FROM period_records WHERE id = :id")
    void delete(int id);
}