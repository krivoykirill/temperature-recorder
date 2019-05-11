package com.example.temptracker

import android.app.Activity
import android.app.AlarmManager
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.NavigationView
import android.app.Fragment
import android.content.BroadcastReceiver
import android.content.Context
import android.net.Uri
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_main.*

import android.widget.EditText
import android.widget.LinearLayout
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Handler
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import com.google.gson.Gson
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import java.lang.IndexOutOfBoundsException
import java.lang.NullPointerException
import java.sql.Date
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener,StatsFragment.OnFragmentInteractionListener, ServicesFragment.OnFragmentInteractionListener, ListsFragment.OnFragmentInteractionListener {

    lateinit var sql:MyHelper
    lateinit var services:List<Service>
    lateinit var measurements:List<Measurement>
    lateinit var servicesFragment:ServicesFragment
    lateinit var statsFragment:StatsFragment
    private var dateForServicesFrag:String=""
    private lateinit var receiver:BroadcastReceiver



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sql= MyHelper(this)
        services =sql.findServices()

 //to do permissions *******************************************************


        if(services.count()<1){
            val intent = Intent(this, AddActivity::class.java)
            val bundle = Bundle()
            bundle.putBoolean("com.addservice.new",true)
            intent.putExtras(bundle)
            startActivityForResult(intent,0)
        }



        setContentView(R.layout.activity_main)
        val toolbar: Toolbar =findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)


        /*
        val fragManager : FragmentManager = supportFragmentManager
        val fragTran:FragmentTransaction = fragManager.beginTransaction()

        servicesFragment = ServicesFragment()

        val bundl=Bundle()
        bundl.putString("date",dateForServicesFrag)
        servicesFragment.setArguments(bundl)

        fragTran.add(R.id.frameLayout1,servicesFragment)
        fragTran.commit()*/


        dateForServicesFrag = getServiceDate()


        fab.setOnClickListener { view ->
            val intent = Intent(this, AddActivity::class.java)
            startActivityForResult(intent, 1)
            // TO do
        }

        val toggle = ActionBarDrawerToggle(
            this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()



        nav_view.setNavigationItemSelectedListener {
            try {
                when (it.itemId){
                    R.id.weather_services -> {

                        val frag = ServicesFragment.newInstance(dateForServicesFrag)
                        supportFragmentManager.beginTransaction().replace(R.id.frameLayout1, frag).commit()
                    }
                    R.id.records -> {
                        toast("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
                        val frag = StatsFragment.newInstance(Gson().toJson(measurements))
                        supportFragmentManager.beginTransaction().replace(R.id.frameLayout1, frag).commit()
                    }
                    R.id.weather_tracker -> {
                        val frag = MeasurementFragment.newInstance(Gson().toJson(measurements))
                        supportFragmentManager.beginTransaction().replace(R.id.frameLayout1, frag).commit()
                    }

                    else -> {
                        val frag = ServicesFragment.newInstance(dateForServicesFrag)
                        supportFragmentManager.beginTransaction().replace(R.id.frameLayout1, frag).commit()
                    }
                }
                drawer_layout.closeDrawers()


                true
            } catch(e: Exception) {
                e.printStackTrace()
                false
            }
        }


        try {
            val frag = ServicesFragment.newInstance(dateForServicesFrag)
            supportFragmentManager.beginTransaction().replace(R.id.frameLayout1, frag).commit()
        } catch(e: Exception) {
            e.printStackTrace()
        }




    }
    fun getServiceDate():String{
        for (service in services){
            return service.getNormalDate()

        }
        return ""
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                try{
                    val extras:Bundle?=data!!.getExtras()
                    val hr = extras!!.getInt("com.addactivity.hr")
                    val min = extras!!.getInt("com.addactivity.min")

                    val unixEpoch = getNextUnixDate(hr,min)

                    val stamp = Timestamp(unixEpoch*1000)
                    val date = Date(stamp.time).toGMTString()

                    alert(" Next measurement will be done at \n $date"){
                        yesButton {  }
                    }.show()
                    sql.insertService(unixEpoch)
                    services =sql.findServices()
                    setSchedule()

                }
                catch (e:NullPointerException){
                    toast("WHOOPS, NullPointerException :( $e")
                }
            }


        }
        else if (requestCode==1){
            if (resultCode==RESULT_OK){
                try{
                    val extras:Bundle?=data!!.getExtras()
                    val hr = extras!!.getInt("com.addactivity.hr")
                    val min = extras!!.getInt("com.addactivity.min")

                    val unixEpoch = this.getNextUnixDate(hr,min)

                    val stamp = Timestamp(unixEpoch*1000)
                    val date = Date(stamp.time).toGMTString()

                    alert(" Next measurement will be done at \n $date"){
                        yesButton {  }
                    }.show()
                    sql.updateService(unixEpoch)
                    services =sql.findServices()
                    setSchedule()


                }
                catch (e:NullPointerException){
                    toast("WHOOPS, NullPointerException :( $e")
                }
            }
        }
    }


    fun getNextUnixDate(hr:Int,min:Int):Long {
        val current = LocalDateTime.now()
        val curHr = current.hour
        println("SASSS"+curHr)
        val curMin = current.minute
        var addHr=23-curHr
        var addMin=60-curMin
        addHr+=hr
        addMin+=min
        var nextLaunch=current.plusHours(addHr.toLong())
        nextLaunch= nextLaunch.plusMinutes(addMin.toLong())
        return nextLaunch.atZone(ZoneOffset.UTC).toEpochSecond()
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onStart() {
        super.onStart()
        services =sql.findServices()
        dateForServicesFrag = getServiceDate()
        val frag = ServicesFragment.newInstance(dateForServicesFrag)
        supportFragmentManager.beginTransaction().replace(R.id.frameLayout1, frag).commit()
        button.setOnClickListener{
            launchMeasurement()
        }
        measurements=sql.findMeasurements()
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onFragmentInteraction(uri: Uri) {

    }

    private fun setSchedule(){
        //schedule the next measurement  and then  update service time in the callback

        val amgr = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        amgr.setExact(AlarmManager.RTC_WAKEUP, services[0].last_date*1000, "measurement", {

           launchMeasurement()
            var normalTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(services[0].last_date*1000),
                ZoneId.of("UTC"))
            normalTime=normalTime.plusDays(1)
            sql.updateService(normalTime.atZone(ZoneOffset.UTC).toEpochSecond()/1000)
            setSchedule()

        },null)
        toast("Scheduler set")
    }


    private fun launchMeasurement(){
        val startIntent = Intent(this, MeasureService::class.java)
        startService(startIntent)
        val h = Handler()
        val r = Runnable {
            stopService(startIntent)
        }
        h.postDelayed(r, 5000)
    }

}
//on start i setSchedule eshe neponjatno, postmotri v nih
