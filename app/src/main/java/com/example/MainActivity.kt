package com.example

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.WristbandTab
import com.example.ui.components.BleDiagnosticModal
import com.example.ui.components.BleTroubleshootBanner
import com.example.ui.components.DotMatrixBackground
import com.example.ui.components.NothingXToastOverlay
import com.example.ui.components.NothingXTopHeader
import com.example.ui.screens.AlarmsScreen
import com.example.ui.screens.BlindMapConsoleScreen
import com.example.ui.screens.HubScreen
import com.example.ui.screens.VitalsScreen
import com.example.ui.screens.WorkoutScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.WristbandViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: WristbandViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                NothingXControllerApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun NothingXControllerApp(viewModel: WristbandViewModel) {
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val showTroubleshootBanner by viewModel.showTroubleshootBanner.collectAsStateWithLifecycle()
    val showBleDiagnosticModal by viewModel.showBleDiagnosticModal.collectAsStateWithLifecycle()
    val toastMessage by viewModel.toastMessage.collectAsStateWithLifecycle()

    val connectionLabel by viewModel.connectionLabel.collectAsStateWithLifecycle()
    val isLiveBleHardware by viewModel.isLiveBleHardware.collectAsStateWithLifecycle()
    val discoveredDevices by viewModel.discoveredDevices.collectAsStateWithLifecycle()
    val isScanningBle by viewModel.isScanningBle.collectAsStateWithLifecycle()
    val audioEnabled by viewModel.audioEnabled.collectAsStateWithLifecycle()
    val deadScreenGuard by viewModel.deadScreenGuard.collectAsStateWithLifecycle()
    val batteryPct by viewModel.batteryPct.collectAsStateWithLifecycle()
    val watchStatePill by viewModel.watchStatePill.collectAsStateWithLifecycle()
    val mirrorFooterStatus by viewModel.mirrorFooterStatus.collectAsStateWithLifecycle()
    val bezelGlowPulse by viewModel.bezelGlowPulse.collectAsStateWithLifecycle()
    val currentTimeStr by viewModel.currentTimeStr.collectAsStateWithLifecycle()
    val currentDateStr by viewModel.currentDateStr.collectAsStateWithLifecycle()

    val heartRate by viewModel.heartRate.collectAsStateWithLifecycle()
    val hrSubtitle by viewModel.hrSubtitle.collectAsStateWithLifecycle()
    val spo2 by viewModel.spo2.collectAsStateWithLifecycle()
    val spo2Subtitle by viewModel.spo2Subtitle.collectAsStateWithLifecycle()
    val stress by viewModel.stress.collectAsStateWithLifecycle()
    val stressSubtitle by viewModel.stressSubtitle.collectAsStateWithLifecycle()
    val activeScanSensor by viewModel.activeScanSensor.collectAsStateWithLifecycle()
    val activeScanProgress by viewModel.activeScanProgress.collectAsStateWithLifecycle()
    val hwSettings by viewModel.hwSettings.collectAsStateWithLifecycle()

    val steps by viewModel.steps.collectAsStateWithLifecycle()
    val stepsGoal by viewModel.stepsGoal.collectAsStateWithLifecycle()
    val activeCals by viewModel.activeCals.collectAsStateWithLifecycle()
    val calsGoal by viewModel.calsGoal.collectAsStateWithLifecycle()
    val hrChartSeries by viewModel.hrChartSeries.collectAsStateWithLifecycle()
    val spo2ChartSeries by viewModel.spo2ChartSeries.collectAsStateWithLifecycle()
    val stressChartSeries by viewModel.stressChartSeries.collectAsStateWithLifecycle()

    val selectedSport by viewModel.selectedSport.collectAsStateWithLifecycle()
    val autoLapEnabled by viewModel.autoLapEnabled.collectAsStateWithLifecycle()
    val hrWarningEnabled by viewModel.hrWarningEnabled.collectAsStateWithLifecycle()
    val workoutActive by viewModel.workoutActive.collectAsStateWithLifecycle()
    val workoutPaused by viewModel.workoutPaused.collectAsStateWithLifecycle()
    val workoutElapsedSec by viewModel.workoutElapsedSec.collectAsStateWithLifecycle()
    val workoutCalories by viewModel.workoutCalories.collectAsStateWithLifecycle()
    val workoutDistanceKm by viewModel.workoutDistanceKm.collectAsStateWithLifecycle()
    val workoutHr by viewModel.workoutHr.collectAsStateWithLifecycle()
    val workoutHrZone by viewModel.workoutHrZone.collectAsStateWithLifecycle()
    val workoutPace by viewModel.workoutPace.collectAsStateWithLifecycle()
    val workoutCadence by viewModel.workoutCadence.collectAsStateWithLifecycle()
    val workoutHistory by viewModel.workoutHistory.collectAsStateWithLifecycle()

    val alarms by viewModel.alarms.collectAsStateWithLifecycle()
    val wristTimerRemainSec by viewModel.wristTimerRemainSec.collectAsStateWithLifecycle()
    val wristTimerRunning by viewModel.wristTimerRunning.collectAsStateWithLifecycle()
    val sedentaryReminder by viewModel.sedentaryReminder.collectAsStateWithLifecycle()
    val hydrationAlert by viewModel.hydrationAlert.collectAsStateWithLifecycle()
    val highRestingHrThreshold by viewModel.highRestingHrThreshold.collectAsStateWithLifecycle()

    val selectedBlindReadout by viewModel.selectedBlindReadout.collectAsStateWithLifecycle()
    val blePacketLogs by viewModel.blePacketLogs.collectAsStateWithLifecycle()

    // Handle Back navigation from secondary tabs back to HUB
    if (selectedTab != WristbandTab.HUB) {
        BackHandler {
            viewModel.selectTab(WristbandTab.HUB)
        }
    }

    // Runtime BLE permission launcher
    val blePermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    val blePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        viewModel.startNativeBleScan()
        viewModel.setShowBleDiagnosticModal(true)
    }

    val triggerBleScanFlow = {
        blePermissionLauncher.launch(blePermissions)
    }

    DotMatrixBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentWindowInsets = WindowInsets.safeDrawing,
            topBar = {
                NothingXTopHeader(
                    connectionLabel = connectionLabel,
                    batteryPct = batteryPct,
                    audioEnabled = audioEnabled,
                    selectedTab = selectedTab,
                    onToggleAudio = viewModel::toggleAudioFeedback,
                    onPairBleClick = triggerBleScanFlow,
                    onOpenDiagnosticModal = { viewModel.setShowBleDiagnosticModal(true) },
                    onSelectTab = viewModel::selectTab
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .widthIn(max = 700.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        AnimatedVisibility(
                            visible = showTroubleshootBanner,
                            enter = expandVertically() + fadeIn(),
                            exit = shrinkVertically() + fadeOut()
                        ) {
                            BleTroubleshootBanner(
                                isBluetoothEnabled = viewModel.bleManager.isBluetoothAvailableAndEnabled(),
                                onScanBleClick = triggerBleScanFlow,
                                onOpenProtocolModal = { viewModel.setShowBleDiagnosticModal(true) },
                                onDismiss = { viewModel.setShowTroubleshootBanner(false) }
                            )
                        }

                        AnimatedContent(
                            targetState = selectedTab,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(220)) togetherWith
                                    fadeOut(animationSpec = tween(160))
                            },
                            label = "tabTransition"
                        ) { tab ->
                            when (tab) {
                                WristbandTab.HUB -> HubScreen(
                                    watchStatePill = watchStatePill,
                                    mirrorFooterStatus = mirrorFooterStatus,
                                    bezelGlowPulse = bezelGlowPulse,
                                    currentTimeStr = currentTimeStr,
                                    currentDateStr = currentDateStr,
                                    batteryPct = batteryPct,
                                    heartRate = heartRate,
                                    hrSubtitle = hrSubtitle,
                                    spo2 = spo2,
                                    spo2Subtitle = spo2Subtitle,
                                    stress = stress,
                                    stressSubtitle = stressSubtitle,
                                    steps = steps,
                                    deadScreenGuard = deadScreenGuard,
                                    hwSettings = hwSettings,
                                    activeScanSensor = activeScanSensor,
                                    activeScanProgress = activeScanProgress,
                                    onCrownHomeReset = viewModel::triggerCrownHomeReset,
                                    onFindWatch = viewModel::triggerFindWatch,
                                    onSyncTime = viewModel::triggerSyncTimeAndTimezone,
                                    onToggleDeadScreenGuard = viewModel::toggleDeadScreenGuard,
                                    onStartSensorScan = viewModel::startOnDemandSensorScan,
                                    onToggleHwSetting = viewModel::toggleHardwareSetting,
                                    onCycleVibration = viewModel::cycleVibrationStrength,
                                    onMediaCommand = viewModel::sendMediaCommand,
                                    onTestIncomingCall = viewModel::testIncomingCallNotification,
                                    onPushWeather = viewModel::pushWeatherPacket
                                )

                                WristbandTab.WORKOUT -> WorkoutScreen(
                                    sportModes = viewModel.sportModes,
                                    selectedSport = selectedSport,
                                    autoLapEnabled = autoLapEnabled,
                                    hrWarningEnabled = hrWarningEnabled,
                                    workoutActive = workoutActive,
                                    workoutPaused = workoutPaused,
                                    workoutElapsedSec = workoutElapsedSec,
                                    workoutCalories = workoutCalories,
                                    workoutDistanceKm = workoutDistanceKm,
                                    workoutHr = workoutHr,
                                    workoutHrZone = workoutHrZone,
                                    workoutPace = workoutPace,
                                    workoutCadence = workoutCadence,
                                    workoutHistory = workoutHistory,
                                    onSelectSport = viewModel::selectSportMode,
                                    onToggleAutoLap = viewModel::toggleAutoLap,
                                    onToggleHrWarning = viewModel::toggleHrWarning,
                                    onStartWorkout = viewModel::startWorkout,
                                    onPauseWorkout = viewModel::togglePauseWorkout,
                                    onStopWorkout = viewModel::stopAndSaveWorkout
                                )

                                WristbandTab.VITALS -> VitalsScreen(
                                    steps = steps,
                                    stepsGoal = stepsGoal,
                                    activeCals = activeCals,
                                    calsGoal = calsGoal,
                                    chartHours = viewModel.chartHours,
                                    hrChartSeries = hrChartSeries,
                                    spo2ChartSeries = spo2ChartSeries,
                                    stressChartSeries = stressChartSeries,
                                    onUpdateStepsGoal = viewModel::updateStepsGoal,
                                    onUpdateCalsGoal = viewModel::updateCaloriesGoal,
                                    onRefreshVitalsBuffer = viewModel::refreshVitalsBuffer
                                )

                                WristbandTab.ALARMS -> AlarmsScreen(
                                    alarms = alarms,
                                    wristTimerRemainSec = wristTimerRemainSec,
                                    wristTimerRunning = wristTimerRunning,
                                    sedentaryReminder = sedentaryReminder,
                                    hydrationAlert = hydrationAlert,
                                    highRestingHrThreshold = highRestingHrThreshold,
                                    onAddAlarm = viewModel::addAlarm,
                                    onToggleAlarm = viewModel::toggleAlarm,
                                    onDeleteAlarm = viewModel::deleteAlarm,
                                    onSelectTimerPreset = viewModel::selectTimerPreset,
                                    onToggleWristTimer = viewModel::toggleWristTimer,
                                    onResetWristTimer = viewModel::resetWristTimer,
                                    onSetSedentaryReminder = viewModel::setSedentaryReminder,
                                    onSetHydrationAlert = viewModel::setHydrationAlert,
                                    onSetHighRestingHrThreshold = viewModel::setHighRestingHrThreshold
                                )

                                WristbandTab.CONSOLE -> BlindMapConsoleScreen(
                                    blindQuadrants = viewModel.blindQuadrants,
                                    selectedBlindReadout = selectedBlindReadout,
                                    blePacketLogs = blePacketLogs,
                                    onExecuteBlindQuadrant = viewModel::executeBlindQuadrant,
                                    onSendRawHex = viewModel::sendCustomRawHex,
                                    onClearBleLogs = viewModel::clearBleLogs,
                                    onShowToast = viewModel::showToast
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(64.dp))
                    }
                }

                // Bottom Toast Overlay
                NothingXToastOverlay(
                    toastMessage = toastMessage,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )

                // BLE Diagnostic & GATT Protocol Modal
                if (showBleDiagnosticModal) {
                    BleDiagnosticModal(
                        isBluetoothEnabled = viewModel.bleManager.isBluetoothAvailableAndEnabled(),
                        isLiveHardware = isLiveBleHardware,
                        isScanning = isScanningBle,
                        discoveredDevices = discoveredDevices,
                        onStartScan = viewModel::startNativeBleScan,
                        onConnectDevice = viewModel::connectToDevice,
                        onSimulateHandshake = viewModel::simulateAtGetSecretHandshake,
                        onDismiss = { viewModel.setShowBleDiagnosticModal(false) }
                    )
                }
            }
        }
    }
}
