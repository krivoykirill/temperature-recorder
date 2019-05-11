package com.example.temptracker

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView

class MeasurementViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
    RecyclerView.ViewHolder(inflater.inflate(R.layout.fragment_measurement, parent, false)) {
    private var date: TextView? = null
    private var temperat: TextView? = null
    private var humidit: TextView? = null


    init {
        date = itemView.findViewById(R.id.date)
        temperat = itemView.findViewById(R.id.temp)
        humidit = itemView.findViewById(R.id.humid)
    }

    fun bind(meas: Measurement) {
        date?.text=meas.getNormalDate()
        temperat?.text = "Temperature: "+meas.temperature.toString()+"'C"
        humidit?.text = "Humidity: "+meas.humidity.toString()+"%"
    }

}