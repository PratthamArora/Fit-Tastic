package com.pratthamarora.fit_tastic.ui.fragments

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.pratthamarora.fit_tastic.R
import com.pratthamarora.fit_tastic.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TrackingFragment : Fragment(R.layout.fragment_tracking) {
    private val viewModel: MainViewModel by viewModels()

}