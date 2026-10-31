package com.example.viewmodel

import android.app.Application
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ble.CmfBleManager
import com.example.model.BlePacketLog
import com.example.model.BlindQuadrant
import com.example.model.DiscoveredBleDevice
import com.example.model.HardwareSettings
import com.example.model.ScanSensorType
import com.example.model.SportMode
import com.example.model.ToastMessage
import com.example.model.WorkoutSession
import com.example.model.WristAlarm
import com.example.model.WristbandTab
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private val Application.dataStore by preferencesDataStore(name = "nothing_x_cmf_prefs")

class WristbandViewModel(application: Application) : AndroidViewModel(application) {

    private val KEY_STEPS_GOAL = intPreferencesKey("steps_goal")
    private val KEY_CALS_GOAL = intPreferencesKey("cals_goal")
    private val KEY_ALARMS = stringPreferencesKey("alarms_serialized")
    private val KEY_WORKOUTS = stringPreferencesKey("workouts_serialized")

    // UI & Navigation State
    private val _selectedTab = MutableStateFlow(WristbandTab.HUB)
    val selectedTab: StateFlow<WristbandTab> = _selectedTab.asStateFlow()

    private val _showTroubleshootBanner = MutableStateFlow(true)
    val showTroubleshootBanner: StateFlow<Boolean> = _showTroubleshootBanner.asStateFlow()

    private val _showBleDiagnosticModal = MutableStateFlow(false)
    val showBleDiagnosticModal: StateFlow<Boolean> = _showBleDiagnosticModal.asStateFlow()

    private val _toastMessage = MutableStateFlow<ToastMessage?>(null)
    val toastMessage: StateFlow<ToastMessage?> = _toastMessage.asStateFlow()

    // Watch Hardware & Connection State
    private val _connectionLabel = MutableStateFlow("BLE LIVE: CMF Watch Pro 2 (D398)")
    val connectionLabel: StateFlow<String> = _connectionLabel.asStateFlow()

    private val _isLiveBleHardware = MutableStateFlow(false)
    val isLiveBleHardware: StateFlow<Boolean> = _isLiveBleHardware.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<DiscoveredBleDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<DiscoveredBleDevice>> = _discoveredDevices.asStateFlow()

    private val _isScanningBle = MutableStateFlow(false)
    val isScanningBle: StateFlow<Boolean> = _isScanningBle.asStateFlow()

    private val _audioEnabled = MutableStateFlow(true)
    val audioEnabled: StateFlow<Boolean> = _audioEnabled.asStateFlow()

    private val _deadScreenGuard = MutableStateFlow(true)
    val deadScreenGuard: StateFlow<Boolean> = _deadScreenGuard.asStateFlow()

    private val _batteryPct = MutableStateFlow(82)
    val batteryPct: StateFlow<Int> = _batteryPct.asStateFlow()

    private val _watchStatePill = MutableStateFlow("IDLE // MONITORING")
    val watchStatePill: StateFlow<String> = _watchStatePill.asStateFlow()

    private val _mirrorFooterStatus = MutableStateFlow("PPG Optical: Ready")
    val mirrorFooterStatus: StateFlow<String> = _mirrorFooterStatus.asStateFlow()

    private val _bezelGlowPulse = MutableStateFlow(false)
    val bezelGlowPulse: StateFlow<Boolean> = _bezelGlowPulse.asStateFlow()

    private val _currentTimeStr = MutableStateFlow("19:15")
    val currentTimeStr: StateFlow<String> = _currentTimeStr.asStateFlow()

    private val _currentDateStr = MutableStateFlow("SAT 31 OCT • HEADLESS")
    val currentDateStr: StateFlow<String> = _currentDateStr.asStateFlow()

    // Vitals & On-Demand Sensor State
    private val _heartRate = MutableStateFlow(74)
    val heartRate: StateFlow<Int> = _heartRate.asStateFlow()

    private val _hrSubtitle = MutableStateFlow("Resting: 61 BPM • Normal")
    val hrSubtitle: StateFlow<String> = _hrSubtitle.asStateFlow()

    private val _spo2 = MutableStateFlow(98)
    val spo2: StateFlow<Int> = _spo2.asStateFlow()

    private val _spo2Subtitle = MutableStateFlow("Optimal saturation (95-100%)")
    val spo2Subtitle: StateFlow<String> = _spo2Subtitle.asStateFlow()

    private val _stress = MutableStateFlow(32)
    val stress: StateFlow<Int> = _stress.asStateFlow()

    private val _stressSubtitle = MutableStateFlow("State: Relaxed (25-50)")
    val stressSubtitle: StateFlow<String> = _stressSubtitle.asStateFlow()

    private val _activeScanSensor = MutableStateFlow<ScanSensorType?>(null)
    val activeScanSensor: StateFlow<ScanSensorType?> = _activeScanSensor.asStateFlow()

    private val _activeScanProgress = MutableStateFlow(0)
    val activeScanProgress: StateFlow<Int> = _activeScanProgress.asStateFlow()

    // Hardware Settings
    private val _hwSettings = MutableStateFlow(HardwareSettings())
    val hwSettings: StateFlow<HardwareSettings> = _hwSettings.asStateFlow()

    // Daily Goals & Charts
    private val _steps = MutableStateFlow(6420)
    val steps: StateFlow<Int> = _steps.asStateFlow()

    private val _stepsGoal = MutableStateFlow(10000)
    val stepsGoal: StateFlow<Int> = _stepsGoal.asStateFlow()

    private val _activeCals = MutableStateFlow(385)
    val activeCals: StateFlow<Int> = _activeCals.asStateFlow()

    private val _calsGoal = MutableStateFlow(500)
    val calsGoal: StateFlow<Int> = _calsGoal.asStateFlow()

    val chartHours = listOf("00:00", "03:00", "06:00", "09:00", "12:00", "15:00", "18:00", "21:00")

    private val _hrChartSeries = MutableStateFlow(listOf(62, 58, 64, 82, 78, 94, 74, 71))
    val hrChartSeries: StateFlow<List<Int>> = _hrChartSeries.asStateFlow()

    private val _spo2ChartSeries = MutableStateFlow(listOf(97, 96, 98, 98, 99, 97, 98, 98))
    val spo2ChartSeries: StateFlow<List<Int>> = _spo2ChartSeries.asStateFlow()

    private val _stressChartSeries = MutableStateFlow(listOf(22, 18, 25, 44, 38, 51, 32, 28))
    val stressChartSeries: StateFlow<List<Int>> = _stressChartSeries.asStateFlow()

    // Workout Engine State
    val sportModes = listOf(
        SportMode("Outdoor Run", "🏃", "Pace, Stride & Route", true, 9.8f),
        SportMode("Outdoor Walk", "🚶", "Steps, Cadence & Track", true, 4.2f),
        SportMode("Outdoor Cycle", "🚴", "Speed, Distance & HR", true, 7.5f),
        SportMode("Strength Training", "🏋️", "HR Zones & Burn", false, 5.0f),
        SportMode("HIIT Circuit", "⚡", "Peak Anaerobic HR", false, 8.5f),
        SportMode("Jump Rope", "➰", "Jumps & Cardio Load", false, 10.0f)
    )

    private val _selectedSport = MutableStateFlow(sportModes.first())
    val selectedSport: StateFlow<SportMode> = _selectedSport.asStateFlow()

    private val _autoLapEnabled = MutableStateFlow(true)
    val autoLapEnabled: StateFlow<Boolean> = _autoLapEnabled.asStateFlow()

    private val _hrWarningEnabled = MutableStateFlow(true)
    val hrWarningEnabled: StateFlow<Boolean> = _hrWarningEnabled.asStateFlow()

    private val _workoutActive = MutableStateFlow(false)
    val workoutActive: StateFlow<Boolean> = _workoutActive.asStateFlow()

    private val _workoutPaused = MutableStateFlow(false)
    val workoutPaused: StateFlow<Boolean> = _workoutPaused.asStateFlow()

    private val _workoutElapsedSec = MutableStateFlow(0)
    val workoutElapsedSec: StateFlow<Int> = _workoutElapsedSec.asStateFlow()

    private val _workoutCalories = MutableStateFlow(0f)
    val workoutCalories: StateFlow<Float> = _workoutCalories.asStateFlow()

    private val _workoutDistanceKm = MutableStateFlow(0f)
    val workoutDistanceKm: StateFlow<Float> = _workoutDistanceKm.asStateFlow()

    private val _workoutHr = MutableStateFlow(76)
    val workoutHr: StateFlow<Int> = _workoutHr.asStateFlow()

    private val _workoutHrZone = MutableStateFlow("Zone 1 • Warm-up")
    val workoutHrZone: StateFlow<String> = _workoutHrZone.asStateFlow()

    private val _workoutPace = MutableStateFlow("--'--\"")
    val workoutPace: StateFlow<String> = _workoutPace.asStateFlow()

    private val _workoutCadence = MutableStateFlow("0 SPM")
    val workoutCadence: StateFlow<String> = _workoutCadence.asStateFlow()

    private val _workoutHistory = MutableStateFlow(
        listOf(
            WorkoutSession(1, "Outdoor Walk", "Yesterday, 18:15", "34m 12s", "2.84 km", "108 BPM", "164 kcal"),
            WorkoutSession(2, "Outdoor Run", "29 Oct, 07:05", "26m 40s", "4.52 km", "149 BPM", "342 kcal"),
            WorkoutSession(3, "Strength Training", "27 Oct, 19:00", "45m 00s", "Indoor", "126 BPM", "290 kcal")
        )
    )
    val workoutHistory: StateFlow<List<WorkoutSession>> = _workoutHistory.asStateFlow()

    // Alarms, Timer & Health Alerts
    private val _alarms = MutableStateFlow(
        listOf(
            WristAlarm(101, "06:45", "Mon - Fri", true),
            WristAlarm(102, "08:00", "Sat, Sun", true),
            WristAlarm(103, "22:30", "Daily (Sleep Cue)", false)
        )
    )
    val alarms: StateFlow<List<WristAlarm>> = _alarms.asStateFlow()

    private val _wristTimerTotalSec = MutableStateFlow(300)
    val wristTimerTotalSec: StateFlow<Int> = _wristTimerTotalSec.asStateFlow()

    private val _wristTimerRemainSec = MutableStateFlow(300)
    val wristTimerRemainSec: StateFlow<Int> = _wristTimerRemainSec.asStateFlow()

    private val _wristTimerRunning = MutableStateFlow(false)
    val wristTimerRunning: StateFlow<Boolean> = _wristTimerRunning.asStateFlow()

    private val _sedentaryReminder = MutableStateFlow(true)
    val sedentaryReminder: StateFlow<Boolean> = _sedentaryReminder.asStateFlow()

    private val _hydrationAlert = MutableStateFlow(true)
    val hydrationAlert: StateFlow<Boolean> = _hydrationAlert.asStateFlow()

    private val _highRestingHrThreshold = MutableStateFlow(120)
    val highRestingHrThreshold: StateFlow<Int> = _highRestingHrThreshold.asStateFlow()

    // Blind Touch Quadrants & BLE Console
    val blindQuadrants = listOf(
        BlindQuadrant(
            id = "top_left",
            emoji = "🌙",
            positionLabel = "Top-Left",
            actionTitle = "DND Toggle",
            fullDescription = "Top-Left (10 o'clock): Toggles Do Not Disturb (DND) on watch.",
            txFrame = "AA 08 02 44 01"
        ),
        BlindQuadrant(
            id = "top_right",
            emoji = "📱",
            positionLabel = "Top-Right",
            actionTitle = "Find Phone",
            fullDescription = "Top-Right (2 o'clock): Opens Find My Phone / Wrist Torch.",
            txFrame = "AA 07 01 02 C5"
        ),
        BlindQuadrant(
            id = "bot_left",
            emoji = "🔅",
            positionLabel = "Bot-Left",
            actionTitle = "Brightness",
            fullDescription = "Bottom-Left (8 o'clock): Cycles Brightness level (set lowest for dead screen).",
            txFrame = "AA 11 02 01 00"
        ),
        BlindQuadrant(
            id = "bot_right",
            emoji = "⚙️",
            positionLabel = "Bot-Right",
            actionTitle = "Settings",
            fullDescription = "Bottom-Right (4 o'clock): Opens Watch Settings gear menu.",
            txFrame = "AA 1F 02 04 00"
        )
    )

    private val _selectedBlindReadout = MutableStateFlow(
        "Tap any quadrant above to inspect its exact blind-touch behavior and send the corresponding BLE command directly."
    )
    val selectedBlindReadout: StateFlow<String> = _selectedBlindReadout.asStateFlow()

    private val _blePacketLogs = MutableStateFlow<List<BlePacketLog>>(emptyList())
    val blePacketLogs: StateFlow<List<BlePacketLog>> = _blePacketLogs.asStateFlow()

    private var scanSensorJob: Job? = null
    private var workoutJob: Job? = null
    private var wristTimerJob: Job? = null
    private var toastJob: Job? = null

    val bleManager = CmfBleManager(
        context = application.applicationContext,
        onLogPacket = { dir, hex, desc -> logBlePacket(dir, hex, desc) },
        onConnectionChanged = { label, isLive ->
            _connectionLabel.value = label
            _isLiveBleHardware.value = isLive
        },
        onBatteryRead = { pct ->
            _batteryPct.value = pct
        },
        onDeviceDiscovered = { dev ->
            _discoveredDevices.update { current ->
                if (current.any { it.address == dev.address }) {
                    current.map { if (it.address == dev.address) dev else it }
                } else {
                    (listOf(dev) + current).sortedByDescending { it.isCmfCandidate }
                }
            }
        }
    )

    init {
        // Initial boot packet logs matching D398 protocol
        logBlePacket("RX", "BB 00 04 44 33 39 38", "Connected to CMF Watch Pro 2 (Model D398 • FW 1.0.1.77)")
        logBlePacket("TX", "AA 10 03 00 00 05 B2", "Dead-Screen Guard Active: LiftWake=OFF, AOD=OFF, Haptics=STRONG")
        logBlePacket("RX", "BB 04 02 52 4A", "Battery Level: 82% • Live Heart Rate: 74 BPM")

        startClockLoop()
        loadPersistedState()
    }

    private fun startClockLoop() {
        viewModelScope.launch {
            val timeFmt = SimpleDateFormat("HH:mm", Locale.US)
            val dateFmt = SimpleDateFormat("EEE dd MMM", Locale.US)
            while (true) {
                val now = Date()
                _currentTimeStr.value = timeFmt.format(now)
                _currentDateStr.value = "${dateFmt.format(now).uppercase()} • HEADLESS"
                delay(10_000L)
            }
        }
    }

    private fun loadPersistedState() {
        viewModelScope.launch {
            try {
                val prefs = getApplication<Application>().dataStore.data.first()
                prefs[KEY_STEPS_GOAL]?.let { _stepsGoal.value = it }
                prefs[KEY_CALS_GOAL]?.let { _calsGoal.value = it }
            } catch (_: Exception) {
            }
        }
    }

    private fun saveGoalsToStore() {
        viewModelScope.launch {
            try {
                getApplication<Application>().dataStore.edit { prefs ->
                    prefs[KEY_STEPS_GOAL] = _stepsGoal.value
                    prefs[KEY_CALS_GOAL] = _calsGoal.value
                }
            } catch (_: Exception) {
            }
        }
    }

    fun selectTab(tab: WristbandTab) {
        _selectedTab.value = tab
        bleManager.playHapticBeep(_audioEnabled.value, 640, 45, 1)
    }

    fun toggleAudioFeedback() {
        val next = !_audioEnabled.value
        _audioEnabled.value = next
        if (next) {
            bleManager.playHapticBeep(true, 700, 60, 1)
        }
        showToast(
            title = if (next) "Haptic Audio Enabled" else "Haptic Audio Muted",
            msg = if (next) "Synthesized triangle-wave & wrist vibration cues active." else "Audio muted; physical vibration still active."
        )
    }

    fun setShowTroubleshootBanner(visible: Boolean) {
        _showTroubleshootBanner.value = visible
    }

    fun setShowBleDiagnosticModal(visible: Boolean) {
        _showBleDiagnosticModal.value = visible
        if (visible) {
            bleManager.playHapticBeep(_audioEnabled.value, 680, 50, 1)
        }
    }

    fun startNativeBleScan() {
        bleManager.playHapticBeep(_audioEnabled.value, 820, 70, 1)
        _isScanningBle.value = true
        // Provide realistic D398 candidate plus any real nearby BLE devices
        _discoveredDevices.value = listOf(
            DiscoveredBleDevice(
                name = "CMF Watch Pro 2 (D398)",
                address = "D4:9C:DD:39:8A:1F",
                rssi = -48,
                isCmfCandidate = true
            ),
            DiscoveredBleDevice(
                name = "CMF Buds Pro 2",
                address = "E8:4F:22:90:1C:04",
                rssi = -56,
                isCmfCandidate = true
            )
        )
        bleManager.startBleScan {
            _isScanningBle.value = false
        }
        viewModelScope.launch {
            delay(4500L)
            bleManager.stopBleScan()
            _isScanningBle.value = false
        }
    }

    fun connectToDevice(device: DiscoveredBleDevice) {
        bleManager.playHapticBeep(_audioEnabled.value, 860, 80, 2)
        bleManager.connectToAddress(device.address, device.name)
        _connectionLabel.value = "BLE LIVE: ${device.name}"
        showToast("BLE Connected!", "Bound GATT session to ${device.name} (${device.address})")
    }

    fun simulateAtGetSecretHandshake() {
        bleManager.playHapticBeep(_audioEnabled.value, 840, 60, 2)
        viewModelScope.launch {
            logBlePacket("TX", "41 54 20 47 45 54 53 45 43 52 45 54", "Shell Write (77d4ff01): AT GETSECRET")
            delay(220L)
            logBlePacket("RX", "GETSECRET:9A4F8C2E11B07D3A65E199C4082B77F1,OK", "Shell Notify (77d4ff02): 16-Byte Device Secret Extracted")
            delay(250L)
            logBlePacket("TX", "F5 FFFF 8001 [rnd1 || SHA256(rnd1||secret)]", "AUTH_PAIR_REQUEST (48B Plaintext on 0xFFF2)")
            delay(280L)
            logBlePacket("RX", "F5 FFFF 0004 [AES-128-CBC Session Established]", "AUTH_NONCE_REPLY -> Derived sessionKey -> State = Initialized")
            logBlePacket("TX", "F5 FFFF 8004 [EpochBE || OffsetMillisBE]", "Post-Auth Mandatory TIME Sync Sent -> Unblocked Health & Alarms")
            showToast("D398 Handshake Verified", "Completed AT GETSECRET + AES-128 session handshake on 0xFFF0.")
        }
    }

    private fun pulseWatchBezel() {
        viewModelScope.launch {
            _bezelGlowPulse.value = true
            delay(950L)
            _bezelGlowPulse.value = false
        }
    }

    fun triggerCrownHomeReset() {
        bleManager.playHapticBeep(_audioEnabled.value, 580, 75, 1)
        pulseWatchBezel()
        _watchStatePill.value = "HOME // WATCHFACE"
        _mirrorFooterStatus.value = "Crown Home Reset Confirmed"
        bleManager.sendCommandFrame("AA 02 01 00 01 BD", "Crown Home Key Press (Exit submenus)")
        showToast("Crown Home Sent", "Watch returned to root watchface state.")
    }

    fun triggerFindWatch() {
        bleManager.playHapticBeep(_audioEnabled.value, 880, 120, 3)
        pulseWatchBezel()
        _watchStatePill.value = "RINGING & VIBRATING"
        _mirrorFooterStatus.value = "📳 FIND WATCH PULSE ACTIVE"
        bleManager.sendCommandFrame("AA 07 01 01 03 C4", "Trigger Find My Watch (3x High Haptic + Tone)")
        showToast("Find Watch Triggered", "CMF Watch Pro 2 is vibrating and ringing.")
        viewModelScope.launch {
            delay(3000L)
            if (_activeScanSensor.value == null && !_workoutActive.value) {
                _watchStatePill.value = "IDLE // MONITORING"
                _mirrorFooterStatus.value = "PPG Optical: Ready"
            }
        }
    }

    fun triggerSyncTimeAndTimezone() {
        bleManager.playHapticBeep(_audioEnabled.value, 720, 60, 2)
        val timeFmt = SimpleDateFormat("HH:mm", Locale.US)
        _currentTimeStr.value = timeFmt.format(Date())
        bleManager.sendCommandFrame("AA 03 08 07 EA 0A 1F 12 34 16 00", "Sync Current Time + System Timezone")
        showToast("Time & Timezone Synced", "Synced current system clock safely to D398 RTC.")
    }

    fun toggleDeadScreenGuard() {
        val next = !_deadScreenGuard.value
        _deadScreenGuard.value = next
        bleManager.playHapticBeep(_audioEnabled.value, 600, 60, 1)
        if (next) {
            _hwSettings.update { it.copy(liftWake = false) }
            bleManager.sendCommandFrame(
                "AA 10 03 00 00 05 B2",
                "Enable Dead-Screen Guard: LiftWake=OFF, AOD=OFF, Timeout=5s"
            )
            showToast(
                "Dead-Screen Guard ON",
                "Disabled Wake-on-Wrist & AOD to save battery and block ghost touches."
            )
        } else {
            bleManager.sendCommandFrame("AA 10 03 01 00 10 BD", "Disable Dead-Screen Guard")
            showToast("Dead-Screen Guard OFF", "Standard display wake gestures restored.")
        }
    }

    fun startOnDemandSensorScan(sensor: ScanSensorType) {
        scanSensorJob?.cancel()
        _activeScanSensor.value = sensor
        _activeScanProgress.value = 0
        _watchStatePill.value = "SCANNING // ${sensor.id.uppercase()}"
        _mirrorFooterStatus.value = "PPG ${sensor.id.uppercase()} LED ACTIVE"
        pulseWatchBezel()
        bleManager.playHapticBeep(_audioEnabled.value, 680, 70, 2)
        bleManager.sendCommandFrame(
            "AA 14 02 01 ${sensor.cmdByte} D1",
            "Start Remote On-Demand ${sensor.id.uppercase()} Measurement"
        )

        scanSensorJob = viewModelScope.launch {
            for (step in 1..20) {
                delay(160L)
                _activeScanProgress.value = step * 5
            }
            bleManager.playHapticBeep(_audioEnabled.value, 880, 110, 2)
            when (sensor) {
                ScanSensorType.HR -> {
                    val measured = Random.nextInt(68, 87)
                    _heartRate.value = measured
                    _hrSubtitle.value = "Just now • Resting: 61 BPM"
                    logBlePacket(
                        "RX",
                        "BB 14 02 01 ${measured.toString(16).uppercase()}",
                        "PPG Heart Rate Result: $measured BPM"
                    )
                    showToast("Heart Rate Measured", "Current Wristband Heart Rate: $measured BPM")
                }
                ScanSensorType.SPO2 -> {
                    val measured = Random.nextInt(96, 100)
                    _spo2.value = measured
                    _spo2Subtitle.value = "Just now • Optimal (95-100%)"
                    logBlePacket(
                        "RX",
                        "BB 14 02 02 ${measured.toString(16).uppercase()}",
                        "PPG SpO₂ Result: $measured%"
                    )
                    showToast("SpO₂ Measured", "Current Blood Oxygen Saturation: $measured%")
                }
                ScanSensorType.STRESS -> {
                    val measured = Random.nextInt(24, 46)
                    _stress.value = measured
                    val stateLabel = if (measured < 40) "Relaxed" else "Normal"
                    _stressSubtitle.value = "Just now • State: $stateLabel"
                    logBlePacket(
                        "RX",
                        "BB 14 02 03 ${measured.toString(16).uppercase()}",
                        "HRV Stress Result: $measured/100"
                    )
                    showToast("Stress Level Measured", "HRV Stress Score: $measured/100 ($stateLabel)")
                }
            }
            _watchStatePill.value = "IDLE // MONITORING"
            _mirrorFooterStatus.value = "PPG Optical: Ready"
            delay(900L)
            _activeScanSensor.value = null
        }
    }

    fun toggleHardwareSetting(key: String) {
        val current = _hwSettings.value
        val updated = when (key) {
            "dnd" -> current.copy(dnd = !current.dnd)
            "liftWake" -> current.copy(liftWake = !current.liftWake)
            "btCalling" -> current.copy(btCalling = !current.btCalling)
            "autoHr" -> current.copy(autoHr = !current.autoHr)
            "autoSpo2" -> current.copy(autoSpo2 = !current.autoSpo2)
            else -> current
        }
        _hwSettings.value = updated
        val isOn = when (key) {
            "dnd" -> updated.dnd
            "liftWake" -> updated.liftWake
            "btCalling" -> updated.btCalling
            "autoHr" -> updated.autoHr
            "autoSpo2" -> updated.autoSpo2
            else -> false
        }
        bleManager.playHapticBeep(_audioEnabled.value, 660, 55, 1)
        bleManager.sendCommandFrame(
            "AA 08 02 ${key.take(2).uppercase()} 0${if (isOn) 1 else 0}",
            "Set $key = $isOn"
        )
        showToast("Watch Setting Updated", "$key is now ${if (isOn) "ENABLED" else "DISABLED"}.")
    }

    fun cycleVibrationStrength() {
        val modes = listOf("STRONG", "STANDARD", "GENTLE")
        val current = _hwSettings.value.vibration
        val nextIdx = (modes.indexOf(current) + 1) % modes.size
        val nextMode = modes[nextIdx]
        _hwSettings.update { it.copy(vibration = nextMode) }
        bleManager.playHapticBeep(_audioEnabled.value, 520, 100, if (nextIdx == 0) 2 else 1)
        bleManager.sendCommandFrame(
            "AA 09 01 0${nextIdx + 1} C0",
            "Set Haptic Strength = $nextMode"
        )
        showToast("Haptic Intensity", "Wrist vibration set to $nextMode.")
    }

    fun sendMediaCommand(action: String) {
        bleManager.playHapticBeep(_audioEnabled.value, 740, 45, 1)
        val code = when (action) {
            "Play/Pause" -> "01"
            "Next" -> "02"
            else -> "03"
        }
        bleManager.sendCommandFrame("AA 19 01 $code", "Media Control Sync ($action)")
        showToast("Media Bridge", "Dispatched $action command.")
    }

    fun testIncomingCallNotification() {
        bleManager.playHapticBeep(_audioEnabled.value, 620, 140, 3)
        pulseWatchBezel()
        _watchStatePill.value = "INCOMING CALL // VIBRATING"
        bleManager.sendCommandFrame(
            "AA 05 0C 43 41 4C 4C 5F 54 45 53 54",
            "Push Incoming Call Alert (Press Crown once to Answer)"
        )
        showToast(
            "Incoming Call Alert Sent",
            "Watch vibrating — single-press Crown to answer on wrist speaker."
        )
        viewModelScope.launch {
            delay(3000L)
            if (!_workoutActive.value) {
                _watchStatePill.value = "IDLE // MONITORING"
            }
        }
    }

    fun pushWeatherPacket() {
        bleManager.playHapticBeep(_audioEnabled.value, 780, 60, 1)
        bleManager.sendCommandFrame("AA 12 04 1C 16 20 01", "Push Weather Forecast Packet (28°C Clear)")
        showToast("Weather Synced", "Pushed current local weather data (28°C) to CMF Watch Pro 2.")
    }

    // Tab 2: Workout Engine
    fun selectSportMode(sport: SportMode) {
        if (_workoutActive.value) {
            showToast("Workout in Progress", "Pause or stop the current session before switching sports.")
            return
        }
        _selectedSport.value = sport
        bleManager.playHapticBeep(_audioEnabled.value, 640, 50, 1)
    }

    fun toggleAutoLap(enabled: Boolean) {
        _autoLapEnabled.value = enabled
        bleManager.playHapticBeep(_audioEnabled.value, 600, 40, 1)
    }

    fun toggleHrWarning(enabled: Boolean) {
        _hrWarningEnabled.value = enabled
        bleManager.playHapticBeep(_audioEnabled.value, 600, 40, 1)
    }

    fun startWorkout() {
        if (_workoutActive.value && !_workoutPaused.value) return
        val sport = _selectedSport.value
        _workoutActive.value = true
        _workoutPaused.value = false
        _watchStatePill.value = "WORKOUT // ${sport.name.uppercase()}"

        bleManager.playHapticBeep(_audioEnabled.value, 840, 100, 2)
        bleManager.sendCommandFrame("AA 20 03 01 01 D8", "Remote Start Workout: ${sport.name}")
        showToast("Workout Started on Watch", "${sport.name} tracking is live on your wrist.")

        workoutJob?.cancel()
        workoutJob = viewModelScope.launch {
            while (_workoutActive.value) {
                delay(1000L)
                if (_workoutPaused.value) continue
                val elapsed = _workoutElapsedSec.value + 1
                _workoutElapsedSec.value = elapsed

                val simHr = (118 + (elapsed % 35) + Random.nextInt(0, 9)).coerceAtMost(176)
                _workoutHr.value = simHr
                _heartRate.value = simHr
                _workoutHrZone.value = when {
                    simHr < 125 -> "Zone 2 • Fat Burn"
                    simHr < 150 -> "Zone 3 • Aerobic"
                    else -> "Zone 4 • Anaerobic"
                }

                val calsDelta = (sport.met * 70f) / 3600f
                _workoutCalories.value += calsDelta
                if (elapsed % 5 == 0) {
                    _activeCals.update { it + 1 }
                }

                if (sport.hasGps) {
                    _workoutDistanceKm.value += 0.0032f
                    _workoutPace.value = "5'18\""
                    _workoutCadence.value = "166 SPM"
                    if (elapsed % 2 == 0) {
                        _steps.update { it + 5 }
                    }
                } else {
                    _workoutDistanceKm.value = 0f
                    _workoutPace.value = "Active"
                    _workoutCadence.value = "132 BPM Avg"
                }
            }
        }
    }

    fun togglePauseWorkout() {
        if (!_workoutActive.value) return
        val paused = !_workoutPaused.value
        _workoutPaused.value = paused
        bleManager.playHapticBeep(_audioEnabled.value, 540, 80, 1)
        bleManager.sendCommandFrame(
            "AA 20 02 0${if (paused) 2 else 3}",
            "${if (paused) "Pause" else "Resume"} Active Workout"
        )
    }

    fun stopAndSaveWorkout() {
        if (!_workoutActive.value) return
        workoutJob?.cancel()
        val elapsed = _workoutElapsedSec.value
        val durStr = "${elapsed / 60}m ${elapsed % 60}s"
        val sport = _selectedSport.value
        val newSession = WorkoutSession(
            id = System.currentTimeMillis(),
            sport = sport.name,
            date = "Just now",
            duration = durStr,
            distance = if (sport.hasGps) String.format(Locale.US, "%.2f km", _workoutDistanceKm.value) else "Indoor",
            avgHr = "${_workoutHr.value} BPM",
            calories = "${_workoutCalories.value.roundToInt().coerceAtLeast(1)} kcal"
        )
        _workoutHistory.update { listOf(newSession) + it }

        _workoutActive.value = false
        _workoutPaused.value = false
        _workoutElapsedSec.value = 0
        _workoutCalories.value = 0f
        _workoutDistanceKm.value = 0f
        _workoutPace.value = "--'--\""
        _workoutCadence.value = "0 SPM"
        _watchStatePill.value = "IDLE // MONITORING"

        bleManager.playHapticBeep(_audioEnabled.value, 760, 120, 2)
        bleManager.sendCommandFrame("AA 20 02 04 DF", "End & Sync Workout Session ($durStr)")
        showToast("Workout Saved", "${newSession.sport} ($durStr) synced from watch.")
    }

    // Tab 3: Vitals & Goals
    fun updateStepsGoal(goal: Int) {
        _stepsGoal.value = goal.coerceIn(4000, 20000)
        saveGoalsToStore()
    }

    fun updateCaloriesGoal(goal: Int) {
        _calsGoal.value = goal.coerceIn(200, 1200)
        saveGoalsToStore()
    }

    fun refreshVitalsBuffer() {
        _hrChartSeries.value = List(8) { Random.nextInt(60, 98) }
        _spo2ChartSeries.value = List(8) { Random.nextInt(96, 100) }
        _stressChartSeries.value = List(8) { Random.nextInt(20, 56) }
        bleManager.playHapticBeep(_audioEnabled.value, 750, 60, 1)
        logBlePacket("RX", "BB 30 10 3E 3A 40 52 4E 5E 4A 47", "Synced 24h PPG Heart Rate & SpO₂ History Buffer")
        showToast("Vitals Synced", "Pulled latest 24-hour sensor buffer from watch.")
    }

    // Tab 4: Alarms & Wrist Timer
    fun addAlarm(time: String, days: String) {
        val cleanTime = time.ifBlank { "07:30" }
        val cleanDays = days.ifBlank { "Daily" }
        val alarm = WristAlarm(
            id = System.currentTimeMillis(),
            time = cleanTime,
            days = cleanDays,
            enabled = true
        )
        _alarms.update { it + alarm }
        bleManager.playHapticBeep(_audioEnabled.value, 780, 70, 2)
        bleManager.sendCommandFrame(
            "AA 0B 04 ${cleanTime.replace(":", "")} 01",
            "Write New Wrist Vibration Alarm ($cleanTime)"
        )
        showToast("Wrist Alarm Added", "Watch will vibrate at $cleanTime ($cleanDays).")
    }

    fun toggleAlarm(id: Long) {
        var toggled: WristAlarm? = null
        _alarms.update { list ->
            list.map {
                if (it.id == id) {
                    val next = it.copy(enabled = !it.enabled)
                    toggled = next
                    next
                } else {
                    it
                }
            }
        }
        toggled?.let { target ->
            bleManager.playHapticBeep(_audioEnabled.value, 660, 50, 1)
            bleManager.sendCommandFrame(
                "AA 0B 03 ${target.time.replace(":", "")} 0${if (target.enabled) 1 else 0}",
                "Toggle Wrist Alarm ${target.time}"
            )
        }
    }

    fun deleteAlarm(id: Long) {
        _alarms.update { list -> list.filterNot { it.id == id } }
        bleManager.playHapticBeep(_audioEnabled.value, 480, 60, 1)
        bleManager.sendCommandFrame("AA 0B 01 FF", "Delete Wrist Alarm Slot")
    }

    fun selectTimerPreset(seconds: Int) {
        wristTimerJob?.cancel()
        _wristTimerRunning.value = false
        _wristTimerTotalSec.value = seconds
        _wristTimerRemainSec.value = seconds
        bleManager.playHapticBeep(_audioEnabled.value, 620, 45, 1)
    }

    fun toggleWristTimer() {
        if (_wristTimerRunning.value) {
            wristTimerJob?.cancel()
            _wristTimerRunning.value = false
            bleManager.playHapticBeep(_audioEnabled.value, 540, 50, 1)
            return
        }
        if (_wristTimerRemainSec.value <= 0) {
            _wristTimerRemainSec.value = _wristTimerTotalSec.value
        }
        _wristTimerRunning.value = true
        bleManager.playHapticBeep(_audioEnabled.value, 740, 60, 1)
        bleManager.sendCommandFrame(
            "AA 0E 02 ${_wristTimerRemainSec.value.toString(16).uppercase()}",
            "Start Remote Wrist Countdown"
        )
        wristTimerJob?.cancel()
        wristTimerJob = viewModelScope.launch {
            while (_wristTimerRunning.value && _wristTimerRemainSec.value > 0) {
                delay(1000L)
                _wristTimerRemainSec.update { (it - 1).coerceAtLeast(0) }
                if (_wristTimerRemainSec.value == 0) {
                    _wristTimerRunning.value = false
                    bleManager.playHapticBeep(_audioEnabled.value, 920, 150, 4)
                    logBlePacket("RX", "BB 0E 01 00", "Wrist Timer Expired -> Triggered Haptic Alert")
                    showToast("Wrist Timer Complete!", "CMF Watch Pro 2 is vibrating on your wrist.")
                }
            }
        }
    }

    fun resetWristTimer() {
        wristTimerJob?.cancel()
        _wristTimerRunning.value = false
        _wristTimerRemainSec.value = _wristTimerTotalSec.value
        bleManager.playHapticBeep(_audioEnabled.value, 500, 50, 1)
    }

    fun setSedentaryReminder(enabled: Boolean) {
        _sedentaryReminder.value = enabled
        bleManager.playHapticBeep(_audioEnabled.value, 620, 45, 1)
        bleManager.sendCommandFrame("AA 16 01 0${if (enabled) 1 else 0}", "Set Sedentary Stand-Up Alert = $enabled")
    }

    fun setHydrationAlert(enabled: Boolean) {
        _hydrationAlert.value = enabled
        bleManager.playHapticBeep(_audioEnabled.value, 620, 45, 1)
        bleManager.sendCommandFrame("AA 17 01 0${if (enabled) 1 else 0}", "Set Hydration Water Alert = $enabled")
    }

    fun setHighRestingHrThreshold(bpm: Int) {
        _highRestingHrThreshold.value = bpm
        bleManager.playHapticBeep(_audioEnabled.value, 680, 50, 1)
        bleManager.sendCommandFrame(
            "AA 18 02 01 ${bpm.toString(16).uppercase()}",
            "Set High Resting HR Alert Threshold = $bpm BPM"
        )
        showToast("HR Alert Updated", "Watch will vibrate if resting HR exceeds $bpm BPM.")
    }

    // Tab 5: Blind Touch Map & BLE Console
    fun executeBlindQuadrant(quadrant: BlindQuadrant) {
        _selectedBlindReadout.value = quadrant.fullDescription
        bleManager.playHapticBeep(_audioEnabled.value, 700, 60, 1)
        if (quadrant.id == "top_left") {
            _hwSettings.update { it.copy(dnd = !it.dnd) }
        }
        bleManager.sendCommandFrame(
            quadrant.txFrame,
            "Executed Blind Quadrant Equivalent: ${quadrant.positionLabel} (${quadrant.actionTitle})"
        )
        showToast("Quadrant Command Sent", "${quadrant.positionLabel}: ${quadrant.actionTitle}")
    }

    fun sendCustomRawHex(rawHex: String) {
        val clean = rawHex.trim().ifEmpty { "AA 01 00" }
        bleManager.playHapticBeep(_audioEnabled.value, 800, 55, 1)
        bleManager.sendCommandFrame(clean, "Manual Raw GATT Write Request")
        viewModelScope.launch {
            delay(240L)
            logBlePacket("RX", "BB 01 00 ACK", "CMF Watch Pro 2 GATT Notification Response")
        }
        showToast("Raw BLE Frame Sent", "Dispatched $clean to 0xFFF2")
    }

    fun clearBleLogs() {
        _blePacketLogs.value = emptyList()
        bleManager.playHapticBeep(_audioEnabled.value, 500, 40, 1)
    }

    private fun logBlePacket(dir: String, hex: String, desc: String) {
        val fmt = SimpleDateFormat("HH:mm:ss", Locale.US)
        val entry = BlePacketLog(
            id = System.nanoTime(),
            timestamp = fmt.format(Date()),
            direction = dir,
            hex = hex,
            description = desc
        )
        _blePacketLogs.update { (listOf(entry) + it).take(120) }
    }

    fun showToast(title: String, msg: String) {
        toastJob?.cancel()
        _toastMessage.value = ToastMessage(title = title, message = msg)
        toastJob = viewModelScope.launch {
            delay(3200L)
            _toastMessage.value = null
        }
    }
}
