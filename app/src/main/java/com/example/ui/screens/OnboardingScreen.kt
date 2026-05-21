package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.RPGAvatarRenderer
import com.example.ui.components.RPGButton
import com.example.ui.theme.BorderColor
import com.example.ui.theme.CardBg
import com.example.ui.theme.DeepSlateBg
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.RetroGold
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun OnboardingScreen(
    onFinished: (avatarClass: String) -> Unit
) {
    var step by remember { mutableStateOf(1) }
    var selectedClass by remember { mutableStateOf("warrior") }
    var dailyTargetMinutes by remember { mutableStateOf(20) }

    val totalSteps = 3

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBg)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LEVELUP STUDY",
                    color = NeonCyan,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.5.sp
                )

                // Page count bubbles
                Row {
                    for (i in 1..totalSteps) {
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 4.dp)
                                .size(if (i == step) 16.dp else 10.dp)
                                .clip(if (i == step) RoundedCornerShape(4.dp) else CircleShape)
                                .background(if (i == step) NeonCyan else BorderColor)
                        )
                    }
                }
            }

            // Animated body step container
            AnimatedContent(
                targetState = step,
                transitionSpec = {
                    fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
                },
                label = "stepAnim",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) { activeStep ->
                when (activeStep) {
                    1 -> OnboardingSlideIntro()
                    2 -> OnboardingSlideClassSelect(
                        selectedClass = selectedClass,
                        onClassSelected = { selectedClass = it }
                    )
                    3 -> OnboardingSlideTargetSet(
                        dailyTargetMinutes = dailyTargetMinutes,
                        onTargetSelected = { dailyTargetMinutes = it }
                    )
                }
            }

            // Bottom Navigation triggers
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (step < totalSteps) {
                    RPGButton(
                        text = "NEXT STAGE",
                        onClick = { step += 1 },
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = NeonPurple,
                        icon = Icons.Default.ArrowForward
                    )
                } else {
                    RPGButton(
                        text = "BEGIN YOUR CAMPAIGN!",
                        onClick = { onFinished(selectedClass) },
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = NeonCyan,
                        contentColor = DeepSlateBg
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "A simple RPG focus experience. Start level 1, end legendary.",
                    fontSize = 11.sp,
                    color = TextSecondary.copy(alpha = 0.5f),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun OnboardingSlideIntro() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(140.dp)
                .background(
                    Brush.radialGradient(listOf(NeonCyan.copy(alpha = 0.25f), Color.Transparent)),
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            RPGAvatarRenderer(
                avatarClass = "mage",
                skin = "plasma",
                modifier = Modifier.size(100.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome to the Learn Guild!",
            fontSize = 24.sp,
            color = TextPrimary,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Turn studying into a cosmic gaming experience.\nStudy to gain EXP, level up your character, unlock mystical skins, and execute daily mission campaigns.",
            fontSize = 14.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
    }
}

@Composable
fun OnboardingSlideClassSelect(
    selectedClass: String,
    onClassSelected: (String) -> Unit
) {
    val classes = listOf(
        Pair("warrior", "WARRIOR\nDefense and shield study routines."),
        Pair("mage", "MAGE\nRadiant magic pomodoros."),
        Pair("rogue", "ROGUE\nStealth ninja focus tracks."),
        Pair("paladin", "PALADIN\nHoly light high-streak campaigns.")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Choose Your Champion Class",
            fontSize = 20.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "Your starter class triggers your layout avatar.",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Show a 2x2 grid of classes
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            for (row in 0..1) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    for (col in 0..1) {
                        val index = row * 2 + col
                        val classInfo = classes[index]
                        val isSelected = selectedClass == classInfo.first

                        val gradient = if (isSelected) {
                            Brush.linearGradient(listOf(NeonCyan, NeonPurple))
                        } else {
                            Brush.linearGradient(listOf(CardBg, CardBg))
                        }

                        val borderBrush = if (isSelected) {
                            Brush.linearGradient(colors = listOf(NeonCyan, NeonPurple))
                        } else {
                            Brush.linearGradient(colors = listOf(BorderColor, BorderColor))
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .shadow(if (isSelected) 10.dp else 0.dp)
                                .background(CardBg, RoundedCornerShape(16.dp))
                                .border(1.5.dp, borderBrush, RoundedCornerShape(16.dp))
                                .clickable { onClassSelected(classInfo.first) }
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                RPGAvatarRenderer(
                                    avatarClass = classInfo.first,
                                    skin = if (isSelected) "neon" else "default",
                                    modifier = Modifier.size(60.dp)
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                val firstLine = classInfo.second.substringBefore("\n")
                                val secondLine = classInfo.second.substringAfter("\n")

                                Text(
                                    text = firstLine,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSelected) NeonCyan else TextPrimary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = secondLine,
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OnboardingSlideTargetSet(
    dailyTargetMinutes: Int,
    onTargetSelected: (Int) -> Unit
) {
    val presets = listOf(
        Pair(15, "15 MINS\nCasual Quest"),
        Pair(30, "30 MINS\nJourneyman Trial"),
        Pair(60, "60 MINS\nHero's Campaign"),
        Pair(120, "120 MINS\nLegendary Crusade")
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Daily Campaign Duration",
            fontSize = 20.sp,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        Text(
            text = "Select your daily objective. You can adjust this later.",
            fontSize = 13.sp,
            color = TextSecondary,
            modifier = Modifier.padding(bottom = 32.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            presets.forEach { preset ->
                val isSelected = dailyTargetMinutes == preset.first
                val border = if (isSelected) {
                    BorderColorLinearBrush(NeonPink)
                } else {
                    BorderColorLinearBrush(BorderColor)
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(if (isSelected) 8.dp else 0.dp)
                        .background(CardBg, RoundedCornerShape(16.dp))
                        .border(1.5.dp, border.brush, RoundedCornerShape(16.dp))
                        .clickable { onTargetSelected(preset.first) }
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val firstPart = preset.second.substringBefore("\n")
                        val secondPart = preset.second.substringAfter("\n")

                        Text(
                            text = firstPart,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            color = if (isSelected) NeonPink else TextPrimary
                        )
                        Text(
                            text = secondPart,
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .border(2.dp, if (isSelected) NeonPink else BorderColor, CircleShape)
                            .background(if (isSelected) NeonPink else Color.Transparent),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(DeepSlateBg)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BorderColorLinearBrush(color: Color): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(
        1.5.dp,
        Brush.linearGradient(listOf(color, color.copy(alpha = 0.2f)))
    )
}
