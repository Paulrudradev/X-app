package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.HardwareSettings
import com.example.model.ScanSensorType
import com.example.ui.components.SpringButton
import com.example.ui.theme.AmberGold
import com.example.ui.theme.AmberTint
import com.example.ui.theme.CmfBorder
import com.example.ui.theme.CmfCard
import com.example.ui.theme.CmfMuted
import com.example.ui.theme.CmfOrange
import com.example.ui.theme.CmfSurface
import com.example.ui.theme.CmfTextPrimary
import com.example.ui.theme.DotMatrixFont
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldTint
import com.example.ui.theme.MonoFont
import com.example.ui.theme.RosePink
import com.example.ui.theme.RoseTint
import com.example.ui.theme.SkyBlue
import com.example.ui.theme.SkyTint

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HubScreen(
    watchStatePill: String,
    mirrorFooterStatus: String,
    bezelGlowPulse: Boolean,
    currentTimeStr: String,
    currentDateStr: String,
    batteryPct: Int,
    heartRate: Int,
    hrSubtitle: String,
    spo2: Int,
    spo2Subtitle: String,
    stress: Int,
    stressSubtitle: String,
    steps: Int,
    deadScreenGuard: Boolean,
    hwSettings: HardwareSettings,
    activeScanSensor: ScanSensorType?,
    activeScanProgress: Int,
    onCrownHomeReset: () -> Unit,
    onFindWatch: () -> Unit,
    onSyncTime: () -> Unit,
    onToggleDeadScreenGuard: () -> Unit,
    onStartSensorScan: (ScanSensorType) -> Unit,
    onToggleHwSetting: (String) -> Unit,
    onCycleVibration: () -> Unit,
    onMediaCommand: (String) -> Unit,
    onTestIncomingCall: () -> Unit,
    onPushWeather: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Virtual CMF Watch Pro 2 OLED Mirror & Dead-Screen Guard Card
        VirtualWatchMirrorCard(
            watchStatePill = watchStatePill,
            mirrorFooterStatus = mirrorFooterStatus,
            bezelGlowPulse = bezelGlowPulse,
            currentTimeStr = currentTimeStr,
            currentDateStr = currentDateStr,
            batteryPct = batteryPct,
            heartRate = heartRate,
            steps = steps,
            dndEnabled = hwSettings.dnd,
            deadScreenGuard = deadScreenGuard,
            activeScanSensor = activeScanSensor,
            onCrownHomeReset = onCrownHomeReset,
            onFindWatch = onFindWatch,
            onSyncTime = onSyncTime,
            onToggleDeadScreenGuard = onToggleDeadScreenGuard
        )

        // 2. Remote PPG Optical Sensor Triggers
        OnDemandPpgSensorCard(
            heartRate = heartRate,
            hrSubtitle = hrSubtitle,
            spo2 = spo2,
            spo2Subtitle = spo2Subtitle,
            stress = stress,
            stressSubtitle = stressSubtitle,
            activeScanSensor = activeScanSensor,
            activeScanProgress = activeScanProgress,
            onStartSensorScan = onStartSensorScan
        )

        // 3. Direct BLE Wristband Hardware Controls & Media Bridge
        HardwareControlsCard(
            hwSettings = hwSettings,
            onToggleHwSetting = onToggleHwSetting,
            onCycleVibration = onCycleVibration,
            onMediaCommand = onMediaCommand,
            onTestIncomingCall = onTestIncomingCall,
            onPushWeather = onPushWeather
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VirtualWatchMirrorCard(
    watchStatePill: String,
    mirrorFooterStatus: String,
    bezelGlowPulse: Boolean,
    currentTimeStr: String,
    currentDateStr: String,
    batteryPct: Int,
    heartRate: Int,
    steps: Int,
    dndEnabled: Boolean,
    deadScreenGuard: Boolean,
    activeScanSensor: ScanSensorType?,
    onCrownHomeReset: () -> Unit,
    onFindWatch: () -> Unit,
    onSyncTime: () -> Unit,
    onToggleDeadScreenGuard: () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "watchMirrorAnim")
    val scanRingAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "scanRingAngle"
    )
    val pulseRadiusFactor by infiniteTransition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseRadiusFactor"
    )

    val animatedHr by animateIntAsState(
        targetValue = heartRate,
        animationSpec = tween(450),
        label = "animatedHr"
    )

    val bezelBorderColor by animateColorAsState(
        targetValue = when {
            bezelGlowPulse -> CmfOrange
            activeScanSensor == ScanSensorType.HR -> RosePink
            activeScanSensor == ScanSensorType.SPO2 -> SkyBlue
            activeScanSensor == ScanSensorType.STRESS -> AmberGold
            else -> Color(0xFF3F3F46)
        },
        animationSpec = tween(350),
        label = "bezelBorderColor"
    )

    Surface(
        color = CmfCard,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CmfBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VIRTUAL OLED MIRROR",
                        fontFamily = MonoFont,
                        fontSize = 10.sp,
                        color = CmfOrange
                    )
                    Text(
                        text = "Live Wristband State",
                        fontFamily = DotMatrixFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CmfTextPrimary
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CmfSurface)
                        .border(1.dp, CmfBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                ) {
                    Text(
                        text = watchStatePill,
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 10.sp,
                        color = if (activeScanSensor != null) CmfOrange else EmeraldGreen
                    )
                }
            }

            // Circular CMF Watch Pro 2 Hardware Visualization with Functional Crown Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.size(244.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Physical Functional Crown Button on Right Top
                    SpringButton(
                        onClick = onCrownHomeReset,
                        backgroundColor = CmfOrange,
                        borderColor = Color(0xFFFDBA74),
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                        testTag = "virtual_crown_btn",
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 8.dp, y = 50.dp)
                            .width(18.dp)
                            .height(44.dp)
                    ) {}

                    // Metallic Circular Watch Bezel
                    Box(
                        modifier = Modifier
                            .size(236.dp)
                            .shadow(
                                elevation = if (bezelGlowPulse || activeScanSensor != null) 24.dp else 10.dp,
                                shape = CircleShape,
                                ambientColor = CmfOrange,
                                spotColor = CmfOrange
                            )
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF3F3F46),
                                        Color(0xFF18181B),
                                        Color.Black
                                    )
                                )
                            )
                            .border(4.dp, bezelBorderColor, CircleShape)
                            .padding(14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        // Inner OLED Virtual Screen
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(Color.Black)
                                .border(1.dp, Color(0xFF27272A), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            // Animated PPG Laser Ring when scanning or subtle pulse ring
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokePx = 3.dp.toPx()
                                val radius = (size.minDimension / 2f - strokePx * 2f) * pulseRadiusFactor
                                drawCircle(
                                    color = bezelBorderColor.copy(alpha = 0.22f),
                                    radius = radius,
                                    style = Stroke(width = strokePx)
                                )
                                if (activeScanSensor != null) {
                                    drawArc(
                                        color = bezelBorderColor,
                                        startAngle = scanRingAngle,
                                        sweepAngle = 110f,
                                        useCenter = false,
                                        style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round)
                                    )
                                }
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(18.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Top Status Row
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    if (dndEnabled) {
                                        Text(
                                            text = "🌙 DND",
                                            fontFamily = MonoFont,
                                            fontSize = 9.sp,
                                            color = AmberGold
                                        )
                                    }
                                    Text(
                                        text = "BT 5.3",
                                        fontFamily = MonoFont,
                                        fontSize = 9.sp,
                                        color = CmfMuted
                                    )
                                    Text(
                                        text = "$batteryPct%",
                                        fontFamily = MonoFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        color = CmfOrange
                                    )
                                }

                                // Center Watchface Digital Readout
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = currentTimeStr,
                                        fontFamily = DotMatrixFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 34.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = currentDateStr,
                                        fontFamily = MonoFont,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp,
                                        color = CmfOrange
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                    ) {
                                        Text(
                                            text = "♥ $animatedHr BPM",
                                            fontFamily = MonoFont,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            color = RosePink
                                        )
                                        Text(
                                            text = "👟 ${String.format("%,d", steps)}",
                                            fontFamily = MonoFont,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 11.sp,
                                            color = SkyBlue
                                        )
                                    }
                                }

                                // Bottom Sensor Footer
                                Text(
                                    text = mirrorFooterStatus.uppercase(),
                                    fontFamily = MonoFont,
                                    fontSize = 9.sp,
                                    color = if (activeScanSensor != null) CmfOrange else CmfMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            // 3 Quick Hardware Action Buttons below Watch Mirror
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpringButton(
                    onClick = onCrownHomeReset,
                    backgroundColor = CmfSurface,
                    borderColor = CmfBorder,
                    shape = RoundedCornerShape(10.dp),
                    testTag = "crown_home_btn"
                ) {
                    Text(
                        text = "🔘 Crown Home Reset",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = CmfTextPrimary,
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp)
                    )
                }

                SpringButton(
                    onClick = onFindWatch,
                    backgroundColor = CmfOrange.copy(alpha = 0.18f),
                    borderColor = CmfOrange.copy(alpha = 0.55f),
                    shape = RoundedCornerShape(10.dp),
                    testTag = "find_watch_btn"
                ) {
                    Text(
                        text = "📳 Vibrate / Find Watch",
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = CmfOrange,
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp)
                    )
                }

                SpringButton(
                    onClick = onSyncTime,
                    backgroundColor = CmfSurface,
                    borderColor = CmfBorder,
                    shape = RoundedCornerShape(10.dp),
                    testTag = "sync_time_btn"
                ) {
                    Text(
                        text = "🕒 Sync Time & TZ",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = CmfTextPrimary,
                        modifier = Modifier.padding(horizontal = 11.dp, vertical = 7.dp)
                    )
                }
            }

            // Dead-Screen Guard Mode Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CmfSurface)
                    .border(
                        1.dp,
                        if (deadScreenGuard) CmfOrange.copy(alpha = 0.45f) else CmfBorder,
                        RoundedCornerShape(14.dp)
                    )
                    .clickable(onClick = onToggleDeadScreenGuard)
                    .padding(14.dp)
                    .testTag("dead_screen_guard_toggle"),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "DEAD-SCREEN GUARD MODE",
                            fontFamily = DotMatrixFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = CmfOrange
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (deadScreenGuard) EmeraldTint else Color(0xFF27272A))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (deadScreenGuard) "ACTIVE" else "BYPASSED",
                                fontFamily = MonoFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp,
                                color = if (deadScreenGuard) EmeraldGreen else CmfMuted
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Disables Lift-to-Wake, AOD & Touch-Wake to prevent accidental ghost touches on your blacked-out display.",
                        fontFamily = MonoFont,
                        fontSize = 10.sp,
                        lineHeight = 14.sp,
                        color = CmfMuted
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Custom Animated Toggle Switch
                val thumbAlign = if (deadScreenGuard) Alignment.CenterEnd else Alignment.CenterStart
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(26.dp)
                        .clip(RoundedCornerShape(50))
                        .background(if (deadScreenGuard) CmfOrange else Color(0xFF3F3F46))
                        .padding(4.dp),
                    contentAlignment = thumbAlign
                ) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(Color.Black)
                    )
                }
            }
        }
    }
}

@Composable
private fun OnDemandPpgSensorCard(
    heartRate: Int,
    hrSubtitle: String,
    spo2: Int,
    spo2Subtitle: String,
    stress: Int,
    stressSubtitle: String,
    activeScanSensor: ScanSensorType?,
    activeScanProgress: Int,
    onStartSensorScan: (ScanSensorType) -> Unit
) {
    val animatedHr by animateIntAsState(heartRate, tween(400), label = "hrVal")
    val animatedSpo2 by animateIntAsState(spo2, tween(400), label = "spo2Val")
    val animatedStress by animateIntAsState(stress, tween(400), label = "stressVal")
    val animatedProgress by animateFloatAsState(
        targetValue = (activeScanProgress / 100f).coerceIn(0f, 1f),
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "scanProgress"
    )

    Surface(
        color = CmfCard,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CmfBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column {
                Text(
                    text = "REMOTE PPG SENSOR TRIGGER",
                    fontFamily = MonoFont,
                    fontSize = 10.sp,
                    color = CmfOrange
                )
                Text(
                    text = "On-Demand Vitals Scan (No Watch Screen Needed)",
                    fontFamily = DotMatrixFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = CmfTextPrimary
                )
            }

            // 3 Sensor Cards
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SensorMeasurementCard(
                    title = "HEART RATE",
                    valueText = "$animatedHr",
                    unitText = "BPM",
                    subtitle = hrSubtitle,
                    accentColor = RosePink,
                    buttonBg = RoseTint,
                    buttonLabel = if (activeScanSensor == ScanSensorType.HR) "Scanning Green PPG LED..." else "▶ Measure HR (15s)",
                    testTag = "scan_hr_btn",
                    onClick = { onStartSensorScan(ScanSensorType.HR) }
                )

                SensorMeasurementCard(
                    title = "BLOOD OXYGEN",
                    valueText = "$animatedSpo2",
                    unitText = "% SpO₂",
                    subtitle = spo2Subtitle,
                    accentColor = SkyBlue,
                    buttonBg = SkyTint,
                    buttonLabel = if (activeScanSensor == ScanSensorType.SPO2) "Scanning Red + IR LED..." else "▶ Measure SpO₂ (20s)",
                    testTag = "scan_spo2_btn",
                    onClick = { onStartSensorScan(ScanSensorType.SPO2) }
                )

                SensorMeasurementCard(
                    title = "HRV STRESS",
                    valueText = "$animatedStress",
                    unitText = "/ 100",
                    subtitle = stressSubtitle,
                    accentColor = AmberGold,
                    buttonBg = AmberTint,
                    buttonLabel = if (activeScanSensor == ScanSensorType.STRESS) "Analyzing R-R Intervals..." else "▶ Measure Stress (15s)",
                    testTag = "scan_stress_btn",
                    onClick = { onStartSensorScan(ScanSensorType.STRESS) }
                )
            }

            // Animated Active Scan Progress Bar
            AnimatedVisibility(
                visible = activeScanSensor != null,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                if (activeScanSensor != null) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .border(1.dp, CmfOrange.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = activeScanSensor.scanningHeadline,
                                fontFamily = MonoFont,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp,
                                color = CmfOrange,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "$activeScanProgress%",
                                fontFamily = MonoFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(50))
                                .background(Color(0xFF27272A))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .fillMaxWidth(animatedProgress)
                                    .clip(RoundedCornerShape(50))
                                    .background(CmfOrange)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SensorMeasurementCard(
    title: String,
    valueText: String,
    unitText: String,
    subtitle: String,
    accentColor: Color,
    buttonBg: Color,
    buttonLabel: String,
    testTag: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CmfSurface)
            .border(1.dp, CmfBorder, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                    Text(
                        text = title,
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = accentColor
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontFamily = MonoFont,
                    fontSize = 10.sp,
                    color = CmfMuted
                )
            }

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = valueText,
                    fontFamily = DotMatrixFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 28.sp,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unitText,
                    fontFamily = MonoFont,
                    fontSize = 11.sp,
                    color = CmfMuted,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
            }
        }

        SpringButton(
            onClick = onClick,
            backgroundColor = buttonBg,
            borderColor = accentColor.copy(alpha = 0.45f),
            shape = RoundedCornerShape(10.dp),
            testTag = testTag,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = buttonLabel,
                fontFamily = MonoFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                color = accentColor,
                modifier = Modifier.padding(vertical = 9.dp)
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HardwareControlsCard(
    hwSettings: HardwareSettings,
    onToggleHwSetting: (String) -> Unit,
    onCycleVibration: () -> Unit,
    onMediaCommand: (String) -> Unit,
    onTestIncomingCall: () -> Unit,
    onPushWeather: () -> Unit
) {
    Surface(
        color = CmfCard,
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.dp, CmfBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "DIRECT BLE CONFIGURATION",
                        fontFamily = MonoFont,
                        fontSize = 10.sp,
                        color = CmfOrange
                    )
                    Text(
                        text = "Wristband Hardware Controls",
                        fontFamily = DotMatrixFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CmfTextPrimary
                    )
                }
                Text(
                    text = "Instant sync to D398",
                    fontFamily = MonoFont,
                    fontSize = 10.sp,
                    color = CmfMuted
                )
            }

            // 2x3 Grid of Hardware Toggle Cards
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HwToggleTile(
                        emoji = "🌙",
                        title = "Do Not Disturb",
                        subtitle = "Mute wrist vibrations",
                        badgeText = if (hwSettings.dnd) "ON" else "OFF",
                        isActive = hwSettings.dnd,
                        onClick = { onToggleHwSetting("dnd") },
                        testTag = "hw_dnd_btn",
                        modifier = Modifier.weight(1f)
                    )
                    HwToggleTile(
                        emoji = "⌚",
                        title = "Lift Wrist to Wake",
                        subtitle = "Keep OFF for dead screen",
                        badgeText = if (hwSettings.liftWake) "ON" else "OFF (SAVED)",
                        isActive = hwSettings.liftWake,
                        onClick = { onToggleHwSetting("liftWake") },
                        testTag = "hw_liftwake_btn",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HwToggleTile(
                        emoji = "📞",
                        title = "BT Calling & Mic",
                        subtitle = "Answer via Crown press",
                        badgeText = if (hwSettings.btCalling) "ON" else "MUTED",
                        isActive = hwSettings.btCalling,
                        onClick = { onToggleHwSetting("btCalling") },
                        testTag = "hw_btcalling_btn",
                        modifier = Modifier.weight(1f)
                    )
                    HwToggleTile(
                        emoji = "❤️",
                        title = "24h Heart Rate",
                        subtitle = "Continuous PPG logging",
                        badgeText = if (hwSettings.autoHr) "ON (5m)" else "OFF",
                        isActive = hwSettings.autoHr,
                        onClick = { onToggleHwSetting("autoHr") },
                        testTag = "hw_autohr_btn",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HwToggleTile(
                        emoji = "🫧",
                        title = "All-Day SpO₂",
                        subtitle = "Blood oxygen tracking",
                        badgeText = if (hwSettings.autoSpo2) "ON" else "OFF",
                        isActive = hwSettings.autoSpo2,
                        onClick = { onToggleHwSetting("autoSpo2") },
                        testTag = "hw_autospo2_btn",
                        modifier = Modifier.weight(1f)
                    )
                    HwToggleTile(
                        emoji = "📳",
                        title = "Haptic Strength",
                        subtitle = "Essential for blind cues",
                        badgeText = hwSettings.vibration,
                        isActive = true,
                        onClick = onCycleVibration,
                        testTag = "hw_vibration_btn",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider(color = CmfBorder)

            // Media & Phone Bridge Controls
            Text(
                text = "MEDIA & PHONE BRIDGE",
                fontFamily = MonoFont,
                fontSize = 10.sp,
                color = CmfMuted
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpringButton(
                    onClick = { onMediaCommand("Prev") },
                    backgroundColor = CmfSurface,
                    borderColor = CmfBorder,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "⏮ Prev",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = CmfTextPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
                SpringButton(
                    onClick = { onMediaCommand("Play/Pause") },
                    backgroundColor = CmfOrange,
                    borderColor = CmfOrange,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "⏯ Play/Pause",
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
                SpringButton(
                    onClick = { onMediaCommand("Next") },
                    backgroundColor = CmfSurface,
                    borderColor = CmfBorder,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "⏭ Next",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = CmfTextPrimary,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
                SpringButton(
                    onClick = onTestIncomingCall,
                    backgroundColor = CmfSurface,
                    borderColor = EmeraldGreen.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    testTag = "test_call_vibe_btn"
                ) {
                    Text(
                        text = "📲 Test Incoming Call Vibe",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = EmeraldGreen,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
                SpringButton(
                    onClick = onPushWeather,
                    backgroundColor = CmfSurface,
                    borderColor = SkyBlue.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(8.dp),
                    testTag = "push_weather_btn"
                ) {
                    Text(
                        text = "⛅ Push Weather (28°C)",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = SkyBlue,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun HwToggleTile(
    emoji: String,
    title: String,
    subtitle: String,
    badgeText: String,
    isActive: Boolean,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    val borderColor by animateColorAsState(
        targetValue = if (isActive) CmfOrange.copy(alpha = 0.55f) else CmfBorder,
        animationSpec = tween(220),
        label = "hwTileBorder"
    )
    SpringButton(
        onClick = onClick,
        backgroundColor = CmfSurface,
        borderColor = borderColor,
        shape = RoundedCornerShape(14.dp),
        testTag = testTag,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = emoji, fontSize = 16.sp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isActive) CmfOrange.copy(alpha = 0.2f) else Color(0xFF27272A))
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 9.sp,
                        color = if (isActive) CmfOrange else CmfMuted
                    )
                }
            }
            Column {
                Text(
                    text = title,
                    fontFamily = MonoFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp,
                    color = CmfTextPrimary
                )
                Text(
                    text = subtitle,
                    fontFamily = MonoFont,
                    fontSize = 10.sp,
                    color = CmfMuted
                )
            }
        }
    }
}
