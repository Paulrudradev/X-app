package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SportMode
import com.example.model.WorkoutSession
import com.example.ui.components.SpringButton
import com.example.ui.theme.AmberGold
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
import com.example.ui.theme.RoseRed
import com.example.ui.theme.RoseTint
import com.example.ui.theme.SkyBlue
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WorkoutScreen(
    sportModes: List<SportMode>,
    selectedSport: SportMode,
    autoLapEnabled: Boolean,
    hrWarningEnabled: Boolean,
    workoutActive: Boolean,
    workoutPaused: Boolean,
    workoutElapsedSec: Int,
    workoutCalories: Float,
    workoutDistanceKm: Float,
    workoutHr: Int,
    workoutHrZone: String,
    workoutPace: String,
    workoutCadence: String,
    workoutHistory: List<WorkoutSession>,
    onSelectSport: (SportMode) -> Unit,
    onToggleAutoLap: (Boolean) -> Unit,
    onToggleHrWarning: (Boolean) -> Unit,
    onStartWorkout: () -> Unit,
    onPauseWorkout: () -> Unit,
    onStopWorkout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Sport Mode Selector & Launch Card
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
                Column {
                    Text(
                        text = "HEADLESS SPORTS ENGINE",
                        fontFamily = MonoFont,
                        fontSize = 10.sp,
                        color = CmfOrange
                    )
                    Text(
                        text = "Launch Workout from Phone",
                        fontFamily = DotMatrixFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CmfTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Starts CMF Watch Pro 2 optical HR + built-in multi-system GPS without touching the watch screen.",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = CmfMuted
                    )
                }

                // 2x3 Sport Mode Grid
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    sportModes.chunked(2).forEach { rowModes ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowModes.forEach { mode ->
                                val isSelected = selectedSport.name == mode.name
                                val bg by animateColorAsState(
                                    targetValue = if (isSelected) CmfOrange.copy(alpha = 0.2f) else CmfSurface,
                                    animationSpec = tween(200),
                                    label = "sportCardBg"
                                )
                                val border by animateColorAsState(
                                    targetValue = if (isSelected) CmfOrange else CmfBorder,
                                    animationSpec = tween(200),
                                    label = "sportCardBorder"
                                )
                                SpringButton(
                                    onClick = { onSelectSport(mode) },
                                    backgroundColor = bg,
                                    borderColor = border,
                                    shape = RoundedCornerShape(14.dp),
                                    testTag = "sport_${mode.name.lowercase().replace(" ", "_")}",
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(12.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(text = mode.emoji, fontSize = 18.sp)
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(
                                                        when {
                                                            isSelected && mode.hasGps -> CmfOrange
                                                            mode.hasGps -> EmeraldTint
                                                            else -> Color(0xFF27272A)
                                                        }
                                                    )
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = if (mode.hasGps) "GPS" else "INDOOR",
                                                    fontFamily = MonoFont,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 9.sp,
                                                    color = when {
                                                        isSelected && mode.hasGps -> Color.Black
                                                        mode.hasGps -> EmeraldGreen
                                                        else -> CmfMuted
                                                    }
                                                )
                                            }
                                        }
                                        Column {
                                            Text(
                                                text = mode.name,
                                                fontFamily = MonoFont,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = CmfTextPrimary
                                            )
                                            Text(
                                                text = mode.subtitle,
                                                fontFamily = MonoFont,
                                                fontSize = 10.sp,
                                                color = CmfMuted
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Haptic & Audio Pace Alerts Config
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CmfSurface)
                        .border(1.dp, CmfBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Wrist Haptic Auto-Lap (Every 1 km)",
                            fontFamily = MonoFont,
                            fontSize = 11.sp,
                            color = CmfTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = autoLapEnabled,
                            onCheckedChange = onToggleAutoLap,
                            colors = CheckboxDefaults.colors(
                                checkedColor = CmfOrange,
                                checkmarkColor = Color.Black
                            )
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "High Heart Rate Vibration Warning (>172 BPM)",
                            fontFamily = MonoFont,
                            fontSize = 11.sp,
                            color = CmfTextPrimary,
                            modifier = Modifier.weight(1f)
                        )
                        Checkbox(
                            checked = hrWarningEnabled,
                            onCheckedChange = onToggleHrWarning,
                            colors = CheckboxDefaults.colors(
                                checkedColor = CmfOrange,
                                checkmarkColor = Color.Black
                            )
                        )
                    }
                }

                // Workout Action Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SpringButton(
                        onClick = onStartWorkout,
                        enabled = !workoutActive,
                        backgroundColor = CmfOrange,
                        borderColor = CmfOrange,
                        shape = RoundedCornerShape(12.dp),
                        testTag = "start_workout_btn",
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Text(
                            text = "▶ START ON WATCH",
                            fontFamily = DotMatrixFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    SpringButton(
                        onClick = onPauseWorkout,
                        enabled = workoutActive,
                        backgroundColor = CmfSurface,
                        borderColor = CmfBorder,
                        shape = RoundedCornerShape(12.dp),
                        testTag = "pause_workout_btn",
                        modifier = Modifier.weight(0.85f)
                    ) {
                        Text(
                            text = if (workoutPaused) "▶ RESUME" else "⏸ PAUSE",
                            fontFamily = DotMatrixFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = CmfTextPrimary,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }

                    SpringButton(
                        onClick = onStopWorkout,
                        enabled = workoutActive,
                        backgroundColor = RoseTint,
                        borderColor = RosePink.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp),
                        testTag = "stop_workout_btn",
                        modifier = Modifier.weight(0.95f)
                    ) {
                        Text(
                            text = "⏹ END & SAVE",
                            fontFamily = DotMatrixFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = RosePink,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )
                    }
                }
            }
        }

        // 2. Live Wristband Workout HUD Card
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SELECTED: ${selectedSport.name.uppercase()} (${if (selectedSport.hasGps) "GPS LOCKED" else "INDOOR PPG"})",
                            fontFamily = MonoFont,
                            fontSize = 10.sp,
                            color = CmfOrange
                        )
                        Text(
                            text = "Live Wristband Workout HUD",
                            fontFamily = DotMatrixFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = CmfTextPrimary
                        )
                    }
                    Text(
                        text = formatWorkoutDuration(workoutElapsedSec),
                        fontFamily = DotMatrixFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp,
                        color = CmfOrange
                    )
                }

                // 2x2 Telemetry Grid
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TelemetryMetricBox(
                        label = "LIVE HEART RATE",
                        value = "$workoutHr",
                        unit = "BPM",
                        footer = workoutHrZone,
                        valueColor = RosePink,
                        footerColor = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryMetricBox(
                        label = "ACTIVE CALORIES",
                        value = "${workoutCalories.roundToInt()}",
                        unit = "kcal",
                        footer = "MET Burn Rate",
                        valueColor = AmberGold,
                        footerColor = CmfMuted,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    TelemetryMetricBox(
                        label = "DISTANCE",
                        value = String.format(Locale.US, "%.2f", workoutDistanceKm),
                        unit = "km",
                        footer = if (selectedSport.hasGps) "📡 L1 GPS: Strong" else "🏠 Indoor Sensor Mode",
                        valueColor = SkyBlue,
                        footerColor = EmeraldGreen,
                        modifier = Modifier.weight(1f)
                    )
                    TelemetryMetricBox(
                        label = "AVG PACE / CADENCE",
                        value = workoutPace,
                        unit = "/km",
                        footer = workoutCadence,
                        valueColor = Color.White,
                        footerColor = CmfMuted,
                        modifier = Modifier.weight(1f)
                    )
                }

                // HR Zone Strip
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "HR Zone Distribution",
                            fontFamily = MonoFont,
                            fontSize = 10.sp,
                            color = CmfMuted
                        )
                        Text(
                            text = "Warm-up / Fat Burn / Aerobic / Anaerobic / Extreme",
                            fontFamily = MonoFont,
                            fontSize = 9.sp,
                            color = CmfMuted
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF27272A))
                    ) {
                        Box(modifier = Modifier.weight(0.20f).fillMaxHeight().background(SkyBlue))
                        Box(modifier = Modifier.weight(0.30f).fillMaxHeight().background(EmeraldGreen))
                        Box(modifier = Modifier.weight(0.25f).fillMaxHeight().background(AmberGold))
                        Box(modifier = Modifier.weight(0.15f).fillMaxHeight().background(CmfOrange))
                        Box(modifier = Modifier.weight(0.10f).fillMaxHeight().background(RoseRed))
                    }
                }
            }
        }

        // 3. Recent Synced Workouts Log
        Surface(
            color = CmfCard,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, CmfBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SYNCED WRISTBAND SESSIONS",
                        fontFamily = DotMatrixFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = CmfTextPrimary
                    )
                    Text(
                        text = "Nothing X Local DB",
                        fontFamily = MonoFont,
                        fontSize = 10.sp,
                        color = CmfMuted
                    )
                }

                workoutHistory.forEach { session ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(CmfSurface)
                            .border(1.dp, CmfBorder, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = session.sport,
                                    fontFamily = MonoFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = CmfTextPrimary
                                )
                                Text(
                                    text = " • ${session.duration}",
                                    fontFamily = MonoFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = CmfOrange
                                )
                            }
                            Text(
                                text = session.date,
                                fontFamily = MonoFont,
                                fontSize = 10.sp,
                                color = CmfMuted
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = session.distance,
                                fontFamily = MonoFont,
                                fontSize = 11.sp,
                                color = SkyBlue
                            )
                            Text(
                                text = session.avgHr,
                                fontFamily = MonoFont,
                                fontSize = 11.sp,
                                color = RosePink
                            )
                            Text(
                                text = session.calories,
                                fontFamily = MonoFont,
                                fontSize = 11.sp,
                                color = AmberGold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TelemetryMetricBox(
    label: String,
    value: String,
    unit: String,
    footer: String,
    valueColor: Color,
    footerColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CmfSurface)
            .border(1.dp, CmfBorder, RoundedCornerShape(14.dp))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            fontFamily = MonoFont,
            fontSize = 10.sp,
            color = CmfMuted
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = value,
                fontFamily = DotMatrixFont,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = valueColor
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = unit,
                fontFamily = MonoFont,
                fontSize = 11.sp,
                color = CmfMuted,
                modifier = Modifier.padding(bottom = 3.dp)
            )
        }
        Text(
            text = footer,
            fontFamily = MonoFont,
            fontSize = 10.sp,
            color = footerColor
        )
    }
}

private fun formatWorkoutDuration(sec: Int): String {
    val h = sec / 3600
    val m = (sec % 3600) / 60
    val s = sec % 60
    return String.format(Locale.US, "%02d:%02d:%02d", h, m, s)
}
