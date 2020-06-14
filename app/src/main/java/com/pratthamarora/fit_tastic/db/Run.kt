package com.pratthamarora.fit_tastic.db

import android.graphics.Bitmap
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "run_db")
data class Run(
    var img: Bitmap? = null,
    var timeStamp: Long = 0L,
    var avgSpeed: Float = 0f,
    var distance: Int = 0,
    var timeInMs: Long = 0L,
    var calories: Int = 0
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}