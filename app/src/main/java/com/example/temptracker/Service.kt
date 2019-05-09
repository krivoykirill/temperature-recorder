package com.example.temptracker

import java.time.Instant
import java.time.ZoneOffset
import java.time.Instant.ofEpochMilli
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*


class Service(i: Long,p: Long,ld: Long){
    val id:Long
    val period:Long
    val last_date:Long

    init{
        id=i
        period=p
        last_date=ld

    }
    fun getNormalDate():String{
        println("DATE ====== "+last_date)
        val normalTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(last_date*1000),
            ZoneId.of("UTC"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val formatDateTime = normalTime.format(formatter)

        return formatDateTime
    }
}
