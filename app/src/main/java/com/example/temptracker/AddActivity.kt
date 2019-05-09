package com.example.temptracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity;
import android.widget.TimePicker

import kotlinx.android.synthetic.main.activity_add.*
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.toast

class AddActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        setSupportActionBar(toolbar)

        val timePicker :TimePicker =  findViewById(R.id.timePicker)

        buttonSubmit.setOnClickListener{
            toast("Submit Pressed")
            val hr:Int=timePicker.currentHour
            val min:Int=timePicker.currentMinute
            val bundle=Bundle()
            bundle.putInt("com.addactivity.hr",hr)
            bundle.putInt("com.addactivity.min",min)

            val intent = Intent()
            intent.putExtras(bundle)

            toast("$hr hours and $min minutes")
            setResult(RESULT_OK,intent)
            finish()
        }




    }

    override fun onBackPressed() {
        toast("Please set time and press submit")
    }


}
