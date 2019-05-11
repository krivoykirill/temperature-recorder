package com.example.temptracker

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.*
import android.provider.Settings
import android.support.v4.app.NotificationCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import org.jetbrains.anko.toast


class MeasureService:android.app.Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    lateinit var temperature: Sensor
    lateinit var humidity: Sensor
    lateinit var sql: MyHelper
    private var notificationManager: NotificationManager? = null
    var  isHumiditySensorPresent=false
    var  isTemperatureSensorPresent=false
    var response=""

    var tempMes: Float = 0.toFloat()
    var humidMes: Float = 0.toFloat()

    private fun createNotificationChannel(
        id: String, name: String,
        description: String
    ) {

        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(id, name, importance)


        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.enableVibration(true)
        channel.vibrationPattern =
            longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        notificationManager?.createNotificationChannel(channel)
    }

    override fun onCreate() {
        super.onCreate()
        sql = MyHelper(this)
        // a particular sensor.
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        toast("service started")

        notificationManager =
            getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

        createNotificationChannel(
            "com.example.temptracker.service",
            "Temperature Measurement Service",
            "Temp tracker measurement service"
        )

        if(sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY) != null) {
            humidity = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)
            isHumiditySensorPresent = true

        }
        else {
            isHumiditySensorPresent = false;
        }

        if(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            temperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
            isTemperatureSensorPresent = true;
        } else {

            isTemperatureSensorPresent = false;
        }


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

        if (event.sensor == temperature) {
            tempMes = event.values[0]
        } else if (event.sensor == humidity) {
            humidMes = event.values[0]
        }
    }




    override fun onDestroy() {
        if ((isTemperatureSensorPresent)){
            response="Temperature: $tempMes Humidity:$humidMes"
        }
        else{
            response="Your device has no sensors needed"
        }


        sql.insertMeasurement(tempMes, humidMes, System.currentTimeMillis())
        val notificationID = 101
        val channelID = "com.example.temptracker.service"

        val notification = Notification.Builder(
            this@MeasureService,
            channelID
        )
            .setContentTitle("Measurements taken")
            .setContentText(response)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setChannelId(channelID)
            .build()
        notificationManager?.notify(notificationID, notification)



        //todo something with rescheduling

    }
}