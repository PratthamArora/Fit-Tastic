package com.pratthamarora.fit_tastic.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.pratthamarora.fit_tastic.R
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        supportActionBar?.subtitle = "by - Prattham Arora"
    }
}