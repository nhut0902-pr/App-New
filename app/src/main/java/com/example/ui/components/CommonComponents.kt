package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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

@Composable
fun RPGCard(
    modifier: Modifier = Modifier,
    borderColor: Color = BorderColor,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(onClick = onClick)
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .shadow(4.dp, RoundedCornerShape(18.dp))
            .background(CardBg, RoundedCornerShape(18.dp))
            .border(
                1.3.dp,
                Brush.linearGradient(
                    colors = listOf(borderColor, borderColor.copy(alpha = 0.3f), Color.Transparent)
                ),
                RoundedCornerShape(18.dp)
            )
            .then(clickableModifier)
            .padding(16.dp)
    ) {
        content()
    }
}

@Composable
fun RPGProgressBar(
    progress: Float, // 0.0 to 1.0
    label: String,
    valueText: String,
    modifier: Modifier = Modifier,
    barColor: Color = NeonPurple,
    showGlowingEffect: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = { it }),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = valueText,
                fontSize = 12.sp,
                color = TextPrimary,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(14.dp)
                .background(Color(0xFF130E26), RoundedCornerShape(7.dp))
                .border(0.5.dp, BorderColor, RoundedCornerShape(7.dp))
                .clip(RoundedCornerShape(7.dp))
        ) {
            // Exp Progress bar
            Canvas(modifier = Modifier.fillMaxSize()) {
                val progressWidth = size.width * progress.coerceIn(0f, 1f)
                
                // Outer bar background shadow
                drawRoundRect(
                    color = barColor.copy(alpha = 0.15f),
                    size = size,
                    cornerRadius = CornerRadius(7f, 7f)
                )

                if (progressWidth > 0f) {
                    // Solid gradient bar
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(barColor, barColor.copy(alpha = 0.6f))
                        ),
                        size = Size(progressWidth, size.height),
                        cornerRadius = CornerRadius(7f, 7f)
                    )

                    if (showGlowingEffect) {
                        // Light sheen effect
                        drawRoundRect(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.White.copy(alpha = 0.3f), Color.Transparent),
                                startX = progressWidth * 0.2f,
                                endX = progressWidth
                            ),
                            size = Size(progressWidth, size.height / 3f),
                            cornerRadius = CornerRadius(7f, 7f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RPGButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    containerColor: Color = NeonPurple,
    contentColor: Color = Color.White,
    icon: ImageVector? = null,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(50.dp)
            .shadow(
                if (enabled) 8.dp else 0.dp,
                shape = RoundedCornerShape(14.dp),
                ambientColor = containerColor,
                spotColor = containerColor
            ),
        enabled = enabled,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = containerColor.copy(alpha = 0.4f),
            disabledContentColor = TextSecondary.copy(alpha = 0.5f)
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                letterSpacing = 0.5.sp
            )
        }
    }
}

// Beautiful procedural vector-style RPG Class Avatars drawn on Canvas
@Composable
fun RPGAvatarRenderer(
    avatarClass: String,
    skin: String = "default",
    modifier: Modifier = Modifier,
    showShield: Boolean = false
) {
    // Collect skin colors
    val themeBrush = when (skin) {
        "neon" -> Brush.radialGradient(colors = listOf(NeonCyan, Color(0xFF00302E)))
        "void" -> Brush.radialGradient(colors = listOf(Color(0xFF3C096C), Color(0xFF10002B)))
        "gold" -> Brush.radialGradient(colors = listOf(RetroGold, Color(0xFF382300)))
        "plasma" -> Brush.radialGradient(colors = listOf(NeonPink, Color(0xFF300015)))
        else -> Brush.radialGradient(colors = listOf(NeonPurple, Color(0xFF0F051D)))
    }

    val skinColor = when (skin) {
        "neon" -> NeonCyan
        "void" -> Color(0xFF7B2CBF)
        "gold" -> RetroGold
        "plasma" -> NeonPink
        else -> NeonPurple
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(themeBrush)
            .border(2.dp, skinColor, CircleShape)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2, size.height / 2)
            val w = size.width
            val h = size.height

            // Procedural drawing of specific elements based on class
            when (avatarClass.lowercase()) {
                "warrior" -> {
                    // Draw heavy helmet & sword lines
                    // Helmet faceplate outline
                    val helmPath = Path().apply {
                        moveTo(w * 0.25f, h * 0.2f)
                        lineTo(w * 0.75f, h * 0.2f)
                        lineTo(w * 0.8f, h * 0.55f)
                        lineTo(center.x, h * 0.85f)
                        lineTo(w * 0.2f, h * 0.55f)
                        close()
                    }
                    drawPath(helmPath, color = Color(0xFFA2A5B5))

                    // Helmet plume
                    drawArc(
                        color = skinColor,
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = true,
                        topLeft = Offset(w * 0.35f, h * 0.02f),
                        size = Size(w * 0.3f, h * 0.25f)
                    )

                    // Helmet eyes slit
                    drawRect(
                        color = Color(0xFF130E26),
                        topLeft = Offset(w * 0.32f, h * 0.35f),
                        size = Size(w * 0.36f, h * 0.1f)
                    )

                    // Crossed sword details
                    drawLine(
                        color = skinColor,
                        start = Offset(w * 0.1f, h * 0.9f),
                        end = Offset(w * 0.9f, h * 0.1f),
                        strokeWidth = 4f
                    )
                }
                "mage" -> {
                    // Wizard pointed hat
                    val hatPath = Path().apply {
                        moveTo(w * 0.15f, h * 0.45f)
                        quadraticTo(w * 0.5f, h * 0.35f, w * 0.85f, h * 0.45f)
                        lineTo(w * 0.5f, h * 0.05f)
                        close()
                    }
                    drawPath(hatPath, color = Color(0xFF240046))

                    // Hat brim edge
                    drawOval(
                        color = skinColor,
                        topLeft = Offset(w * 0.1f, h * 0.4f),
                        size = Size(w * 0.8f, h * 0.12f)
                    )

                    // Star details
                    drawCircle(
                        color = RetroGold,
                        radius = w * 0.06f,
                        center = Offset(w * 0.5f, h * 0.28f)
                    )
                    // Magic sparkling core
                    drawCircle(
                        color = NeonCyan,
                        radius = w * 0.15f,
                        center = Offset(w * 0.5f, h * 0.72f)
                    )
                }
                "rogue" -> {
                    // Ninja cowl eyes slit
                    val cowlPath = Path().apply {
                        moveTo(w * 0.2f, h * 0.25f)
                        quadraticTo(w * 0.5f, h * 0.15f, w * 0.8f, h * 0.25f)
                        lineTo(w * 0.85f, h * 0.75f)
                        quadraticTo(w * 0.5f, h * 0.9f, w * 0.15f, h * 0.75f)
                        close()
                    }
                    drawPath(cowlPath, color = Color(0xFF22242B))

                    // Ninja Mask wrap line
                    drawRect(
                        color = skinColor,
                        topLeft = Offset(w * 0.2f, h * 0.46f),
                        size = Size(w * 0.6f, h * 0.05f)
                    )

                    // Glowing green stealthy eyes
                    drawOval(
                        color = NeonCyan,
                        topLeft = Offset(w * 0.3f, h * 0.33f),
                        size = Size(w * 0.14f, h * 0.07f)
                    )
                    drawOval(
                        color = NeonCyan,
                        topLeft = Offset(w * 0.56f, h * 0.33f),
                        size = Size(w * 0.14f, h * 0.07f)
                    )
                }
                "paladin" -> {
                    // Holy Hammer & sunburst back
                    drawCircle(
                        color = Color.White.copy(alpha = 0.4f),
                        radius = w * 0.35f,
                        center = center,
                        style = Stroke(width = 3f)
                    )

                    // Helmet with cross emblem
                    val helmPath = Path().apply {
                        moveTo(w * 0.3f, h * 0.22f)
                        lineTo(w * 0.7f, h * 0.22f)
                        lineTo(w * 0.75f, h * 0.6f)
                        lineTo(center.x, h * 0.82f)
                        lineTo(w * 0.25f, h * 0.6f)
                        close()
                    }
                    drawPath(helmPath, color = Color(0xFFBAC0D1))

                    // Holy Cross emblem on helmet
                    drawLine(
                        color = skinColor,
                        start = Offset(center.x, h * 0.3f),
                        end = Offset(center.x, h * 0.65f),
                        strokeWidth = 6f
                    )
                    drawLine(
                        color = skinColor,
                        start = Offset(w * 0.38f, h * 0.44f),
                        end = Offset(w * 0.62f, h * 0.44f),
                        strokeWidth = 6f
                    )
                }
                "ranger" -> {
                    // Leaf green cowl
                    val cowlPath = Path().apply {
                        moveTo(w * 0.5f, h * 0.1f)
                        quadraticTo(w * 0.85f, h * 0.2f, w * 0.8f, h * 0.72f)
                        lineTo(center.x, h * 0.9f)
                        lineTo(w * 0.2f, h * 0.72f)
                        quadraticTo(w * 0.15f, h * 0.2f, w * 0.5f, h * 0.1f)
                    }
                    drawPath(cowlPath, color = Color(0xFF143026))

                    // Cloak clasp
                    drawCircle(
                        color = skinColor,
                        radius = w * 0.06f,
                        center = Offset(center.x, h * 0.75f)
                    )

                    // Bow silhouette arc
                    drawArc(
                        color = RetroGold,
                        startAngle = 135f,
                        sweepAngle = 90f,
                        useCenter = false,
                        topLeft = Offset(w * 0.05f, h * 0.25f),
                        size = Size(w * 0.9f, h * 0.5f),
                        style = Stroke(width = 4f)
                    )
                }
                "necro" -> {
                    // Undead hood
                    val cowlPath = Path().apply {
                        moveTo(w * 0.5f, h * 0.05f)
                        quadraticTo(w * 0.85f, h * 0.2f, w * 0.8f, h * 0.75f)
                        quadraticTo(w * 0.5f, h * 0.95f, w * 0.2f, h * 0.75f)
                        quadraticTo(w * 0.15f, h * 0.2f, w * 0.5f, h * 0.05f)
                    }
                    drawPath(cowlPath, color = Color(0xFF0F0F13))

                    // Glowing eyes in dark shadow
                    drawCircle(
                        color = skinColor,
                        radius = w * 0.05f,
                        center = Offset(w * 0.38f, h * 0.45f)
                    )
                    drawCircle(
                        color = skinColor,
                        radius = w * 0.05f,
                        center = Offset(w * 0.62f, h * 0.45f)
                    )

                    // Horn skull details
                    drawLine(
                        color = skinColor.copy(alpha = 0.5f),
                        start = Offset(w * 0.35f, h * 0.32f),
                        end = Offset(w * 0.65f, h * 0.32f),
                        strokeWidth = 2f
                    )
                }
                else -> {
                    // Generic hero mask
                    drawCircle(color = skinColor, radius = w * 0.3f, center = center)
                    drawCircle(color = Color.White, radius = w * 0.08f, center = Offset(center.x - w * 0.1f, center.y))
                    drawCircle(color = Color.White, radius = w * 0.08f, center = Offset(center.x + w * 0.1f, center.y))
                }
            }
        }
    }
}

// Sparkle Effect animation for Level Up celebration
@Composable
fun SparkleEffect(modifier: Modifier = Modifier, color: Color = RetroGold) {
    val infiniteTransition = rememberInfiniteTransition(label = "sparkle")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val scaleAnim by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Canvas(modifier = modifier) {
        val radius = (size.width / 2) * scaleAnim
        val center = Offset(size.width / 2, size.height / 2)

        // Draw simple crossed sparkles
        drawCircle(
            color = color.copy(alpha = 0.15f * alphaAnim),
            radius = radius * 1.4f,
            center = center
        )

        // Vertical burst star shape
        val path = Path().apply {
            moveTo(center.x, center.y - radius)
            quadraticTo(center.x, center.y, center.x + radius, center.y)
            quadraticTo(center.x, center.y, center.x, center.y + radius)
            quadraticTo(center.x, center.y, center.x - radius, center.y)
            quadraticTo(center.x, center.y, center.x, center.y - radius)
            close()
        }
        drawPath(path, color = color.copy(alpha = alphaAnim))
    }
}


@Composable
fun LevelUpDialog(
    newLevel: Int,
    rewardType: String, // "Avatar" or "Skin"
    rewardName: String,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = CardBg,
            border = BorderBorderLinearBrush(NeonCyan)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background sparkles
                SparkleEffect(
                    modifier = Modifier
                        .size(180.dp)
                        .align(Alignment.Center),
                    color = RetroGold
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🏆 CLASS PROMOTED! 🏆",
                        color = RetroGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        letterSpacing = 1.5.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .background(
                                Brush.radialGradient(listOf(NeonCyan.copy(alpha = 0.2f), Color.Transparent)),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$newLevel",
                            color = NeonCyan,
                            fontSize = 48.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "You reached Level $newLevel!",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Reward unlocked: $rewardType - \"${rewardName.uppercase()}\"",
                        color = NeonPink,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    RPGButton(
                        text = "CLAIM REWARD & EQUIP",
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = NeonCyan,
                        contentColor = DeepSlateBg
                    )
                }
            }
        }
    }
}

// Extension to draw custom brush border
@Composable
fun BorderBorderLinearBrush(color: Color): androidx.compose.foundation.BorderStroke {
    return androidx.compose.foundation.BorderStroke(
        1.5.dp,
        Brush.linearGradient(listOf(color, color.copy(alpha = 0.2f)))
    )
}
