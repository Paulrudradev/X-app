package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BlePacketLog
import com.example.model.BlindQuadrant
import com.example.ui.components.SpringButton
import com.example.ui.theme.CmfBorder
import com.example.ui.theme.CmfCard
import com.example.ui.theme.CmfMuted
import com.example.ui.theme.CmfOrange
import com.example.ui.theme.CmfSurface
import com.example.ui.theme.CmfTextPrimary
import com.example.ui.theme.CmfTextSecondary
import com.example.ui.theme.DotMatrixFont
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MonoFont

@Composable
fun BlindMapConsoleScreen(
    blindQuadrants: List<BlindQuadrant>,
    selectedBlindReadout: String,
    blePacketLogs: List<BlePacketLog>,
    onExecuteBlindQuadrant: (BlindQuadrant) -> Unit,
    onSendRawHex: (String) -> Unit,
    onClearBleLogs: () -> Unit,
    onShowToast: (String, String) -> Unit
) {
    val context = LocalContext.current
    var rawHexInput by remember { mutableStateOf("AA 01 04 00 12 01 00 B8") }

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Interactive 1.32" Bezel Coordinate Map (Blind Touch Digitizer Helper)
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
                        text = "1.32\" BEZEL COORDINATE MAP",
                        fontFamily = MonoFont,
                        fontSize = 10.sp,
                        color = CmfOrange
                    )
                    Text(
                        text = "Blind Touch Digitizer Helper",
                        fontFamily = DotMatrixFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = CmfTextPrimary
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "If your OLED display is blacked out but the touch digitizer still responds, press the Crown button once to return to the Watchface, swipe down from the top edge, and tap these exact quadrants:",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = CmfMuted
                    )
                }

                // Circular Bezel with 2x2 Quadrant Grid
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .size(254.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF18181B))
                            .border(4.dp, Color(0xFF3F3F46), CircleShape)
                            .padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "↓ SWIPE DOWN FROM TOP EDGE ↓",
                            fontFamily = MonoFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = CmfOrange,
                            textAlign = TextAlign.Center
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            blindQuadrants.chunked(2).forEach { rowQuads ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowQuads.forEach { quad ->
                                        SpringButton(
                                            onClick = { onExecuteBlindQuadrant(quad) },
                                            backgroundColor = CmfSurface,
                                            borderColor = CmfBorder,
                                            shape = RoundedCornerShape(12.dp),
                                            testTag = "blind_${quad.id}",
                                            modifier = Modifier.weight(1f)
                                        ) {
                                            Column(
                                                modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally
                                            ) {
                                                Text(text = quad.emoji, fontSize = 15.sp)
                                                Text(
                                                    text = quad.positionLabel,
                                                    fontFamily = MonoFont,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 10.sp,
                                                    color = Color.White
                                                )
                                                Text(
                                                    text = quad.actionTitle,
                                                    fontFamily = MonoFont,
                                                    fontSize = 9.sp,
                                                    color = CmfMuted
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        Text(
                            text = "Crown Press = Always Return Home",
                            fontFamily = MonoFont,
                            fontSize = 9.sp,
                            color = CmfMuted,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Quadrant Readout Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(1.dp, CmfBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = selectedBlindReadout,
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = CmfOrange
                    )
                }
            }
        }

        // 2. Live BLE GATT Packet Inspector Console
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
                            text = "GATT PROTOCOL INSPECTOR",
                            fontFamily = MonoFont,
                            fontSize = 10.sp,
                            color = EmeraldGreen
                        )
                        Text(
                            text = "Live BLE Command & Frame Log",
                            fontFamily = DotMatrixFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CmfTextPrimary
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SpringButton(
                            onClick = {
                                val text = blePacketLogs.joinToString("\n") {
                                    "[${it.timestamp}] ${it.direction} ${it.hex} // ${it.description}"
                                }
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                clipboard?.setPrimaryClip(ClipData.newPlainText("BLE GATT Log", text))
                                onShowToast("BLE Log Copied", "Copied GATT packet log to clipboard.")
                            },
                            backgroundColor = CmfSurface,
                            borderColor = CmfBorder,
                            shape = RoundedCornerShape(8.dp),
                            testTag = "copy_ble_log_btn"
                        ) {
                            Text(
                                text = "Copy",
                                fontFamily = MonoFont,
                                fontSize = 11.sp,
                                color = CmfTextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }

                        SpringButton(
                            onClick = onClearBleLogs,
                            backgroundColor = CmfSurface,
                            borderColor = CmfBorder,
                            shape = RoundedCornerShape(8.dp),
                            testTag = "clear_ble_log_btn"
                        ) {
                            Text(
                                text = "Clear",
                                fontFamily = MonoFont,
                                fontSize = 11.sp,
                                color = CmfTextPrimary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                // Scrollable Console Output Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .border(1.dp, CmfBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (blePacketLogs.isEmpty()) {
                        Text(
                            text = "// GATT Console cleared. Trigger any watch action or send a raw hex frame below.",
                            fontFamily = MonoFont,
                            fontSize = 11.sp,
                            color = CmfMuted
                        )
                    } else {
                        blePacketLogs.forEach { entry ->
                            val annotated = buildAnnotatedString {
                                withStyle(SpanStyle(color = Color(0xFF71717A))) {
                                    append("[${entry.timestamp}] ")
                                }
                                withStyle(
                                    SpanStyle(
                                        color = if (entry.direction == "TX") CmfOrange else EmeraldGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append("${entry.direction} ")
                                }
                                withStyle(SpanStyle(color = Color(0xFFE4E4E7))) {
                                    append("${entry.hex} ")
                                }
                                withStyle(SpanStyle(color = CmfMuted)) {
                                    append("// ${entry.description}")
                                }
                            }
                            Text(
                                text = annotated,
                                fontFamily = MonoFont,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Custom Hex Injector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = rawHexInput,
                        onValueChange = { rawHexInput = it },
                        placeholder = {
                            Text(
                                "Send raw CMF BLE frame (hex)...",
                                fontFamily = MonoFont,
                                fontSize = 11.sp,
                                color = CmfMuted
                            )
                        },
                        singleLine = true,
                        textStyle = TextStyle(
                            fontFamily = MonoFont,
                            fontSize = 12.sp,
                            color = EmeraldGreen
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = CmfOrange,
                            unfocusedBorderColor = CmfBorder,
                            focusedContainerColor = Color.Black,
                            unfocusedContainerColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("custom_hex_input")
                    )

                    SpringButton(
                        onClick = { onSendRawHex(rawHexInput) },
                        backgroundColor = CmfOrange,
                        borderColor = CmfOrange,
                        shape = RoundedCornerShape(10.dp),
                        testTag = "send_raw_hex_btn"
                    ) {
                        Text(
                            text = "Send TX",
                            fontFamily = MonoFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp)
                        )
                    }
                }
            }
        }
    }
}
