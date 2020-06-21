package com.pratthamarora.fit_tastic.db

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface RunDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRun(run: Run)

    @Delete
    suspend fun deleteRun(run: Run)

    @Query("SELECT * FROM run_db ORDER BY timeStamp DESC")
    fun getAllRunByDate(): LiveData<List<Run>>

    @Query("SELECT * FROM run_db ORDER BY timeInMs DESC")
    fun getAllRunByTimeInMs(): LiveData<List<Run>>

    @Query("SELECT * FROM run_db ORDER BY calories DESC")
    fun getAllRunByCalories(): LiveData<List<Run>>

    @Query("SELECT * FROM run_db ORDER BY avgSpeed DESC")
    fun getAllRunBySpeed(): LiveData<List<Run>>

    @Query("SELECT * FROM run_db ORDER BY distance DESC")
    fun getAllRunByDistance(): LiveData<List<Run>>

    @Query("SELECT SUM(timeInMs) FROM run_db")
    fun getTotalTime(): LiveData<Long>

    @Query("SELECT SUM(calories) FROM run_db")
    fun getTotalCalories(): LiveData<Int>

    @Query("SELECT SUM(distance) FROM run_db")
    fun getTotalDistance(): LiveData<Int>

    @Query("SELECT AVG(avgSpeed) FROM run_db")
    fun getTotalSpeed(): LiveData<Float>
}