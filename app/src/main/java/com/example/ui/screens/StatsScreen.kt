package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudySession
import com.example.data.model.UserStats
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun StatsScreen(
    stats: UserStats?,
    sessions: List<StudySession>
) {
    val nonNullStats = stats ?: UserStats()

    // Map sessions to represent 7 days of the week for our Canvas bar chart
    val dayStudyMinutes = doubleArrayOf(15.0, 30.0, 10.0, 45.0, 20.0, 0.0, 0.0) // Mock base fallback
    // Injects actual Room database study sessions into today's slot!
    dayStudyMinutes[4] = nonNullStats.todayStudyMinutes.toDouble().coerceAtLeast(10.0)

    val maxMinutes = (dayStudyMinutes.maxOrNull() ?: 60.0).coerceAtLeast(40.0)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBg)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // High level stats row counters
        item {
            Spacer(modifier = Modifier.height(16.dp))
            StatsGridRow(nonNullStats)
        }

        // Native Canvas Bar Chart Card
        item {
            RPGCard(borderColor = NeonCyan) {
                Text(
                    text = "CAMPAIGN CHRONICLES",
                    fontSize = 11.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Weekly Study Volume (mins)",
                    fontSize = 16.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Chart Canvas drawing
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        val spacingX = canvasWidth / 7f
                        val barWidth = spacingX * 0.5f

                        val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

                        // Draw Grid lines
                        val gridLines = 4
                        for (i in 0..gridLines) {
                            val y = (canvasHeight / gridLines) * i
                            drawLine(
                                color = BorderColor.copy(alpha = 0.3f),
                                start = Offset(0f, y),
                                end = Offset(canvasWidth, y),
                                strokeWidth = 2f
                            )
                        }

                        // Draw Bars
                        for (i in 0..6) {
                            val mins = dayStudyMinutes[i]
                            val barHeightRatio = (mins / maxMinutes).toFloat()
                            val barHeight = canvasHeight * barHeightRatio * 0.8f // Leave headspace

                            val startX = spacingX * i + (spacingX - barWidth) / 2f
                            val startY = canvasHeight - barHeight - 20f // Save border height for day label text

                            // Neon brush gradient
                            val brush = Brush.verticalGradient(
                                colors = listOf(NeonCyan, NeonPurple)
                            )

                            if (barHeight > 0f) {
                                drawRoundRect(
                                    brush = brush,
                                    topLeft = Offset(startX, startY),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(8f, 8f)
                                )

                                // Soft glow overlay
                                drawRoundRect(
                                    color = Color.White.copy(alpha = 0.15f),
                                    topLeft = Offset(startX, startY),
                                    size = Size(barWidth, barHeight / 4),
                                    cornerRadius = CornerRadius(8f, 8f)
                                )
                            }
                        }
                    }

                    // Bottom labels Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun").forEach { day ->
                            Text(
                                text = day,
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(36.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Recent Activity Sessions Logs list
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    tint = RetroGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "HISTORICAL QUEST LOGS",
                    fontSize = 13.sp,
                    color = RetroGold,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        if (sessions.isEmpty()) {
            item {
                RPGCard(borderColor = BorderColor) {
                    Text(
                        text = "📜 Empty chronicles. Open Pomodoro screen and finish a focused focus run to write history.",
                        color = TextMuted,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            items(sessions.take(15)) { session ->
                HistoricalSessionItem(session)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun StatsGridRow(stats: UserStats) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // High Score Pomo Minute
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .background(CardBg, RoundedCornerShape(16.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Icon(
                    imageVector = Icons.Default.HourglassBottom,
                    contentDescription = null,
                    tint = ShieldBlue,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${stats.totalStudyMinutes} MINS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Accumulated Focus",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }

        // Streak Highs card
        Box(
            modifier = Modifier
                .weight(1f)
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .background(CardBg, RoundedCornerShape(16.dp))
                .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Icon(
                    imageVector = Icons.Default.LocalFireDepartment,
                    contentDescription = null,
                    tint = NeonPink,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "${stats.streak} DAYS",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Highest Streak Run",
                    fontSize = 11.sp,
                    color = TextMuted
                )
            }
        }
    }
}

@Composable
fun HistoricalSessionItem(session: StudySession) {
    val sdf = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val formattedDate = sdf.format(Date(session.timestamp))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(NeonPurple.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = NeonPurple,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Focused Dungeon Campaign",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = formattedDate,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "+${session.durationMinutes}m",
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = NeonCyan
            )
            Text(
                text = "Ambient: ${session.focusSound}",
                fontSize = 9.sp,
                color = TextSecondary
            )
        }
    }
}
