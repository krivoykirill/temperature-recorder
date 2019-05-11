package com.example.temptracker


import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup

class ListAdapter(private val list: List<Measurement>)
    : RecyclerView.Adapter<MeasurementViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MeasurementViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: MeasurementViewHolder, position: Int) {
        val measurement: Measurement = list[position]
        holder.bind(measurement)
    }

    override fun getItemCount(): Int = list.size

}