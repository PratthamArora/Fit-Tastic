package com.pratthamarora.fit_tastic.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.pratthamarora.fit_tastic.R
import com.pratthamarora.fit_tastic.utils.Constants.FIRST_RUN
import com.pratthamarora.fit_tastic.utils.Constants.KEY_NAME
import com.pratthamarora.fit_tastic.utils.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_setup.*
import javax.inject.Inject


@AndroidEntryPoint
class SetupFragment : Fragment(R.layout.fragment_setup) {

    @Inject
    lateinit var sharePrefs: SharedPreferences

    @set:Inject
    var isFirstRun = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isFirstRun) {
            val options = NavOptions.Builder()
                .setPopUpTo(R.id.setupFragment, true)
                .build()
            findNavController().navigate(
                R.id.action_setupFragment_to_runFragment,
                savedInstanceState,
                options
            )
        }

        tvContinue.setOnClickListener {
            val isSuccess = saveDataToSharedPrefs()
            if (isSuccess) {
                findNavController().navigate(R.id.action_setupFragment_to_runFragment)
            } else {
                Snackbar.make(requireView(), "Please enter all fields", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun saveDataToSharedPrefs(): Boolean {
        val name = etName.text.toString()
        val weight = etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }
        sharePrefs.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .putBoolean(FIRST_RUN, false)
            .apply()

        return true
    }
}