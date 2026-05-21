package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.LightAccentBg
import com.example.ui.theme.ShieldBlue
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyMission
import com.example.data.model.UserStats
import com.example.ui.components.RPGAvatarRenderer
import com.example.ui.components.RPGButton
import com.example.ui.components.RPGCard
import com.example.ui.components.RPGProgressBar
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
fun DashboardScreen(
    stats: UserStats?,
    missions: List<DailyMission>,
    onQuickStudyClicked: () -> Unit,
    onNavigateToMissions: () -> Unit
) {
    val nonNullStats = stats ?: UserStats()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBg)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Avatar Row
        item {
            Spacer(modifier = Modifier.height(16.dp))
            DashboardHeroSection(nonNullStats)
        }

        // Quick Entry Banner
        item {
            DashboardCampaignLaunchCard(onQuickStudyClicked)
        }

        // Missions summary teaser list
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAILY CAMPAIGNS",
                    fontSize = 14.sp,
                    color = NeonPink,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "VIEW GUILD LOG",
                    fontSize = 11.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .testTag("view_missions_link")
                        .clickable { onNavigateToMissions() }
                )
            }
        }

        if (missions.isEmpty()) {
            item {
                RPGCard(borderColor = BorderColor) {
                    Text(
                        text = "📜 Prepping trial archives. Re-entering daily servers...",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        } else {
            items(missions.take(3)) { mission ->
                DashboardMissionItem(mission)
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DashboardHeroSection(stats: UserStats) {
    val levelProgress = stats.experience.toFloat() / stats.maxExpForLevel().toFloat()

    RPGCard(borderColor = NeonPurple) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cool Avatar Profile Render
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        Brush.radialGradient(listOf(NeonPurple.copy(alpha = 0.2f), Color.Transparent)),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                RPGAvatarRenderer(
                    avatarClass = stats.equippedAvatar,
                    skin = stats.equippedSkin,
                    modifier = Modifier.size(70.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // User level info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = "HERO LEVEL ${stats.level}",
                            fontSize = 18.sp,
                            color = NeonCyan,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = stats.equippedAvatar.uppercase() + " CLASS",
                            fontSize = 11.sp,
                            color = RetroGold,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Streak Badge
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(LightAccentBg)
                            .border(0.5.dp, NeonPink.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = NeonPink,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${stats.streak}D STREAK",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Progression bar
                RPGProgressBar(
                    progress = levelProgress,
                    label = "EXP TRACKER",
                    valueText = "${stats.experience} / ${stats.maxExpForLevel()} XP",
                    barColor = NeonPurple
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Basic Stats Counters summary row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DashboardSmallCounter(
                label = "STUDY TODAY",
                value = "${stats.todayStudyMinutes}m",
                icon = Icons.Default.Schedule,
                color = ShieldBlue
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(BorderColor)
            )
            DashboardSmallCounter(
                label = "TOTAL STUDY",
                value = "${stats.totalStudyMinutes}m",
                icon = Icons.Default.ElectricBolt,
                color = NeonPink
            )
            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(BorderColor)
            )
            DashboardSmallCounter(
                label = "QUESTS WON",
                value = "${stats.totalMissionsCompleted}",
                icon = Icons.Default.CheckCircle,
                color = RetroGold
            )
        }
    }
}

@Composable
fun DashboardCampaignLaunchCard(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(12.dp, RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(listOf(CardBg, NeonPurple.copy(alpha = 0.15f))),
                RoundedCornerShape(18.dp)
            )
            .border(
                1.3.dp,
                Brush.linearGradient(listOf(NeonCyan.copy(alpha = 0.8f), NeonPurple.copy(alpha = 0.2f))),
                RoundedCornerShape(18.dp)
            )
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "LAUNCH STUDY DUNGEON",
                    fontSize = 16.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Cast your focused Pomodoro spells now and harvest massive bonus EXP points.",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    lineHeight = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(NeonCyan)
                    .testTag("launch_timer_button"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start Timer",
                    tint = DeepSlateBg,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun DashboardSmallCounter(
    label: String,
    value: String,
    icon: ImageVector,
    color: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = TextPrimary
            )
        }
        Text(
            text = label,
            fontSize = 9.sp,
            color = TextMuted,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 2.dp)
        )
    }
}

@Composable
fun DashboardMissionItem(mission: DailyMission) {
    val progress = if (mission.targetCount > 0) {
        mission.currentCount.toFloat() / mission.targetCount.toFloat()
    } else {
        0f
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(if (mission.isCompleted) NeonCyan.copy(alpha = 0.15f) else BorderColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (mission.isCompleted) Icons.Default.Star else Icons.Default.StarOutline,
                contentDescription = null,
                tint = if (mission.isCompleted) NeonCyan else TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = mission.title,
                fontSize = 13.sp,
                color = if (mission.isCompleted) TextMuted else TextPrimary,
                fontWeight = FontWeight.Bold,
                textDecoration = if (mission.isCompleted) TextDecoration.LineThrough else null
            )
            Text(
                text = mission.description,
                fontSize = 11.sp,
                color = TextMuted,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Micro progress ticker
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Percentage or fraction
                Text(
                    text = "Progress: ${mission.currentCount}/${mission.targetCount}",
                    fontSize = 10.sp,
                    color = if (mission.isCompleted) NeonCyan else TextSecondary,
                    fontWeight = FontWeight.Medium
                )
                // EXP tags
                Text(
                    text = "+${mission.expReward} EXP",
                    fontSize = 10.sp,
                    color = RetroGold,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
