package com.example.temptracker

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class Measurement(i:Long,t:Float,h:Float,dt:Long) {

    val id: Long
    val temperature: Float
    val humidity: Float
    val date_taken:Long

    init{
        id=i
        temperature=t
        humidity=h
        date_taken=dt


    }
    fun getNormalDate():String{
        println("DATE ====== "+date_taken)
        val normalTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(date_taken),
            ZoneId.of("UTC"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val formatDateTime = normalTime.format(formatter)

        return formatDateTime
    }
    fun getLocalDateTime():LocalDateTime {
        val normalTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(date_taken*1000),
            ZoneId.of("UTC")
        )
        return normalTime
    }
}