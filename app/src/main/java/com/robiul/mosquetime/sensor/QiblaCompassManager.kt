package com.robiul.mosquetime.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.robiul.mosquetime.util.HapticUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.*

data class QiblaState(
    val currentAzimuth: Float = 0f,
    val qiblaBearing: Float = 277.5f, // Default Bangladesh to Mecca bearing (~277.5°)
    val relativeAngle: Float = 0f,
    val isAligned: Boolean = false,
    val accuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_HIGH,
    val distanceKm: Double = 5150.0,
    val userLatitude: Double = 23.8103, // Default Dhaka
    val userLongitude: Double = 90.4125,
    val isSensorAvailable: Boolean = true
)

class QiblaCompassManager(private val context: Context) : SensorEventListener {

    companion object {
        // Kaaba Exact Coordinates
        const val KAABA_LATITUDE = 21.422487
        const val KAABA_LONGITUDE = 39.826206
    }

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _qiblaState = MutableStateFlow(QiblaState())
    val qiblaState: StateFlow<QiblaState> = _qiblaState.asStateFlow()

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private val rMatrix = FloatArray(9)
    private val iMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    private var hasGravity = false
    private var hasGeomagnetic = false
    private var lastHapticTriggerTime = 0L

    private var smoothedAzimuth = 0f

    init {
        updateUserLocation(23.8103, 90.4125)
    }

    fun startListening() {
        val hasSensors = accelerometer != null && magnetometer != null
        if (!hasSensors) {
            _qiblaState.value = _qiblaState.value.copy(isSensorAvailable = false)
            return
        }

        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        magnetometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    fun updateUserLocation(lat: Double, lng: Double) {
        val bearing = calculateQiblaBearing(lat, lng).toFloat()
        val distance = calculateDistanceToKaaba(lat, lng)
        _qiblaState.value = _qiblaState.value.copy(
            userLatitude = lat,
            userLongitude = lng,
            qiblaBearing = bearing,
            distanceKm = distance
        )
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val alpha = 0.18f // Low pass filter factor for smooth movement

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                gravity[0] = alpha * event.values[0] + (1 - alpha) * gravity[0]
                gravity[1] = alpha * event.values[1] + (1 - alpha) * gravity[1]
                gravity[2] = alpha * event.values[2] + (1 - alpha) * gravity[2]
                hasGravity = true
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                geomagnetic[0] = alpha * event.values[0] + (1 - alpha) * geomagnetic[0]
                geomagnetic[1] = alpha * event.values[1] + (1 - alpha) * geomagnetic[1]
                geomagnetic[2] = alpha * event.values[2] + (1 - alpha) * geomagnetic[2]
                hasGeomagnetic = true
            }
        }

        if (hasGravity && hasGeomagnetic) {
            val success = SensorManager.getRotationMatrix(rMatrix, iMatrix, gravity, geomagnetic)
            if (success) {
                SensorManager.getOrientation(rMatrix, orientation)
                // Orientation[0] is azimuth in radians [-pi, pi]
                val rawAzimuthDeg = (Math.toDegrees(orientation[0].toDouble()) + 360.0) % 360.0

                // Circular smoothing for azimuth to prevent 359 -> 0 flip jump
                val diff = (rawAzimuthDeg - smoothedAzimuth + 540) % 360 - 180
                smoothedAzimuth = (((smoothedAzimuth + diff * 0.2f) + 360) % 360).toFloat()

                val bearing = _qiblaState.value.qiblaBearing
                // Angle between device orientation and Qibla direction
                val relative = ((bearing - smoothedAzimuth) + 360.0) % 360.0

                // Aligned if within +/- 3.5 degrees of true Qibla
                val deviation = abs((relative + 180) % 360 - 180)
                val isAligned = deviation <= 3.5

                if (isAligned) {
                    val now = System.currentTimeMillis()
                    if (now - lastHapticTriggerTime > 1200) {
                        lastHapticTriggerTime = now
                        HapticUtils.performQiblaLockPulse(context)
                    }
                }

                _qiblaState.value = _qiblaState.value.copy(
                    currentAzimuth = smoothedAzimuth.toFloat(),
                    relativeAngle = relative.toFloat(),
                    isAligned = isAligned,
                    isSensorAvailable = true
                )
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        _qiblaState.value = _qiblaState.value.copy(accuracy = accuracy)
    }

    /**
     * Calculates the Great Circle forward azimuth from user coordinates to the Holy Kaaba.
     */
    private fun calculateQiblaBearing(lat: Double, lon: Double): Double {
        val lat1 = Math.toRadians(lat)
        val lon1 = Math.toRadians(lon)
        val lat2 = Math.toRadians(KAABA_LATITUDE)
        val lon2 = Math.toRadians(KAABA_LONGITUDE)

        val dLon = lon2 - lon1
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)

        val initialBearing = atan2(y, x)
        return (Math.toDegrees(initialBearing) + 360.0) % 360.0
    }

    /**
     * Calculates geodesic distance to the Holy Kaaba in kilometers using the Haversine formula.
     */
    private fun calculateDistanceToKaaba(lat: Double, lon: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(KAABA_LATITUDE - lat)
        val dLon = Math.toRadians(KAABA_LONGITUDE - lon)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat)) * cos(Math.toRadians(KAABA_LATITUDE)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return r * c
    }
}
