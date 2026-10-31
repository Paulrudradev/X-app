package com.example.model

enum class WristbandTab(val index: Int, val code: String, val label: String) {
    HUB(0, "01", "Wristband Hub"),
    WORKOUT(1, "02", "Remote Workouts"),
    VITALS(2, "03", "Health & Vitals"),
    ALARMS(3, "04", "Alarms & Alerts"),
    CONSOLE(4, "05", "Blind Touch & BLE")
}

enum class ScanSensorType(
    val id: String,
    val title: String,
    val unit: String,
    val durationSec: Int,
    val scanningHeadline: String,
    val cmdByte: String
) {
    HR(
        id = "hr",
        title = "Heart Rate",
        unit = "BPM",
        durationSec = 15,
        scanningHeadline = "Optical Green LED PPG: Measuring Heart Rate...",
        cmdByte = "01"
    ),
    SPO2(
        id = "spo2",
        title = "Blood Oxygen",
        unit = "% SpO₂",
        durationSec = 20,
        scanningHeadline = "Red + IR Optical LED: Measuring Blood Oxygen (SpO₂)...",
        cmdByte = "02"
    ),
    STRESS(
        id = "stress",
        title = "HRV Stress",
        unit = "/ 100",
        durationSec = 15,
        scanningHeadline = "HRV R-R Interval Analysis: Measuring Stress Index...",
        cmdByte = "03"
    )
}

data class HardwareSettings(
    val dnd: Boolean = false,
    val liftWake: Boolean = false,
    val btCalling: Boolean = true,
    val autoHr: Boolean = true,
    val autoSpo2: Boolean = true,
    val vibration: String = "STRONG"
)

data class SportMode(
    val name: String,
    val emoji: String,
    val subtitle: String,
    val hasGps: Boolean,
    val met: Float
)

data class WorkoutSession(
    val id: Long,
    val sport: String,
    val date: String,
    val duration: String,
    val distance: String,
    val avgHr: String,
    val calories: String
)

data class WristAlarm(
    val id: Long,
    val time: String,
    val days: String,
    val enabled: Boolean
)

data class BlePacketLog(
    val id: Long,
    val timestamp: String,
    val direction: String, // "TX" or "RX"
    val hex: String,
    val description: String
)

data class DiscoveredBleDevice(
    val name: String,
    val address: String,
    val rssi: Int,
    val isCmfCandidate: Boolean
)

data class BlindQuadrant(
    val id: String,
    val emoji: String,
    val positionLabel: String,
    val actionTitle: String,
    val fullDescription: String,
    val txFrame: String
)

data class ToastMessage(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val message: String
)
