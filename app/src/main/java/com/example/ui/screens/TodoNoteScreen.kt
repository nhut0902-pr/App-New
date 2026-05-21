package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AssignmentTurnedIn
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.TaskNote
import com.example.ui.components.RPGCard
import com.example.ui.components.RPGButton
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TodoNoteScreen(
    tasks: List<TaskNote>,
    notes: List<TaskNote>,
    onCreateQuest: (String) -> Unit,
    onToggleQuest: (TaskNote) -> Unit,
    onSaveNote: (String, String) -> Unit,
    onDeleteTaskNote: (TaskNote) -> Unit
) {
    var activeTab by remember { mutableStateOf("quests") } // quests OR notes

    // Input States
    var questTitleInput by remember { mutableStateOf("") }
    var noteTitleInput by remember { mutableStateOf("") }
    var noteContentInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBg)
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Upper Tab selection Row
        item {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(CardBg)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                listOf(
                    Pair("quests", "⚔️ QUEST LOG"),
                    Pair("notes", "📜 ARCHIVE SCROLL")
                ).forEach { tab ->
                    val isSelected = activeTab == tab.first
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) NeonPurple else Color.Transparent)
                            .clickable { activeTab = tab.first }
                            .padding(vertical = 12.dp)
                            .testTag("tab_" + tab.first),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.second,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }

        // Action Inputs based on active tab
        if (activeTab == "quests") {
            // Quest Input Row
            item {
                RPGCard(borderColor = NeonCyan) {
                    Text(
                        text = "DRAFT NEW FOCUS QUEST",
                        fontSize = 11.sp,
                        color = NeonCyan,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = questTitleInput,
                            onValueChange = { questTitleInput = it },
                            placeholder = { Text("e.g., Read Physics Chapter 3", color = TextMuted, fontSize = 13.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("quest_input_field"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedBorderColor = NeonCyan,
                                unfocusedBorderColor = BorderColor,
                                focusedContainerColor = Color(0xFF130E26),
                                unfocusedContainerColor = Color(0xFF130E26)
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(NeonCyan)
                                .clickable {
                                    if (questTitleInput.isNotBlank()) {
                                        onCreateQuest(questTitleInput.trim())
                                        questTitleInput = ""
                                    }
                                }
                                .testTag("add_quest_button"),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add task",
                                tint = DeepSlateBg,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // Quests List Headers
            if (tasks.isEmpty()) {
                item {
                    RPGCard(borderColor = BorderColor) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlaylistAddCheck,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Your Quest log is empty.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Draft some study quests to gain XP bonuses and clear dungeon campaigns.",
                                color = TextMuted,
                                fontSize = 11.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            items(tasks) { task ->
                QuestItemRow(
                    task = task,
                    onToggle = { onToggleQuest(task) },
                    onDelete = { onDeleteTaskNote(task) }
                )
            }
        } else {
            // Study Research Notes Tab Input Form
            item {
                RPGCard(borderColor = NeonPink) {
                    Text(
                        text = "SCRIBE CHRONICLES NOTE",
                        fontSize = 11.sp,
                        color = NeonPink,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = noteTitleInput,
                        onValueChange = { noteTitleInput = it },
                        placeholder = { Text("Note Title (e.g. Calculus formulas)", color = TextMuted, fontSize = 13.sp) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_title_field"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = BorderColor,
                            focusedContainerColor = Color(0xFF130E26),
                            unfocusedContainerColor = Color(0xFF130E26)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = noteContentInput,
                        onValueChange = { noteContentInput = it },
                        placeholder = { Text("Enter detailed study summaries or formulae locks...", color = TextMuted, fontSize = 12.sp) },
                        minLines = 3,
                        maxLines = 6,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("note_content_field"),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedBorderColor = NeonPink,
                            unfocusedBorderColor = BorderColor,
                            focusedContainerColor = Color(0xFF130E26),
                            unfocusedContainerColor = Color(0xFF130E26)
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    RPGButton(
                        text = "SAVE STUDY ARCHIVE",
                        onClick = {
                            if (noteTitleInput.isNotBlank() || noteContentInput.isNotBlank()) {
                                onSaveNote(
                                    noteTitleInput.ifBlank { "Untitled Chron" },
                                    noteContentInput
                                )
                                noteTitleInput = ""
                                noteContentInput = ""
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("save_note_button"),
                        containerColor = NeonPink,
                        icon = Icons.Default.Notes
                    )
                }
            }

            // Quick Notes list
            if (notes.isEmpty()) {
                item {
                    RPGCard(borderColor = BorderColor) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "The Archive Scroll is blank.",
                                color = TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Scribe formula reminders, quick chapter research briefs, or Pomodoro notes here.",
                                color = TextMuted,
                                fontSize = 11.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            items(notes) { note ->
                ArchiveNoteRow(
                    note = note,
                    onDelete = { onDeleteTaskNote(note) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuestItemRow(
    task: TaskNote,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val checkboxColor by animateColorAsState(
        if (task.isCompleted) NeonCyan else Color.Transparent, label = "color"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardBg)
            .border(
                1.dp,
                if (task.isCompleted) NeonCyan.copy(alpha = 0.3f) else BorderColor,
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cyber checklist checkbox ring
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .border(2.dp, if (task.isCompleted) NeonCyan else BorderColor, CircleShape)
                    .background(checkboxColor)
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                if (task.isCompleted) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Done",
                        tint = DeepSlateBg,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Text(
                text = task.title,
                fontSize = 13.sp,
                color = if (task.isCompleted) TextMuted else TextPrimary,
                fontWeight = FontWeight.Bold,
                textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null,
                modifier = Modifier.clickable { onToggle() }
            )
        }

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.DeleteOutline,
                contentDescription = "Delete quest",
                tint = TextMuted,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ArchiveNoteRow(
    note: TaskNote,
    onDelete: () -> Unit
) {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    val formattedDate = sdf.format(Date(note.createdAt))

    RPGCard(borderColor = BorderColor) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary
                )
                Text(
                    text = "Scribed on $formattedDate",
                    fontSize = 10.sp,
                    color = TextMuted
                )

                if (note.content.isNotBlank()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF130E26))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = note.content,
                            fontSize = 12.sp,
                            color = TextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteOutline,
                    contentDescription = "Delete Note",
                    tint = TextMuted,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
