package com.example.sensorial

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.example.sensorial.databinding.ActivityMainBinding
import kotlin.math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    private lateinit var binding: ActivityMainBinding

    private var lastThudTime: Long = 0
    private var lastDetectionTime: Long = 0 // 🕒 Tiempo de la última detección
    private var thudCount = 0
    private val THUD_THRESHOLD = 15.0f
    private val DOUBLE_THUD_TIME = 500 // ⏳ Tiempo máximo entre thuds en ms
    private val RESET_TIME = 5000 // ⏳ Espera 5 segundos antes de volver a detectar

    private val handler = Handler(Looper.getMainLooper()) // Handler para cambiar el texto

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) as Sensor
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        binding.xProgressBar.progress = abs(x * 10).toInt()
        binding.yProgressBar.progress = abs(y * 10).toInt()
        binding.zProgressBar.progress = abs(z * 10).toInt()

        detectThud(x, y, z)
    }

    private fun detectThud(x: Float, y: Float, z: Float) {
        val currentTime = System.currentTimeMillis()

        // ⏳ Si han pasado menos de 5 segundos desde el último Double Thud, no detectamos nada
        if (currentTime - lastDetectionTime < RESET_TIME) return

        val acceleration = abs(x) + abs(y) + abs(z)
        if (acceleration > THUD_THRESHOLD) {
            if (currentTime - lastThudTime < DOUBLE_THUD_TIME) {
                thudCount++
                if (thudCount >= 2) {
                    runOnUiThread {
                        binding.thudStatus.text = "DOBLE TOQUE: DETECTADO FELICIDADES"
                    }
                    thudCount = 0
                    lastDetectionTime = currentTime // 🕒 Guardamos el tiempo de detección

                    // ⏳ Restaurar el texto después de 5 segundos
                    handler.postDelayed({
                        runOnUiThread {
                            binding.thudStatus.text = "DOBLE TOQUE: NO DETECTADO"
                        }
                    }, RESET_TIME.toLong())
                }
            } else {
                thudCount = 1
            }
            lastThudTime = currentTime
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
