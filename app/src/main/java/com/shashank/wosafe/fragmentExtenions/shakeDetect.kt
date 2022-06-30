package com.shashank.wosafe.fragmentExtenions

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.shashank.wosafe.R
import java.util.*

var acceleration = 0f
var currentAcceleration = 0f
var lastAcceleration = 0f

fun shakeDetect (context: Context):Boolean{


    val sensorListener: SensorEventListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent) {


            val x = event.values[0]
//            val y = event.values[1]
//            val z = event.values[2]
            lastAcceleration = currentAcceleration


            currentAcceleration = x
            val delta: Float = currentAcceleration - lastAcceleration
            acceleration = acceleration * 0.9f + delta


            if (acceleration > 10) {
                Toast.makeText(context, "Shake event detected", Toast.LENGTH_SHORT).show()

            }
        }
        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}
    }


 var sensorManager: SensorManager? = null

    sensorManager = context!!.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    Objects.requireNonNull(sensorManager)!!
        .registerListener(sensorListener, sensorManager!!
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL)

    acceleration = 10f
    currentAcceleration = SensorManager.GRAVITY_EARTH
    lastAcceleration = SensorManager.GRAVITY_EARTH


    return true

}