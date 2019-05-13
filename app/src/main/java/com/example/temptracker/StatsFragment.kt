package com.example.temptracker

import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.Toast
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneId.systemDefault
import java.util.*

import android.text.method.TextKeyListener.clear
import com.jjoe64.graphview.LegendRenderer


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [StatsFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [StatsFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
private const val ARG_PARAMETER = "json"
class StatsFragment : Fragment() {
    lateinit var graph:GraphView
    lateinit var measurements:List<Measurement>
    lateinit var json:String
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        println("STATSFRAGMENT ***********")
        arguments?.let {
            json = it.getString(ARG_PARAMETER)

        }
        val listType = object : TypeToken<List<Measurement>>() {}.type
        println(json)
        measurements= Gson().fromJson(json, listType)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v =inflater.inflate(R.layout.fragment_stats, container, false)
        val calendar = v.findViewById<CalendarView>(R.id.calendar)
        graph=v.findViewById(R.id.graphic) as GraphView
        calendar.setOnDateChangeListener{
                view, year, month, dayOfMonth ->
            val msg = "Selected date is " + dayOfMonth + "/" + (month + 1) + "/" + year
            Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
            initGraphFromDate(year,month,dayOfMonth,graph)
        }
        return v
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }
    override fun onDetach() {
        super.onDetach()
        listener = null
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments]
     * (http://developer.android.com/training/basics/fragments/communicating.html)
     * for more information.
     */
    interface OnFragmentInteractionListener {

        fun onFragmentInteraction(uri: Uri)
    }
    companion object {
        @JvmStatic
        fun newInstance(jsonObj:String) =
            StatsFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAMETER, jsonObj)

                }
            }
    }

    fun localDateTimeToDate(localDateTime: LocalDateTime): Date {
        val calendar = Calendar.getInstance()
        calendar.clear()
        calendar.set(
            localDateTime.year, localDateTime.monthValue - 1, localDateTime.dayOfMonth,
            localDateTime.hour, localDateTime.minute, localDateTime.second
        )
        return calendar.time
    }
    fun initGraphFromDate(y:Int, m:Int, d:Int,graph: GraphView){
        graph.removeAllSeries()
        println("INIT GRAPH")
        val startPoint: LocalDateTime = LocalDateTime.of(y,m+1,d,0,0)
        val  temperature: LineGraphSeries<DataPoint> = LineGraphSeries<DataPoint>()
        val  humidity: LineGraphSeries<DataPoint> = LineGraphSeries<DataPoint>()
        // set manual X bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-100.toDouble());
        graph.getViewport().setMaxY(100.toDouble());

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(measurements[0].date_taken.toDouble());
        graph.getViewport().setMaxX(measurements[measurements.count()-1].date_taken.toDouble());

        // enable scaling and scrolling
        graph.getViewport().setScalable(true);
        graph.getViewport().setScalableY(true);
        for (measurement in measurements){
            val curPoint = measurement.getLocalDateTime()
            if(curPoint.isAfter(startPoint)==true){
                val x = measurement.date_taken.toDouble()
                val y = measurement.temperature.toDouble()
                println("MEASUREMENT -      x: $x y: $y  id: ${measurement.id}")
                temperature.appendData(DataPoint(x,y),true,measurements.count())
                val x1 = measurement.date_taken.toDouble()
                val y1 = measurement.humidity.toDouble()
                humidity.appendData(DataPoint(x1,y1),true,measurements.count())
            }
            else {
            }
        }
        temperature.setTitle("Temperature")
        graph.getLegendRenderer().setVisible(true)
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP)

        temperature.setThickness(8)
        temperature.setColor(Color.RED)
        graph.addSeries(temperature)

        humidity.setThickness(3)
        humidity.setTitle("Humidity")
        humidity.setColor(Color.BLUE)
        graph.addSeries(humidity)
    }
}
