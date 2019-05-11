package com.example.temptracker

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import org.jetbrains.anko.*

import com.google.gson.Gson
import kotlinx.android.synthetic.main.fragment_measurement_list.*
import com.google.gson.reflect.TypeToken



/**
 * A fragment representing a list of Items.
 * Activities containing this fragment MUST implement the
 * [MeasurementFragment.OnListFragmentInteractionListener] interface.
 */
private const val ARG_PARAMETER = "json"

class MeasurementFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1


    var mAdapter: ListAdapter? = null
    lateinit var measurements:List<Measurement>
    lateinit var json:String
    private var j =""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            json = it.getString(ARG_PARAMETER)

        }
        val listType = object : TypeToken<List<Measurement>>() {

        }.type
        println(json)
        measurements=Gson().fromJson(json, listType)
    }


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val mListRecyclerView: RecyclerView
        val view:View=inflater.inflate(R.layout.fragment_measurement_list, container, false)
        mListRecyclerView = view.findViewById(R.id.list);
        mListRecyclerView.setLayoutManager(LinearLayoutManager(getActivity()));

        // only create and set a new adapter if there isn't already one
        if (mAdapter == null) {
            val mAdapter = ListAdapter(measurements);
            mListRecyclerView.setAdapter(mAdapter);
        }
        return view
    }


    // populate the views now that the layout has been inflated
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // RecyclerView node initialized here
        list.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView

            adapter = ListAdapter(measurements)
        }
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
}
