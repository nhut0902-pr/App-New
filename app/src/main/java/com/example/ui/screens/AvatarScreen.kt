package com.example.ui.screens

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.LightAccentBg
import com.example.ui.theme.ShieldBlue
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserStats
import com.example.ui.components.RPGAvatarRenderer
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
fun AvatarScreen(
    stats: UserStats?,
    onEquipAvatar: (String) -> Unit,
    onEquipSkin: (String) -> Unit
) {
    val nonNullStats = stats ?: UserStats()
    val unlockedAvatars = nonNullStats.unlockedAvatars.split(",")
    val unlockedSkins = nonNullStats.unlockedSkins.split(",")

    // List of catalog elements
    val avatarClasses = listOf(
        AvatarClassItem("warrior", "WARRIOR", "Heavy focused study loops. Mighty protection shields.", 1),
        AvatarClassItem("mage", "MAGE", "Arcane magic spells. Transmuting books into pure EXP.", 1),
        AvatarClassItem("rogue", "ROGUE", "Stealthy silent focus timers. Fleet and swift focus runs.", 3),
        AvatarClassItem("paladin", "PALADIN", "Holy light configurations. Defends massive streaks.", 5),
        AvatarClassItem("ranger", "RANGER", "Precise focus target hunting. Archery details.", 7),
        AvatarClassItem("necro", "NECROMANCER", "Undead dark focus spells. Multi-tasking skills.", 10)
    )

    val customSkins = listOf(
        SkinThemeItem("default", "COSMIC INDIGO", "Vibrant amethyst shadows.", 1, NeonPurple),
        SkinThemeItem("neon", "CYBER GLINT", "Vivid turquoise lightning streaks.", 2, NeonCyan),
        SkinThemeItem("void", "DARK ECLIPSE", "Mystical shadowy purple nebulae.", 4, Color(0xFF7209B7)),
        SkinThemeItem("plasma", "SOLAR CORONA", "Vibrant hot plasma pink.", 6, NeonPink),
        SkinThemeItem("gold", "CHAMPION ORE", "Imperial gleaming gold plates.", 8, RetroGold)
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBg)
            .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Active Avatar Hero Overview
        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(16.dp))
            ActiveHeroCard(nonNullStats)
        }

        // Section header for Classes
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.WorkspacePremium,
                    contentDescription = null,
                    tint = RetroGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SELECT HERO CLASS",
                    fontSize = 13.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }
        }

        // Hero items grid (items list)
        items(avatarClasses) { avatar ->
            val isUnlocked = unlockedAvatars.contains(avatar.id) || nonNullStats.level >= avatar.reqLevel
            val isEquipped = nonNullStats.equippedAvatar == avatar.id

            AvatarClassGridCell(
                item = avatar,
                isUnlocked = isUnlocked,
                isEquipped = isEquipped,
                equippedSkin = nonNullStats.equippedSkin,
                onClick = {
                    if (isUnlocked) {
                        onEquipAvatar(avatar.id)
                    }
                }
            )
        }

        // Section header for Skins
        item(span = { GridItemSpan(2) }) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = NeonPink,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "SELECT AVATAR SKIN",
                    fontSize = 13.sp,
                    color = NeonPink,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }
        }

        // Skins items grid (items list)
        items(customSkins) { skin ->
            val isUnlocked = unlockedSkins.contains(skin.id) || nonNullStats.level >= skin.reqLevel
            val isEquipped = nonNullStats.equippedSkin == skin.id

            SkinThemeGridCell(
                item = skin,
                isUnlocked = isUnlocked,
                isEquipped = isEquipped,
                onClick = {
                    if (isUnlocked) {
                        onEquipSkin(skin.id)
                    }
                }
            )
        }

        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ActiveHeroCard(stats: UserStats) {
    RPGCard(borderColor = NeonPurple) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.verticalGradient(listOf(LightAccentBg, Color.Transparent))
                    ),
                contentAlignment = Alignment.Center
            ) {
                RPGAvatarRenderer(
                    avatarClass = stats.equippedAvatar,
                    skin = stats.equippedSkin,
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "MY INVENTORY",
                    fontSize = 11.sp,
                    color = RetroGold,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${stats.equippedAvatar.uppercase()} CRUSADER",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary
                )
                Text(
                    text = "Active Skin: " + stats.equippedSkin.uppercase(),
                    fontSize = 11.sp,
                    color = NeonCyan,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Reach levels to automatically unlock advanced characters and cyber cosmic colors.",
                    fontSize = 10.sp,
                    color = TextSecondary,
                    lineHeight = 14.sp
                )
            }
        }
    }
}

@Composable
fun AvatarClassGridCell(
    item: AvatarClassItem,
    isUnlocked: Boolean,
    isEquipped: Boolean,
    equippedSkin: String,
    onClick: () -> Unit
) {
    val borderBrush = if (isEquipped) {
        Brush.linearGradient(colors = listOf(NeonCyan, NeonPurple))
    } else {
        Brush.linearGradient(colors = listOf(BorderColor, BorderColor))
    }

    Box(
        modifier = Modifier
            .shadow(if (isEquipped) 6.dp else 0.dp, RoundedCornerShape(16.dp))
            .background(CardBg, RoundedCornerShape(16.dp))
            .border(
                1.3.dp,
                borderBrush,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(contentAlignment = Alignment.Center) {
                RPGAvatarRenderer(
                    avatarClass = item.id,
                    skin = if (isUnlocked) equippedSkin else "default",
                    modifier = Modifier
                        .size(64.dp)
                        .then(if (isUnlocked) Modifier else Modifier.clip(CircleShape).background(Color.Black.copy(alpha = 0.5f)))
                )

                if (!isUnlocked) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.62f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Locked",
                                tint = NeonPink,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "LVL ${item.reqLevel}",
                                color = NeonPink,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = item.name,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isEquipped) NeonCyan else if (isUnlocked) TextPrimary else TextMuted
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = item.desc,
                fontSize = 9.sp,
                color = TextMuted,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp
            )

            if (isEquipped) {
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(NeonCyan.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "EQUIPPED",
                        color = NeonCyan,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

@Composable
fun SkinThemeGridCell(
    item: SkinThemeItem,
    isUnlocked: Boolean,
    isEquipped: Boolean,
    onClick: () -> Unit
) {
    val borderBrush = if (isEquipped) {
        Brush.linearGradient(colors = listOf(NeonPink, NeonPurple))
    } else {
        Brush.linearGradient(colors = listOf(BorderColor, BorderColor))
    }

    Box(
        modifier = Modifier
            .height(130.dp)
            .shadow(if (isEquipped) 6.dp else 0.dp, RoundedCornerShape(16.dp))
            .background(CardBg, RoundedCornerShape(16.dp))
            .border(
                1.3.dp,
                borderBrush,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        if (!isUnlocked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = TextSecondary.copy(alpha = 0.5f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "LEVEL ${item.reqLevel}",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "UNLOCKED",
                        color = TextMuted,
                        fontSize = 8.sp
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Color Circle Display Indicator
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(item.colorColor)
                        .border(1.5.dp, Color.White, CircleShape)
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = item.name,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = if (isEquipped) NeonPink else TextPrimary,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = item.desc,
                        fontSize = 8.sp,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }

                if (isEquipped) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(NeonPink.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ACTIVE",
                            color = NeonPink,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                } else {
                    Text(
                        text = "EQUIP",
                        fontSize = 9.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

data class AvatarClassItem(val id: String, val name: String, val desc: String, val reqLevel: Int)
data class SkinThemeItem(val id: String, val name: String, val desc: String, val reqLevel: Int, val colorColor: Color)
