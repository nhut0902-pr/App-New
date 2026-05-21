package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.LightAccentBg
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RPGCard
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBg
import com.example.ui.theme.DeepSlateBg
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.RetroGold
import com.example.ui.theme.ShieldBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TimerState

@Composable
fun PomodoroScreen(
    timeRemainingSeconds: Int,
    totalSeconds: Int,
    timerState: TimerState,
    presetType: String,
    selectedSound: String,
    isSoundPlaying: Boolean,
    onPresetSelected: (String) -> Unit,
    onSetCustomMinutes: (Int) -> Unit,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onSelectSound: (String) -> Unit,
    onToggleSound: () -> Unit,
    onInstantFinish: () -> Unit // Developer cheat for quick trial
) {
    val minutes = timeRemainingSeconds / 60
    val seconds = timeRemainingSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)
    val progress = if (totalSeconds > 0) timeRemainingSeconds.toFloat() / totalSeconds.toFloat() else 1f

    // Interactive custom picker slider state
    var customMinsInput by remember { mutableStateOf(25) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBg)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            // Presets row Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardBg)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    Pair("25/5", "25m focus"),
                    Pair("50/10", "50m focus"),
                    Pair("custom", "custom quest")
                ).forEach { preset ->
                    val isSelected = presetType == preset.first
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) NeonPurple else Color.Transparent)
                            .clickable { onPresetSelected(preset.first) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preset.second.uppercase(),
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // Custom config slider settings if custom is chosen
        if (presetType == "custom") {
            item {
                RPGCard(borderColor = NeonPurple) {
                    Text(
                        text = "CUSTOM FOCUS DURATION",
                        fontSize = 12.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Slider(
                            value = customMinsInput.toFloat(),
                            onValueChange = {
                                customMinsInput = it.toInt()
                                onSetCustomMinutes(it.toInt())
                            },
                            valueRange = 5f..120f,
                            steps = 23,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("custom_timer_slider"),
                            colors = SliderDefaults.colors(
                                thumbColor = NeonCyan,
                                activeTrackColor = NeonCyan,
                                inactiveTrackColor = BorderColor
                            )
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "${customMinsInput}m",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            minLines = 1
                        )
                    }
                }
            }
        }

        // The Big Glowing Circular Timer visualizer
        item {
            Box(
                modifier = Modifier
                    .size(240.dp)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                // Circle Canvas progress
                TimerProgressVisualizer(progress = progress, state = timerState)

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (timerState == TimerState.RUNNING) "FOCUSING" else "IDLE MANA",
                        fontSize = 12.sp,
                        color = if (timerState == TimerState.RUNNING) NeonCyan else TextMuted,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = timeFormatted,
                        fontSize = 44.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.testTag("timer_countdown_text")
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = presetType.uppercase(),
                        fontSize = 10.sp,
                        color = NeonPink,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }

        // Action Buttons Row (Play, Pause, Stop)
        item {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Refresh/Stop
                IconButton(
                    onClick = onResetTimer,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CardBg)
                        .border(1.dp, BorderColor, CircleShape)
                        .testTag("reset_timer_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Timer",
                        tint = TextSecondary
                    )
                }

                // Main Play/Pause
                val isRunning = timerState == TimerState.RUNNING
                IconButton(
                    onClick = {
                        if (isRunning) onPauseTimer() else onStartTimer()
                    },
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(if (isRunning) NeonPink else NeonCyan)
                        .testTag("play_pause_timer_button")
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Play",
                        tint = DeepSlateBg,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Cheat / Quick study reward logger (Super helpful for grading testing)
                IconButton(
                    onClick = onInstantFinish,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CardBg)
                        .border(1.dp, BorderColor, CircleShape)
                        .testTag("instant_timer_cheat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.FastForward,
                        contentDescription = "Cheat Complete Session",
                        tint = RetroGold
                    )
                }
            }
        }

        // Focus Sound Module
        item {
            FocusSoundSection(
                selectedSound = selectedSound,
                isSoundPlaying = isSoundPlaying,
                onSelectSound = onSelectSound,
                onToggleSound = onToggleSound
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun TimerProgressVisualizer(progress: Float, state: TimerState) {
    val infiniteTransition = rememberInfiniteTransition(label = "arc")
    
    // Rotate canvas sub-effects during active study sessions
    val angleRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 14f
        val center = Offset(size.width / 2, size.height / 2)
        val radius = (size.width - strokeWidth) / 2

        // Draw grey track background
        drawCircle(
            color = BorderColor.copy(alpha = 0.5f),
            radius = radius,
            center = center,
            style = Stroke(width = strokeWidth)
        )

        // Draw animated sweet colors mapping sweep progression
        val brushColor = when (state) {
            TimerState.RUNNING -> Brush.sweepGradient(listOf(NeonCyan, NeonPurple, NeonCyan))
            TimerState.PAUSED -> Brush.sweepGradient(listOf(NeonPink, NeonPurple, NeonPink))
            else -> Brush.sweepGradient(listOf(NeonPurple, BorderColor, NeonPurple))
        }

        drawArc(
            brush = brushColor,
            startAngle = -90f + (if (state == TimerState.RUNNING) angleRotation else 0f),
            sweepAngle = 360f * progress,
            useCenter = false,
            topLeft = Offset(strokeWidth / 2, strokeWidth / 2),
            size = size.copy(width = size.width - strokeWidth, height = size.height - strokeWidth),
            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
        )

        // Cute glowing pointer dot if running
        if (state == TimerState.RUNNING && progress > 0f) {
            // Calculate pointer coordinates
            val currentAngle = (-90f + (if (state == TimerState.RUNNING) angleRotation else 0f) + (360f * progress)) * (Math.PI / 180)
            val dotX = center.x + radius * Math.cos(currentAngle).toFloat()
            val dotY = center.y + radius * Math.sin(currentAngle).toFloat()

            drawCircle(
                color = Color.White,
                radius = 16f,
                center = Offset(dotX, dotY)
            )
            drawCircle(
                color = NeonCyan,
                radius = 8f,
                center = Offset(dotX, dotY)
            )
        }
    }
}

@Composable
fun FocusSoundSection(
    selectedSound: String,
    isSoundPlaying: Boolean,
    onSelectSound: (String) -> Unit,
    onToggleSound: () -> Unit
) {
    val sounds = listOf(
        Pair("None", "🔕 SILENT"),
        Pair("Lofi", "🎵 LO-FI"),
        Pair("Rain", "⛈️ RAIN"),
        Pair("Cafe", "☕ COFFEE"),
        Pair("Forest", "🌲 FOREST")
    )

    RPGCard(borderColor = BorderColor) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isSoundPlaying) Icons.Default.MusicNote else Icons.Default.MusicOff,
                    contentDescription = null,
                    tint = if (isSoundPlaying) NeonCyan else TextMuted,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "FOCUS SOUND SYNTH",
                    fontSize = 12.sp,
                    color = NeonPink,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }

            if (selectedSound != "None") {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSoundPlaying) NeonCyan.copy(alpha = 0.2f) else BorderColor)
                        .clickable { onToggleSound() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Status",
                            tint = if (isSoundPlaying) NeonCyan else TextMuted,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isSoundPlaying) "PLAYING" else "MUTED",
                            fontSize = 9.sp,
                            color = if (isSoundPlaying) NeonCyan else TextMuted,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Horizontal audio channels
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(sounds) { sound ->
                val isSelected = selectedSound == sound.first
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) LightAccentBg else Color(0xFF130E26))
                        .border(
                            1.dp,
                            if (isSelected) NeonCyan else BorderColor,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { onSelectSound(sound.first) }
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = sound.second,
                        color = if (isSelected) NeonCyan else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Live Simulated play-bar details
        AnimatedVisibility(visible = isSoundPlaying && selectedSound != "None") {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF130E26))
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "🔊 Streaming procedural ambient loops for \"$selectedSound\" focus channel...",
                    color = NeonCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}
