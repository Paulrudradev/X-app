package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WristAlarm
import com.example.ui.components.SpringButton
import com.example.ui.theme.CmfBorder
import com.example.ui.theme.CmfCard
import com.example.ui.theme.CmfMuted
import com.example.ui.theme.CmfOrange
import com.example.ui.theme.CmfSurface
import com.example.ui.theme.CmfTextPrimary
import com.example.ui.theme.DotMatrixFont
import com.example.ui.theme.MonoFont
import com.example.ui.theme.RosePink
import com.example.ui.theme.RoseTint
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AlarmsScreen(
    alarms: List<WristAlarm>,
    wristTimerRemainSec: Int,
    wristTimerRunning: Boolean,
    sedentaryReminder: Boolean,
    hydrationAlert: Boolean,
    highRestingHrThreshold: Int,
    onAddAlarm: (time: String, days: String) -> Unit,
    onToggleAlarm: (Long) -> Unit,
    onDeleteAlarm: (Long) -> Unit,
    onSelectTimerPreset: (Int) -> Unit,
    onToggleWristTimer: () -> Unit,
    onResetWristTimer: () -> Unit,
    onSetSedentaryReminder: (Boolean) -> Unit,
    onSetHydrationAlert: (Boolean) -> Unit,
    onSetHighRestingHrThreshold: (Int) -> Unit
) {
    var newAlarmTime by remember { mutableStateOf("07:30") }
    var newAlarmDays by remember { mutableStateOf("Daily") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Watch Vibration Alarms Manager
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
                        text = "SILENT WRISTBAND WAKE",
                        fontFamily = MonoFont,
                        fontSize = 10.sp,
                        color = CmfOrange
                    )
                    Text(
                        text = "Watch Vibration Alarms",
                        fontFamily = DotMatrixFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CmfTextPrimary
                    )
                    Text(
                        text = "Press the physical watch Crown button once to stop an active wrist alarm.",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = CmfMuted
                    )
                }

                // Add Alarm Row
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CmfSurface)
                        .border(1.dp, CmfBorder, RoundedCornerShape(14.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = newAlarmTime,
                            onValueChange = { newAlarmTime = it },
                            label = { Text("Time (HH:MM)", fontFamily = MonoFont, fontSize = 10.sp) },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = MonoFont,
                                fontSize = 13.sp,
                                color = Color.White
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CmfOrange,
                                unfocusedBorderColor = CmfBorder,
                                focusedContainerColor = Color.Black,
                                unfocusedContainerColor = Color.Black
                            ),
                            modifier = Modifier
                                .weight(0.95f)
                                .testTag("alarm_time_input")
                        )

                        OutlinedTextField(
                            value = newAlarmDays,
                            onValueChange = { newAlarmDays = it },
                            label = { Text("Repeat Days", fontFamily = MonoFont, fontSize = 10.sp) },
                            singleLine = true,
                            textStyle = TextStyle(
                                fontFamily = MonoFont,
                                fontSize = 13.sp,
                                color = Color.White
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = CmfOrange,
                                unfocusedBorderColor = CmfBorder,
                                focusedContainerColor = Color.Black,
                                unfocusedContainerColor = Color.Black
                            ),
                            modifier = Modifier
                                .weight(1.25f)
                                .testTag("alarm_days_input")
                        )
                    }

                    SpringButton(
                        onClick = { onAddAlarm(newAlarmTime, newAlarmDays) },
                        backgroundColor = CmfOrange,
                        borderColor = CmfOrange,
                        shape = RoundedCornerShape(10.dp),
                        testTag = "add_alarm_btn",
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "+ Add Vibration Alarm to Watch",
                            fontFamily = MonoFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 10.dp)
                        )
                    }
                }

                // Alarms List
                alarms.forEach { alarm ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(CmfSurface)
                            .border(1.dp, CmfBorder, RoundedCornerShape(14.dp))
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = alarm.time,
                                fontFamily = DotMatrixFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = if (alarm.enabled) Color.White else CmfMuted,
                                textDecoration = if (alarm.enabled) TextDecoration.None else TextDecoration.LineThrough
                            )
                            Text(
                                text = "${alarm.days} • Wrist Haptic",
                                fontFamily = MonoFont,
                                fontSize = 11.sp,
                                color = CmfMuted
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            SpringButton(
                                onClick = { onToggleAlarm(alarm.id) },
                                backgroundColor = if (alarm.enabled) CmfOrange else Color(0xFF27272A),
                                borderColor = if (alarm.enabled) CmfOrange else CmfBorder,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = if (alarm.enabled) "ON" else "OFF",
                                    fontFamily = MonoFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = if (alarm.enabled) Color.Black else CmfMuted,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }

                            SpringButton(
                                onClick = { onDeleteAlarm(alarm.id) },
                                backgroundColor = RoseTint,
                                borderColor = RosePink.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "✕",
                                    fontFamily = MonoFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = RosePink,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // 2. Remote Wrist Countdown Timer
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HAPTIC WRIST COUNTDOWN",
                            fontFamily = MonoFont,
                            fontSize = 10.sp,
                            color = CmfOrange
                        )
                        Text(
                            text = "Remote Timer (Vibrates at 00:00)",
                            fontFamily = DotMatrixFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CmfTextPrimary
                        )
                    }
                    val mm = wristTimerRemainSec / 60
                    val ss = wristTimerRemainSec % 60
                    Text(
                        text = String.format(Locale.US, "%02d:%02d", mm, ss),
                        fontFamily = DotMatrixFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 26.sp,
                        color = if (wristTimerRunning) CmfOrange else Color.White
                    )
                }

                // Preset Timer Chips
                val presets = listOf(
                    60 to "1 Min",
                    180 to "3 Min",
                    300 to "5 Min",
                    600 to "10 Min",
                    1500 to "25m Pomodoro"
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    presets.forEach { (sec, label) ->
                        SpringButton(
                            onClick = { onSelectTimerPreset(sec) },
                            backgroundColor = CmfSurface,
                            borderColor = CmfBorder,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = label,
                                fontFamily = MonoFont,
                                fontSize = 11.sp,
                                color = CmfTextPrimary,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SpringButton(
                        onClick = onToggleWristTimer,
                        backgroundColor = CmfOrange,
                        borderColor = CmfOrange,
                        shape = RoundedCornerShape(12.dp),
                        testTag = "start_wrist_timer_btn",
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = if (wristTimerRunning) "⏸ PAUSE WRIST TIMER" else "▶ START WRIST TIMER",
                            fontFamily = DotMatrixFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 11.dp)
                        )
                    }

                    SpringButton(
                        onClick = onResetWristTimer,
                        backgroundColor = CmfSurface,
                        borderColor = CmfBorder,
                        shape = RoundedCornerShape(12.dp),
                        testTag = "reset_wrist_timer_btn"
                    ) {
                        Text(
                            text = "Reset",
                            fontFamily = MonoFont,
                            fontSize = 12.sp,
                            color = CmfTextPrimary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 11.dp)
                        )
                    }
                }
            }
        }

        // 3. Wristband Health & Safety Alerts
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
                Text(
                    text = "WRISTBAND HEALTH & SAFETY ALERTS",
                    fontFamily = DotMatrixFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = CmfTextPrimary
                )

                // Sedentary Alert
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
                        Text(
                            text = "Sedentary Stand-Up Reminder",
                            fontFamily = MonoFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = CmfTextPrimary
                        )
                        Text(
                            text = "Vibrates every 60 mins if inactive (09:00–18:00)",
                            fontFamily = MonoFont,
                            fontSize = 10.sp,
                            color = CmfMuted
                        )
                    }
                    Checkbox(
                        checked = sedentaryReminder,
                        onCheckedChange = onSetSedentaryReminder,
                        colors = CheckboxDefaults.colors(
                            checkedColor = CmfOrange,
                            checkmarkColor = Color.Black
                        )
                    )
                }

                // Hydration Alert
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
                        Text(
                            text = "Hydration Water Alert",
                            fontFamily = MonoFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = CmfTextPrimary
                        )
                        Text(
                            text = "Double wrist pulse every 90 mins",
                            fontFamily = MonoFont,
                            fontSize = 10.sp,
                            color = CmfMuted
                        )
                    }
                    Checkbox(
                        checked = hydrationAlert,
                        onCheckedChange = onSetHydrationAlert,
                        colors = CheckboxDefaults.colors(
                            checkedColor = CmfOrange,
                            checkmarkColor = Color.Black
                        )
                    )
                }

                // High Resting HR Threshold
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CmfSurface)
                        .border(1.dp, CmfBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column {
                        Text(
                            text = "High Resting HR Alert Threshold",
                            fontFamily = MonoFont,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = CmfTextPrimary
                        )
                        Text(
                            text = "Immediate wrist alert if resting BPM exceeds limit",
                            fontFamily = MonoFont,
                            fontSize = 10.sp,
                            color = CmfMuted
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf(100, 110, 120, 130).forEach { bpm ->
                            val isSelected = highRestingHrThreshold == bpm
                            SpringButton(
                                onClick = { onSetHighRestingHrThreshold(bpm) },
                                backgroundColor = if (isSelected) CmfOrange else Color.Black,
                                borderColor = if (isSelected) CmfOrange else CmfBorder,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "$bpm BPM",
                                    fontFamily = MonoFont,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.Black else CmfOrange,
                                    modifier = Modifier.padding(vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
