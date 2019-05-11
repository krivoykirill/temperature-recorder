package com.example.temptracker

import android.content.Context
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
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.time.LocalDateTime



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
        val listType = object : TypeToken<List<Measurement>>() {

        }.type
        println(json)
        measurements= Gson().fromJson(json, listType)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v =inflater.inflate(R.layout.fragment_stats, container, false)
        val calendar = v.findViewById<CalendarView>(R.id.calendar)
        graph=v.findViewById(R.id.graphic)
        calendar.setOnDateChangeListener{
                view, year, month, dayOfMonth ->
            // Note that months are indexed from 0. So, 0 means January, 1 means february, 2 means march etc.
            val msg = "Selected date is " + dayOfMonth + "/" + (month + 1) + "/" + year
            //Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
            initGraphFromDate(year,month,dayOfMonth,graph)


        }
        return v
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {
        listener?.onFragmentInteraction(uri)
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
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(jsonObj:String) =
            MeasurementFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAMETER, jsonObj)

                }
            }
    }
    fun initGraphFromDate(y:Int, m:Int, d:Int,graph: GraphView){
        val startPoint: LocalDateTime = LocalDateTime.of(y,m+1,d,0,0)
        val  series: LineGraphSeries<DataPoint> = LineGraphSeries<DataPoint>()
        for (measurement in measurements){
            val curPoint = measurement.getLocalDateTime()
            if(startPoint.compareTo(curPoint)==1){
                val x = measurement.date_taken.toDouble()
                val y = measurement.temperature.toDouble()
                series.appendData(DataPoint(x,y),true,measurements.count())
            }
        }

    }
}
