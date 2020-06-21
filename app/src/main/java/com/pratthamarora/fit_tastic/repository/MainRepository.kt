package com.pratthamarora.fit_tastic.repository

import com.pratthamarora.fit_tastic.db.Run
import com.pratthamarora.fit_tastic.db.RunDao
import javax.inject.Inject

class MainRepository @Inject constructor(private val runDao: RunDao) {

    suspend fun insertRun(run: Run) = runDao.insertRun(run)
    suspend fun deleteRun(run: Run) = runDao.deleteRun(run)

    fun getAllRunSortByDate() = runDao.getAllRunByDate()
    fun getAllRunSortByCalories() = runDao.getAllRunByCalories()
    fun getAllRunSortByDistance() = runDao.getAllRunByDistance()
    fun getAllRunSortBySpeed() = runDao.getAllRunBySpeed()
    fun getAllRunSortByTimeInMs() = runDao.getAllRunByTimeInMs()

    fun getTotalAvgSpeed() = runDao.getTotalSpeed()
    fun getTotalCalories() = runDao.getTotalCalories()
    fun getTotalDistance() = runDao.getTotalDistance()
    fun getTotalTime() = runDao.getTotalTime()
}