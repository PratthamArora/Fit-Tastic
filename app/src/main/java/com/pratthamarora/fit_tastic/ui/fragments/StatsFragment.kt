package com.pratthamarora.fit_tastic.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pratthamarora.fit_tastic.R
import com.pratthamarora.fit_tastic.ui.viewmodel.StatsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatsFragment : Fragment(R.layout.fragment_statistics) {
    private val viewModel: StatsViewModel by viewModels()

}