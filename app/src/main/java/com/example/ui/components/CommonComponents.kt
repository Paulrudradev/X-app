package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.model.DiscoveredBleDevice
import com.example.model.ToastMessage
import com.example.model.WristbandTab
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CmfBorder
import com.example.ui.theme.CmfCard
import com.example.ui.theme.CmfDark
import com.example.ui.theme.CmfMuted
import com.example.ui.theme.CmfOrange
import com.example.ui.theme.CmfSurface
import com.example.ui.theme.CmfTextPrimary
import com.example.ui.theme.CmfTextSecondary
import com.example.ui.theme.DotMatrixFont
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MonoFont
import com.example.ui.theme.RosePink
import com.example.ui.theme.RoseTint

@Composable
fun DotMatrixBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CmfDark)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacing = 16.dp.toPx()
            val dotRadius = 1.1.dp.toPx()
            val dotColor = Color.White.copy(alpha = 0.075f)
            var x = spacing / 2f
            while (x < size.width) {
                var y = spacing / 2f
                while (y < size.height) {
                    drawCircle(
                        color = dotColor,
                        radius = dotRadius,
                        center = Offset(x, y)
                    )
                    y += spacing
                }
                x += spacing
            }
        }
        content()
    }
}

@Composable
fun SpringButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = CmfSurface,
    borderColor: Color = CmfBorder,
    shape: RoundedCornerShape = RoundedCornerShape(12.dp),
    testTag: String? = null,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.96f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow
        ),
        label = "springButtonScale"
    )

    val finalModifier = modifier
        .scale(scale)
        .let { if (testTag != null) it.testTag(testTag) else it }
        .clip(shape)
        .background(if (enabled) backgroundColor else backgroundColor.copy(alpha = 0.45f))
        .border(1.dp, if (enabled) borderColor else borderColor.copy(alpha = 0.4f), shape)
        .clickable(
            interactionSource = interactionSource,
            indication = null,
            enabled = enabled,
            onClick = onClick
        )

    Box(
        modifier = finalModifier,
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NothingXTopHeader(
    connectionLabel: String,
    batteryPct: Int,
    audioEnabled: Boolean,
    selectedTab: WristbandTab,
    onToggleAudio: () -> Unit,
    onPairBleClick: () -> Unit,
    onOpenDiagnosticModal: () -> Unit,
    onSelectTab: (WristbandTab) -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "connPulse")
    val dotScale by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.25f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "connDotScale"
    )
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "connDotAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CmfDark.copy(alpha = 0.94f))
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Brand Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(CmfOrange),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "NX",
                        fontFamily = DotMatrixFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "NOTHING X ",
                            fontFamily = DotMatrixFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CmfTextPrimary
                        )
                        Text(
                            text = "// WRISTBAND OS",
                            fontFamily = DotMatrixFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = CmfOrange
                        )
                    }
                    Text(
                        text = "CMF Watch Pro 2 (D398) • FW 1.0.1.77 • Screenless Mode",
                        fontFamily = MonoFont,
                        fontSize = 10.sp,
                        color = CmfMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Status & Action Pills Row
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Live Connection Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(CmfCard)
                    .border(1.dp, CmfBorder, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .scale(dotScale)
                        .clip(CircleShape)
                        .background(EmeraldGreen.copy(alpha = dotAlpha))
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = connectionLabel,
                    fontFamily = MonoFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    color = EmeraldGreen
                )
            }

            // Battery Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(CmfCard)
                    .border(1.dp, CmfBorder, RoundedCornerShape(50))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = "🔋 $batteryPct%",
                    fontFamily = MonoFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    color = CmfTextPrimary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "(~9d)",
                    fontFamily = MonoFont,
                    fontSize = 10.sp,
                    color = CmfMuted
                )
            }

            // Haptic Audio Toggle
            SpringButton(
                onClick = onToggleAudio,
                backgroundColor = CmfCard,
                borderColor = if (audioEnabled) CmfOrange.copy(alpha = 0.5f) else CmfBorder,
                shape = RoundedCornerShape(50),
                testTag = "audio_toggle_btn"
            ) {
                Text(
                    text = if (audioEnabled) "🔊 Haptic Audio: ON" else "🔇 Audio: OFF",
                    fontFamily = MonoFont,
                    fontSize = 10.sp,
                    color = if (audioEnabled) CmfTextPrimary else CmfMuted,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }

            // Pair Real BLE Button
            SpringButton(
                onClick = onPairBleClick,
                backgroundColor = CmfOrange,
                borderColor = CmfOrange,
                shape = RoundedCornerShape(50),
                testTag = "pair_ble_btn"
            ) {
                Text(
                    text = "⚡ Pair Real BLE",
                    fontFamily = MonoFont,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(horizontal = 11.dp, vertical = 5.dp)
                )
            }

            // Fix BLE Connection Button
            SpringButton(
                onClick = onOpenDiagnosticModal,
                backgroundColor = RoseTint,
                borderColor = RosePink.copy(alpha = 0.5f),
                shape = RoundedCornerShape(50),
                testTag = "open_ble_fix_btn"
            ) {
                Text(
                    text = "🔧 Fix BLE & UUIDs",
                    fontFamily = MonoFont,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    color = RosePink,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        HorizontalDivider(color = CmfBorder.copy(alpha = 0.6f), thickness = 1.dp)
        Spacer(modifier = Modifier.height(8.dp))

        // Scrollable 5-Tab Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            WristbandTab.entries.forEach { tab ->
                val isSelected = selectedTab == tab
                val bg by animateColorAsState(
                    targetValue = if (isSelected) CmfOrange else CmfSurface.copy(alpha = 0.55f),
                    animationSpec = tween(200),
                    label = "tabBg"
                )
                val fg by animateColorAsState(
                    targetValue = if (isSelected) Color.Black else CmfMuted,
                    animationSpec = tween(200),
                    label = "tabFg"
                )
                SpringButton(
                    onClick = { onSelectTab(tab) },
                    backgroundColor = bg,
                    borderColor = if (isSelected) CmfOrange else CmfBorder,
                    shape = RoundedCornerShape(9.dp),
                    testTag = "tab_${tab.name.lowercase()}"
                ) {
                    Text(
                        text = "${tab.code} // ${tab.label}",
                        fontFamily = DotMatrixFont,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp,
                        color = fg,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BleTroubleshootBanner(
    isBluetoothEnabled: Boolean,
    onScanBleClick: () -> Unit,
    onOpenProtocolModal: () -> Unit,
    onDismiss: () -> Unit
) {
    Surface(
        color = CmfCard,
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, CmfOrange.copy(alpha = 0.75f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(CmfOrange)
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "NATIVE ANDROID BLE BRIDGE READY",
                            fontFamily = DotMatrixFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            color = Color.Black
                        )
                    }
                    Text(
                        text = if (isBluetoothEnabled) "✅ Bluetooth Adapter Active" else "⚡ D398 Hardware Simulator Ready",
                        fontFamily = MonoFont,
                        fontSize = 10.sp,
                        color = EmeraldGreen
                    )
                }
                SpringButton(
                    onClick = onDismiss,
                    backgroundColor = CmfSurface,
                    borderColor = CmfBorder,
                    shape = RoundedCornerShape(8.dp),
                    testTag = "dismiss_banner_btn"
                ) {
                    Text(
                        text = "✕",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = CmfMuted,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "1. Native Android GATT Unlocked: Unlike browser iframes, this native Android app accesses BluetoothGatt directly for service 0xFFF0 and Shell 77d4e67c.\n" +
                    "2. Nothing X Single-Session Lock: Your CMF Watch Pro 2 (Actions ATS3089C) allows only one active BLE client at a time. Force-stop the official Nothing X app in Android Settings before scanning.",
                fontFamily = MonoFont,
                fontSize = 11.sp,
                lineHeight = 16.sp,
                color = CmfTextSecondary
            )

            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SpringButton(
                    onClick = onScanBleClick,
                    backgroundColor = CmfOrange,
                    borderColor = CmfOrange,
                    shape = RoundedCornerShape(10.dp),
                    testTag = "banner_scan_ble_btn"
                ) {
                    Text(
                        text = "📡 Scan & Pair D398 Watch",
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.Black,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
                SpringButton(
                    onClick = onOpenProtocolModal,
                    backgroundColor = CmfSurface,
                    borderColor = CmfBorder,
                    shape = RoundedCornerShape(10.dp),
                    testTag = "banner_protocol_btn"
                ) {
                    Text(
                        text = "🔑 D398 GATT UUIDs & AT Handshake",
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = CmfTextPrimary,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BleDiagnosticModal(
    isBluetoothEnabled: Boolean,
    isLiveHardware: Boolean,
    isScanning: Boolean,
    discoveredDevices: List<DiscoveredBleDevice>,
    onStartScan: () -> Unit,
    onConnectDevice: (DiscoveredBleDevice) -> Unit,
    onSimulateHandshake: () -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            color = CmfCard,
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(2.dp, CmfOrange),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HARDWARE BLE DIAGNOSTICS (MODEL D398)",
                            fontFamily = MonoFont,
                            fontSize = 10.sp,
                            color = CmfOrange
                        )
                        Text(
                            text = "CMF Watch Pro 2 GATT Scanner & Protocol",
                            fontFamily = DotMatrixFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CmfTextPrimary
                        )
                    }
                    SpringButton(
                        onClick = onDismiss,
                        backgroundColor = CmfSurface,
                        borderColor = CmfBorder,
                        shape = RoundedCornerShape(8.dp),
                        testTag = "close_ble_modal_btn"
                    ) {
                        Text(
                            text = "✕ Close",
                            fontFamily = MonoFont,
                            fontSize = 11.sp,
                            color = CmfTextSecondary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                HorizontalDivider(color = CmfBorder)

                // Live Environment Readout
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .border(1.dp, CmfBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "• Native Android BluetoothGatt: ✅ Unlocked (No Browser Sandbox)",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = EmeraldGreen
                    )
                    Text(
                        text = "• Adapter Status: ${if (isBluetoothEnabled) "✅ Bluetooth LE Enabled" else "⚠️ Virtual D398 Bridge Mode"}",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = if (isBluetoothEnabled) EmeraldGreen else AmberGold
                    )
                    Text(
                        text = "• Active GATT Channel (0xFFF0): ${if (isLiveHardware) "✅ Bound to Real Hardware" else "Simulated D398 Bridge Active"}",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = CmfTextPrimary
                    )
                }

                // Discovered Peripherals List
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CmfSurface)
                        .border(1.dp, CmfBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isScanning) "📡 SCANNING NEARBY BLE..." else "DISCOVERED D398 / BLE DEVICES",
                            fontFamily = MonoFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = CmfOrange
                        )
                        SpringButton(
                            onClick = onStartScan,
                            backgroundColor = CmfOrange,
                            borderColor = CmfOrange,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = if (isScanning) "Scanning..." else "🔄 Scan Now",
                                fontFamily = MonoFont,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    if (discoveredDevices.isEmpty()) {
                        Text(
                            text = "Tap 'Scan Now' to enumerate CMF Watch Pro 2 (D398) and nearby BLE peripherals.",
                            fontFamily = MonoFont,
                            fontSize = 11.sp,
                            color = CmfMuted
                        )
                    } else {
                        discoveredDevices.take(5).forEach { dev ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .border(
                                        1.dp,
                                        if (dev.isCmfCandidate) CmfOrange.copy(alpha = 0.6f) else CmfBorder,
                                        RoundedCornerShape(10.dp)
                                    )
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = dev.name,
                                        fontFamily = MonoFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = CmfTextPrimary
                                    )
                                    Text(
                                        text = "${dev.address} • RSSI ${dev.rssi} dBm",
                                        fontFamily = MonoFont,
                                        fontSize = 10.sp,
                                        color = CmfMuted
                                    )
                                }
                                SpringButton(
                                    onClick = {
                                        onConnectDevice(dev)
                                        onDismiss()
                                    },
                                    backgroundColor = CmfOrange,
                                    borderColor = CmfOrange,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = "Connect",
                                        fontFamily = MonoFont,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Verified GATT UUID Spec Box
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(CmfSurface)
                        .border(1.dp, CmfBorder, RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "VERIFIED CMF WATCH PRO 2 (D398) GATT UUIDS",
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = EmeraldGreen
                    )
                    Text(
                        text = "• Command Service: 0000fff0-0000-1000-8000-00805f9b34fb (Write: 0xfff2 | Notify: 0xfff1)\n" +
                            "• Shell / Auth Service: 77d4e67c-2fe2-2334-0d35-9ccd078f529c (AT GETSECRET → 77d4ff01 / 77d4ff02)\n" +
                            "• Bulk Data Service: 02f00000-0000-0000-0000-00000000ffe0 (Write: ffe1 | Notify: ffe2)\n" +
                            "• Standard Services: 0x180f (Battery), 0x180a (Device Info)",
                        fontFamily = MonoFont,
                        fontSize = 10.sp,
                        lineHeight = 15.sp,
                        color = CmfTextSecondary
                    )
                }

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SpringButton(
                        onClick = {
                            onSimulateHandshake()
                            onDismiss()
                        },
                        backgroundColor = CmfSurface,
                        borderColor = EmeraldGreen.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f),
                        testTag = "modal_handshake_btn"
                    ) {
                        Text(
                            text = "🔑 Run AT GETSECRET Handshake",
                            fontFamily = MonoFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = EmeraldGreen,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp)
                        )
                    }
                    SpringButton(
                        onClick = onStartScan,
                        backgroundColor = CmfOrange,
                        borderColor = CmfOrange,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "📡 Rescan BLE",
                            fontFamily = MonoFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = Color.Black,
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NothingXToastOverlay(
    toastMessage: ToastMessage?,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = toastMessage != null,
        enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
        modifier = modifier
    ) {
        if (toastMessage != null) {
            Surface(
                color = CmfCard,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(2.dp, CmfOrange),
                shadowElevation = 12.dp,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "⚡",
                        fontSize = 18.sp
                    )
                    Column {
                        Text(
                            text = toastMessage.title.uppercase(),
                            fontFamily = DotMatrixFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = CmfOrange
                        )
                        Text(
                            text = toastMessage.message,
                            fontFamily = MonoFont,
                            fontSize = 11.sp,
                            color = CmfTextPrimary
                        )
                    }
                }
            }
        }
    }
}
