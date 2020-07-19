package com.pratthamarora.fit_tastic.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pratthamarora.fit_tastic.db.Run
import com.pratthamarora.fit_tastic.repository.MainRepository
import com.pratthamarora.fit_tastic.utils.SortType
import kotlinx.coroutines.launch

class MainViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {

    private val runsByDate = mainRepository.getAllRunSortByDate()
    private val runsByDistance = mainRepository.getAllRunSortByDistance()
    private val runsByCalories = mainRepository.getAllRunSortByCalories()
    private val runsByTimeInMs = mainRepository.getAllRunSortByTimeInMs()
    private val runsByAvgSpeed = mainRepository.getAllRunSortBySpeed()

    val runs = MediatorLiveData<List<Run>>()

    var sortType = SortType.DATE

    init {
        runs.addSource(runsByDate) {
            if (sortType == SortType.DATE) {
                it?.let { list ->
                    runs.value = list
                }
            }
        }
        runs.addSource(runsByAvgSpeed) {
            if (sortType == SortType.AVG_SPEED) {
                it?.let { list ->
                    runs.value = list
                }
            }
        }
        runs.addSource(runsByCalories) {
            if (sortType == SortType.CALORIES) {
                it?.let { list ->
                    runs.value = list
                }
            }
        }
        runs.addSource(runsByTimeInMs) {
            if (sortType == SortType.RUNNING_TIME) {
                it?.let { list ->
                    runs.value = list
                }
            }
        }
        runs.addSource(runsByDistance) {
            if (sortType == SortType.DISTANCE) {
                it?.let { list ->
                    runs.value = list
                }
            }
        }
    }

    fun sortRuns(sortType: SortType) = when (sortType) {
        SortType.DATE -> runsByDate.value?.let { runs.value = it }
        SortType.DISTANCE -> runsByDistance.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runsByTimeInMs.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runsByAvgSpeed.value?.let { runs.value = it }
        SortType.CALORIES -> runsByCalories.value?.let { runs.value = it }
    }.also {
        this.sortType = sortType
    }


    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }
}