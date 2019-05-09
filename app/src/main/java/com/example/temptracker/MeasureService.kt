package com.example.temptracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.provider.Settings
import android.support.v4.app.NotificationCompat

class MeasureService:android.app.Service(), SensorEventListener {

    private lateinit var sensorManager : SensorManager
    private var temperature: Sensor?=null
    private var humidity: Sensor?=null
    lateinit var sql:MyHelper


    var tempMes:Float=0.toFloat()
    var humidMes:Float=0.toFloat()
    override fun onCreate() {
        super.onCreate()
        sql= MyHelper(this)
        // a particular sensor.
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        temperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
        humidity=sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)



    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return null
    }
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {

        // Do something with this sensor data.

        if(event.sensor == temperature) {
            tempMes=event.values[0]
        }
        else if (event.sensor == humidity) {
            humidMes=event.values[0]
        }
    }

    override fun onDestroy() {
        //NOTIFICATION ***************!!!!!!!!!!!!!!!

        sql.insertMeasurement(tempMes,humidMes,System.currentTimeMillis())

    // Check that we are running at least Oreo, channels can't be used in older versions
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.O){
                val channelID = "channel0"
                val name = "emails"
                val descriptionText = "Notification channel for email updates"
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(channelID, name, importance)
                val nMgr = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
                nMgr.createNotificationChannel(channel)
                val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply {
                    putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                    putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId())
                }
                val notification= NotificationCompat.Builder(this,"channel0")
                    .setContentTitle("Measuremennts taken")
                    .setContentText("Temperature: $tempMes Humidity: $humidMes ")
                    .setSmallIcon(R.drawable.ic_baseline_list_24px)
                    .build()

                startActivity(intent)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                val launch = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)



                nMgr.notify(0, notification) // id is a unique ID for this notification

            }



    }
}