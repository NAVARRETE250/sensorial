package com.example.sensorial

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getSystemService
import kotlin.math.abs
import androidx.compose.ui.semantics.setProgress
import androidx.core.view.WindowCompat

import com.example.sensorial.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var binding: ActivityMainBinding

    // Constants for better readability
    companion object {
        private const val PROGRESS_BAR_MAX = 100
        private const val SENSITIVITY_MULTIPLIER = 7.0 // Adjust this value for desired sensitivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Use view binding to access UI elements
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Listen for sensor changes
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor
    }

    override fun onResume() {
        super.onResume()
        // Register the listener in onResume
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        // Unregister the listener in onPause
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        // Use sensor data
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        // 1g = 9.8 m/s², which is a fairly high value.
        // Multiplying by 10 gets us closer to 100, which is the default maximum value for the ProgressBar
        // Using the SENSITIVITY_MULTIPLIER constant

        // Using coerceIn to limit the range and setProgress to update the progress bar
        binding.xProgressBar.setProgress(abs(x * SENSITIVITY_MULTIPLIER).toInt().coerceIn(0, PROGRESS_BAR_MAX), true)
        binding.yProgressBar.setProgress(abs(y * SENSITIVITY_MULTIPLIER).toInt().coerceIn(0, PROGRESS_BAR_MAX), true)
        binding.zProgressBar.setProgress(abs(z * SENSITIVITY_MULTIPLIER).toInt().coerceIn(0, PROGRESS_BAR_MAX), true)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // You can handle changes in sensor accuracy here
        // For example, display a message to the user if the accuracy is low
    }
}

