package com.example.temptracker

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper



class MyHelper(ctx: Context) : SQLiteOpenHelper(ctx,"TestDB", null, 1) {


    override fun onCreate(db: SQLiteDatabase) {
        println("ON CREATE SQLITE")
        db.execSQL ("CREATE TABLE IF NOT EXISTS Services (id INTEGER PRIMARY KEY AUTOINCREMENT, period INTEGER, last_date INTEGER)")
        db.execSQL ("CREATE TABLE IF NOT EXISTS Measurements (id INTEGER PRIMARY KEY AUTOINCREMENT, temperature REAL, humidity REAL, date_taken INTEGER)")
    }


    override fun onUpgrade(db: SQLiteDatabase, oldVersion:Int, newVersion:Int) {
        db.execSQL ("DROP TABLE IF EXISTS Services")
        db.execSQL ("DROP TABLE IF EXISTS Measurements")
        onCreate(db)
    }

    fun findServices() : List<Service> {
        val services = mutableListOf<Service>()
        val db = getReadableDatabase()
        val cursor = db.rawQuery ("SELECT * FROM Services",null)
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                val p = Service(cursor.getLong(cursor.getColumnIndex("id")),cursor.getLong(cursor.getColumnIndex("period")),
                    cursor.getLong(cursor.getColumnIndex("last_date")))
                services.add(p)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return services
    }
    fun findMeasurements() : List<Measurement> {
        val measurements = mutableListOf<Measurement>()
        val db = getReadableDatabase()
        val cursor = db.rawQuery ("SELECT * FROM Measurements ORDER BY date_taken", null )
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                val p = Measurement(cursor.getLong(cursor.getColumnIndex("id")),cursor.getFloat(cursor.getColumnIndex("temperature")),cursor.getFloat(cursor.getColumnIndex("humidity")),
                    cursor.getLong(cursor.getColumnIndex("date_taken")))
                measurements.add(p)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return measurements
    }
    fun insertService(last_date:Long) : Long?{

        val db = getWritableDatabase()
        val stmt = db.compileStatement ("INSERT INTO Services (period,last_date) VALUES (1,?)")
        stmt.bindDouble (1, last_date.toDouble())
        val id = stmt.executeInsert()
        return id
    }
    fun insertMeasurement(temperature:Float,humidity:Float, date_taken:Long) : Long{
        val db = getWritableDatabase()
        val stmt = db.compileStatement ("INSERT INTO Measurements (temperature,humidity, date_taken) VALUES (?, ?,?)")
        stmt.bindDouble (1, temperature.toDouble())
        stmt.bindDouble (2, humidity.toDouble())
        stmt.bindLong (3, date_taken)
        val id = stmt.executeInsert()
        return id
    }
    fun updateService(date:Long): Int{
        val db = getWritableDatabase()
        val stmt = db.compileStatement ("UPDATE Services SET period=1,last_date=?")
        stmt.bindLong (1, date)

        val nAffectedRows = stmt.executeUpdateDelete()
        return nAffectedRows
    }


    //todo insert measurement  i vnizpo spisku


   /*
    fun deleteRecord(id:String): Int{
        val db = getWritableDatabase()
        val stmt = db.compileStatement  ("DELETE FROM Songs WHERE Id=?");
        stmt.bindLong (1, id.toLong());
        val nAffectedRows = stmt.executeUpdateDelete()
        return nAffectedRows
    }
    fun searchByArtist(artist:String):List<Song>{
        val songs = mutableListOf<Song>()
        val db = getReadableDatabase()
        val cursor = db.rawQuery ("SELECT * FROM Songs WHERE Artist LIKE ?", arrayOf<String>(artist) )
        if (cursor.moveToFirst()){
            while(!cursor.isAfterLast()){
                val p = Song(cursor.getString(cursor.getColumnIndex("Title")),cursor.getString(cursor.getColumnIndex("Artist")),
                    cursor.getLong(cursor.getColumnIndex("Year")),cursor.getLong(cursor.getColumnIndex("Id")))
                songs.add(p)
                cursor.moveToNext()
            }
        }
        cursor.close()
        return songs
    }

*/


}