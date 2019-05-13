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
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset


class MeasureService:android.app.Service(), SensorEventListener {
    private lateinit var sensorManager: SensorManager
    lateinit var services:List<Service>
    lateinit var temperature: Sensor
    lateinit var humidity: Sensor
    lateinit var sql: MyHelper
    private var notificationManager: NotificationManager? = null
    var  isHumiditySensorPresent=false
    var  isTemperatureSensorPresent=false
    var response=""
    var noSensorString=""
    var tempMes: Float = 0.toFloat()
    var humidMes: Float = 0.toFloat()
    private fun createNotificationChannel(
        id: String, name: String,
        description: String
    ) {
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(id, name, importance)
        channel.description = description
        channel.enableLights(true)
        channel.lightColor = Color.RED
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
            sensorManager.registerListener(this,humidity,SensorManager.SENSOR_DELAY_NORMAL)
            isHumiditySensorPresent = true
        }
        else {
            noSensorString+=" HumiditySensor "
            isHumiditySensorPresent = false;
        }
        if(sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE) != null) {
            temperature = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)
            sensorManager.registerListener(this,temperature,SensorManager.SENSOR_DELAY_NORMAL)
            isTemperatureSensorPresent = true;

        } else {
            noSensorString+=" TemperatureSensor "
            isTemperatureSensorPresent = false;
        }
    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }
    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor == temperature) {
            tempMes = event.values[0]
        } else if (event.sensor == humidity) {
            humidMes = event.values[0]
        }
    }
    override fun onDestroy() {
        if ((isTemperatureSensorPresent&&isHumiditySensorPresent)){
            response="Temperature: $tempMes Humidity:$humidMes $noSensorString"
        }
        else{
            response="Following sensors not available: $noSensorString"
        }
        services=sql.findServices()
        sql.insertMeasurement(tempMes, humidMes, System.currentTimeMillis())
        var normalTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(services[0].last_date*1000),
            ZoneId.of("UTC"))
        normalTime=normalTime.plusDays(1)
        sql.updateService(normalTime.atZone(ZoneOffset.UTC).toEpochSecond())
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
        if (::temperature.isInitialized){
            sensorManager.unregisterListener(this,temperature)
        }
        if (::humidity.isInitialized){
            sensorManager.unregisterListener(this,humidity)
        }
    }
}