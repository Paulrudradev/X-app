package com.example.ui.screens

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import com.example.ui.theme.IndigoDeep
import com.example.ui.theme.MonoFont
import com.example.ui.theme.PurpleSleep
import com.example.ui.theme.RosePink
import com.example.ui.theme.SkyBlue
import kotlin.math.roundToInt

@Composable
fun VitalsScreen(
    steps: Int,
    stepsGoal: Int,
    activeCals: Int,
    calsGoal: Int,
    chartHours: List<String>,
    hrChartSeries: List<Int>,
    spo2ChartSeries: List<Int>,
    stressChartSeries: List<Int>,
    onUpdateStepsGoal: (Int) -> Unit,
    onUpdateCalsGoal: (Int) -> Unit,
    onRefreshVitalsBuffer: () -> Unit
) {
    val stepsPct = ((steps.toFloat() / stepsGoal.coerceAtLeast(1)) * 100f).roundToInt().coerceIn(0, 100)
    val calsPct = ((activeCals.toFloat() / calsGoal.coerceAtLeast(1)) * 100f).roundToInt().coerceIn(0, 100)

    val animatedStepsProgress by animateFloatAsState(
        targetValue = (stepsPct / 100f).coerceIn(0f, 1f),
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "stepsProgress"
    )
    val animatedCalsProgress by animateFloatAsState(
        targetValue = (calsPct / 100f).coerceIn(0f, 1f),
        animationSpec = tween(400, easing = FastOutSlowInEasing),
        label = "calsProgress"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Daily Steps Goal Card
        Surface(
            color = CmfCard,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, CmfBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "DAILY STEPS",
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = SkyBlue
                    )
                    Text(
                        text = "$stepsPct%",
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = String.format("%,d", steps),
                        fontFamily = DotMatrixFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Goal: ${String.format("%,d", stepsGoal)}",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = CmfMuted
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
                            .fillMaxWidth(animatedStepsProgress)
                            .clip(RoundedCornerShape(50))
                            .background(SkyBlue)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Target:",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = CmfMuted
                    )
                    Slider(
                        value = stepsGoal.toFloat(),
                        onValueChange = { onUpdateStepsGoal((it / 1000f).roundToInt() * 1000) },
                        valueRange = 4000f..20000f,
                        steps = 15,
                        colors = SliderDefaults.colors(
                            thumbColor = CmfOrange,
                            activeTrackColor = CmfOrange,
                            inactiveTrackColor = CmfSurface
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 2. Active Calories Goal Card
        Surface(
            color = CmfCard,
            shape = RoundedCornerShape(18.dp),
            border = BorderStroke(1.dp, CmfBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "ACTIVE CALORIES",
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = CmfOrange
                    )
                    Text(
                        text = "$calsPct%",
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "$activeCals",
                        fontFamily = DotMatrixFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color.White
                    )
                    Text(
                        text = "Goal: $calsGoal kcal",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = CmfMuted
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
                            .fillMaxWidth(animatedCalsProgress)
                            .clip(RoundedCornerShape(50))
                            .background(CmfOrange)
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Target:",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
                        color = CmfMuted
                    )
                    Slider(
                        value = calsGoal.toFloat(),
                        onValueChange = { onUpdateCalsGoal((it / 50f).roundToInt() * 50) },
                        valueRange = 200f..1200f,
                        steps = 19,
                        colors = SliderDefaults.colors(
                            thumbColor = CmfOrange,
                            activeTrackColor = CmfOrange,
                            inactiveTrackColor = CmfSurface
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // 3. Last Night Sleep Card
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
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "LAST NIGHT SLEEP",
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = PurpleSleep
                    )
                    Text(
                        text = "Score: 86",
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = EmeraldGreen
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "7h 24m",
                        fontFamily = DotMatrixFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color.White
                    )
                    Text(
                        text = "23:18 → 06:42",
                        fontFamily = MonoFont,
                        fontSize = 11.sp,
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
                    Box(modifier = Modifier.weight(0.28f).fillMaxHeight().background(IndigoDeep))
                    Box(modifier = Modifier.weight(0.52f).fillMaxHeight().background(PurpleSleep))
                    Box(modifier = Modifier.weight(0.16f).fillMaxHeight().background(SkyBlue))
                    Box(modifier = Modifier.weight(0.04f).fillMaxHeight().background(AmberGold))
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Deep 2h05m", fontFamily = MonoFont, fontSize = 10.sp, color = CmfMuted)
                    Text("Light 3h50m", fontFamily = MonoFont, fontSize = 10.sp, color = CmfMuted)
                    Text("REM 1h12m", fontFamily = MonoFont, fontSize = 10.sp, color = CmfMuted)
                }
            }
        }

        // 4. 24-Hour Optical PPG Curve (Continuous Heart Rate Spline Chart)
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
                            text = "24-HOUR OPTICAL PPG CURVE",
                            fontFamily = MonoFont,
                            fontSize = 10.sp,
                            color = RosePink
                        )
                        Text(
                            text = "Continuous Heart Rate (BPM)",
                            fontFamily = DotMatrixFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CmfTextPrimary
                        )
                    }
                    SpringButton(
                        onClick = onRefreshVitalsBuffer,
                        backgroundColor = CmfSurface,
                        borderColor = CmfBorder,
                        shape = RoundedCornerShape(8.dp),
                        testTag = "refresh_vitals_btn"
                    ) {
                        Text(
                            text = "🔄 Sync Watch Buffer",
                            fontFamily = MonoFont,
                            fontSize = 10.sp,
                            color = CmfTextPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                HeartRateSplineChart(
                    hours = chartHours,
                    values = hrChartSeries
                )
            }
        }

        // 5. Blood Oxygen & HRV Stress Grouped Bar Chart
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
                    Column {
                        Text(
                            text = "BLOOD OXYGEN & HRV RECOVERY",
                            fontFamily = MonoFont,
                            fontSize = 10.sp,
                            color = SkyBlue
                        )
                        Text(
                            text = "SpO₂ (%) & Stress Index",
                            fontFamily = DotMatrixFont,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = CmfTextPrimary
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(SkyBlue))
                            Text("SpO₂", fontFamily = MonoFont, fontSize = 10.sp, color = CmfMuted)
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AmberGold))
                            Text("Stress", fontFamily = MonoFont, fontSize = 10.sp, color = CmfMuted)
                        }
                    }
                }

                Spo2StressBarChart(
                    hours = chartHours,
                    spo2Values = spo2ChartSeries,
                    stressValues = stressChartSeries
                )
            }
        }
    }
}

@Composable
private fun HeartRateSplineChart(
    hours: List<String>,
    values: List<Int>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
                .padding(horizontal = 6.dp, vertical = 8.dp)
        ) {
            if (values.size < 2) return@Canvas
            val minVal = 45f
            val maxVal = 135f
            val range = maxVal - minVal

            // Draw horizontal grid lines
            for (i in 0..3) {
                val y = size.height * (i / 3f)
                drawLine(
                    color = Color.White.copy(alpha = 0.06f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            val stepX = size.width / (values.size - 1).coerceAtLeast(1)
            val points = values.mapIndexed { idx, v ->
                val normY = ((v - minVal) / range).coerceIn(0f, 1f)
                Offset(idx * stepX, size.height * (1f - normY))
            }

            val linePath = Path().apply {
                moveTo(points.first().x, points.first().y)
                for (i in 0 until points.size - 1) {
                    val p0 = points[i]
                    val p1 = points[i + 1]
                    val cx = (p0.x + p1.x) / 2f
                    cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                }
            }

            val fillPath = Path().apply {
                addPath(linePath)
                lineTo(points.last().x, size.height)
                lineTo(points.first().x, size.height)
                close()
            }

            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        CmfOrange.copy(alpha = 0.28f),
                        CmfOrange.copy(alpha = 0.02f)
                    )
                )
            )

            drawPath(
                path = linePath,
                color = CmfOrange,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )

            points.forEach { pt ->
                drawCircle(
                    color = Color.Black,
                    radius = 5.dp.toPx(),
                    center = pt
                )
                drawCircle(
                    color = CmfOrange,
                    radius = 3.5.dp.toPx(),
                    center = pt
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            hours.forEachIndexed { idx, hrLabel ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${values.getOrElse(idx) { 72 }}",
                        fontFamily = MonoFont,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = CmfOrange
                    )
                    Text(
                        text = hrLabel,
                        fontFamily = MonoFont,
                        fontSize = 9.sp,
                        color = CmfMuted
                    )
                }
            }
        }
    }
}

@Composable
private fun Spo2StressBarChart(
    hours: List<String>,
    spo2Values: List<Int>,
    stressValues: List<Int>
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .padding(horizontal = 6.dp, vertical = 8.dp)
        ) {
            val count = hours.size.coerceAtLeast(1)
            val groupWidth = size.width / count
            val barWidth = (groupWidth * 0.28f).coerceAtLeast(6.dp.toPx())

            for (i in 0..3) {
                val y = size.height * (i / 3f)
                drawLine(
                    color = Color.White.copy(alpha = 0.06f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            for (i in 0 until count) {
                val centerX = i * groupWidth + groupWidth / 2f
                val spo2Pct = (spo2Values.getOrElse(i) { 98 } / 100f).coerceIn(0.05f, 1f)
                val stressPct = (stressValues.getOrElse(i) { 30 } / 100f).coerceIn(0.05f, 1f)

                val spo2Height = size.height * spo2Pct
                val stressHeight = size.height * stressPct

                drawRoundRect(
                    color = SkyBlue,
                    topLeft = Offset(centerX - barWidth - 2.dp.toPx(), size.height - spo2Height),
                    size = Size(barWidth, spo2Height),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )

                drawRoundRect(
                    color = AmberGold,
                    topLeft = Offset(centerX + 2.dp.toPx(), size.height - stressHeight),
                    size = Size(barWidth, stressHeight),
                    cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            hours.forEachIndexed { idx, hrLabel ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${spo2Values.getOrElse(idx) { 98 }}%",
                        fontFamily = MonoFont,
                        fontSize = 9.sp,
                        color = SkyBlue
                    )
                    Text(
                        text = hrLabel,
                        fontFamily = MonoFont,
                        fontSize = 9.sp,
                        color = CmfMuted
                    )
                }
            }
        }
    }
}
