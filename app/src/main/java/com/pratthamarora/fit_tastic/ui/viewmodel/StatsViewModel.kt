package com.pratthamarora.fit_tastic.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.pratthamarora.fit_tastic.repository.MainRepository

class StatsViewModel @ViewModelInject constructor(
    val mainRepository: MainRepository
) : ViewModel() {
}