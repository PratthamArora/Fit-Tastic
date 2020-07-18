package com.pratthamarora.fit_tastic.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pratthamarora.fit_tastic.R
import com.pratthamarora.fit_tastic.db.Run
import com.pratthamarora.fit_tastic.utils.Utility
import kotlinx.android.synthetic.main.item_run.view.*
import java.text.SimpleDateFormat
import java.util.*

class RunAdapter : RecyclerView.Adapter<RunAdapter.RunViewHolder>() {

    inner class RunViewHolder(view: View) : RecyclerView.ViewHolder(view)

    private val differCallback = object : DiffUtil.ItemCallback<Run>() {
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean =
            oldItem.hashCode() == newItem.hashCode()
    }

    private val differ = AsyncListDiffer(this, differCallback)

    fun setRuns(list: List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_run, parent, false)
        )
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val run = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(run.img).into(ivRunImage)

            val calender = Calendar.getInstance().apply {
                timeInMillis = run.timeStamp
            }
            val dateFormat = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
            tvDate.text = dateFormat.format(calender.time)
            tvAvgSpeed.text = "${run.avgSpeed} km/h"
            tvDistance.text = "${run.distance / 1000f} km"
            tvTime.text = Utility.getFormattedStopWatchTime(run.timeInMs)
            tvCalories.text = "${run.calories} kcal"
        }
    }
}