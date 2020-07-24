package com.pratthamarora.fit_tastic.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.pratthamarora.fit_tastic.R
import com.pratthamarora.fit_tastic.utils.Constants.KEY_NAME
import com.pratthamarora.fit_tastic.utils.Constants.KEY_WEIGHT
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    @Inject
    lateinit var sharePrefs: SharedPreferences

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadSharedData()

        btnApplyChanges.setOnClickListener {
            val isSuccess = saveNewDataToSharedPrefs()
            if (isSuccess) {
                Snackbar.make(view, "Changes Updated Successfully", Snackbar.LENGTH_LONG)
                    .show()
            } else {
                Snackbar.make(view, "Please enter all fields", Snackbar.LENGTH_LONG)
                    .show()
            }

        }
    }

    private fun saveNewDataToSharedPrefs(): Boolean {
        val name = etName.text.toString()
        val weight = etWeight.text.toString()
        if (name.isEmpty() || weight.isEmpty()) {
            return false
        }
        sharePrefs.edit()
            .putString(KEY_NAME, name)
            .putFloat(KEY_WEIGHT, weight.toFloat())
            .apply()

        return true
    }

    private fun loadSharedData() {
        val name = sharePrefs.getString(KEY_NAME, "")
        val weight = sharePrefs.getFloat(KEY_WEIGHT, 70f)
        etName.setText(name)
        etWeight.setText(weight.toString())
    }
}