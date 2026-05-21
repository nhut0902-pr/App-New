package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 1,
    val level: Int = 1,
    val experience: Int = 0,
    val streak: Int = 0,
    val lastActiveDate: String = "", // yyyy-MM-dd
    val todayStudyMinutes: Int = 0,
    val totalStudyMinutes: Int = 0,
    val totalMissionsCompleted: Int = 0,
    val equippedAvatar: String = "warrior", // warrior, mage, rogue, paladin, necro, ranger
    val equippedSkin: String = "default", // default, neon, void, gold, plasma
    val unlockedAvatars: String = "warrior,mage", // Comma-separated list of unlocked classes
    val unlockedSkins: String = "default" // Comma-separated list of unlocked skins
) {
    fun maxExpForLevel(): Int {
        return 100 + (level - 1) * 50
    }
}

@Entity(tableName = "daily_missions")
data class DailyMission(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val targetCount: Int,
    val currentCount: Int,
    val isCompleted: Boolean = false,
    val expReward: Int,
    val missionType: String, // "STUDY_MINUTES", "POMODORO", "CHECK_IN", "ADD_TASK", "COMPLETE_TASK"
    val dateString: String // yyyy-MM-dd
)

@Entity(tableName = "study_sessions")
data class StudySession(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val durationMinutes: Int,
    val timestamp: Long = System.currentTimeMillis(),
    val focusSound: String = "None" // None, Lofi, Rain, Cafe, Forest
)

@Entity(tableName = "task_notes")
data class TaskNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String = "",
    val isCompleted: Boolean = false,
    val isNote: Boolean = false, // true = Note, false = Task
    val createdAt: Long = System.currentTimeMillis()
)
