package com.pratthamarora.fit_tastic.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.pratthamarora.fit_tastic.repository.MainRepository

class StatsViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    val totalTimeRun = mainRepository.getTotalTime()
    val totalDistance = mainRepository.getTotalDistance()
    val totalSpeed = mainRepository.getTotalAvgSpeed()
    val totalCalories = mainRepository.getTotalCalories()

    val sortedByDate = mainRepository.getAllRunSortByDate()
}