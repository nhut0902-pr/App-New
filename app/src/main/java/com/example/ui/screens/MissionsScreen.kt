package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.CrisisAlert
import androidx.compose.material.icons.filled.DoubleArrow
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyMission
import com.example.data.model.UserStats
import com.example.ui.components.RPGButton
import com.example.ui.components.RPGCard
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBg
import com.example.ui.theme.DeepSlateBg
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.RetroGold
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MissionsScreen(
    missions: List<DailyMission>,
    stats: UserStats?,
    onForceLevelUp: () -> Unit // Developer Cheat
) {
    val totalMissions = missions.size
    val completedMissions = missions.count { it.isCompleted }
    val progressPercent = if (totalMissions > 0) completedMissions * 100 / totalMissions else 0

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBg)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Quest Guild Stats Banner
        item {
            Spacer(modifier = Modifier.height(16.dp))
            RPGCard(borderColor = NeonPink) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "GUILD OUTPOST",
                            fontSize = 11.sp,
                            color = NeonPink,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Daily Campaign Progress",
                            fontSize = 18.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(NeonPink.copy(alpha = 0.15f))
                            .border(1.dp, NeonPink, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$progressPercent%",
                            color = NeonPink,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = BorderColor)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Guild quests solved today:",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                    Text(
                        text = "$completedMissions / $totalMissions",
                        fontSize = 15.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Campaign Trials Header Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.MilitaryTech,
                    contentDescription = null,
                    tint = RetroGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ACTIVE GUILD TRIALS",
                    fontSize = 14.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        if (missions.isEmpty()) {
            item {
                RPGCard(borderColor = BorderColor) {
                    Text(
                        text = "📜 Prepping trial archives. Servers rebooting...",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            items(missions) { mission ->
                MissionDetailItem(mission)
            }
        }

        // Developer cheats section for quick evaluation!
        item {
            RPGCard(borderColor = RetroGold) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CrisisAlert,
                        contentDescription = null,
                        tint = RetroGold,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ADMIN GUILD CONSOLE",
                        fontSize = 12.sp,
                        color = RetroGold,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Gain XP quickly to showcase the Level-Up custom dialogs and unlock legendary warrior or paladin classes instantly.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                RPGButton(
                    text = "EXECUTE LEVEL-UP CHEAT",
                    onClick = onForceLevelUp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("force_level_up_cheat"),
                    containerColor = RetroGold,
                    contentColor = DeepSlateBg
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun MissionDetailItem(mission: DailyMission) {
    val progress = if (mission.targetCount > 0) {
        mission.currentCount.toFloat() / mission.targetCount.toFloat()
    } else {
        0f
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardBg)
            .border(
                1.3.dp,
                if (mission.isCompleted) NeonCyan.copy(alpha = 0.5f) else BorderColor,
                RoundedCornerShape(16.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (mission.isCompleted) NeonCyan.copy(alpha = 0.15f) else BorderColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (mission.isCompleted) Icons.Default.Star else Icons.Default.StarOutline,
                contentDescription = null,
                tint = if (mission.isCompleted) NeonCyan else TextMuted,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = mission.title,
                    fontSize = 14.sp,
                    color = if (mission.isCompleted) TextMuted else TextPrimary,
                    fontWeight = FontWeight.ExtraBold,
                    textDecoration = if (mission.isCompleted) TextDecoration.LineThrough else null,
                    modifier = Modifier.weight(1f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF130E26))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "+${mission.expReward} EXP",
                        color = RetroGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = mission.description,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Progress tracking fraction metric
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (mission.isCompleted) "🏆 COMPLETED" else "⚔️ ACTIVE TRIAL",
                    color = if (mission.isCompleted) NeonCyan else NeonPink,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black
                )

                Text(
                    text = "Goal: ${mission.currentCount} / ${mission.targetCount}",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Graphic inner sub-slider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF130E26))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceAtMost(1f))
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    if (mission.isCompleted) NeonCyan else NeonPink,
                                    NeonPurple
                                )
                            )
                        )
                )
            }
        }
    }
}
