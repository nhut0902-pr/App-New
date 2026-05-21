package com.example.data.repository

import com.example.data.local.DailyMissionDao
import com.example.data.local.StudySessionDao
import com.example.data.local.TaskNoteDao
import com.example.data.local.UserStatsDao
import com.example.data.model.DailyMission
import com.example.data.model.StudySession
import com.example.data.model.TaskNote
import com.example.data.model.UserStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class StudyRepository(
    private val userStatsDao: UserStatsDao,
    private val dailyMissionDao: DailyMissionDao,
    private val studySessionDao: StudySessionDao,
    private val taskNoteDao: TaskNoteDao
) {
    // Flows
    val userStatsFlow: Flow<UserStats?> = userStatsDao.getUserStatsFlow()
    val allSessionsFlow: Flow<List<StudySession>> = studySessionDao.getAllSessionsFlow()
    val tasksFlow: Flow<List<TaskNote>> = taskNoteDao.getTasksFlow()
    val notesFlow: Flow<List<TaskNote>> = taskNoteDao.getNotesFlow()

    fun getMissionsForDateFlow(date: String): Flow<List<DailyMission>> {
        return dailyMissionDao.getMissionsForDateFlow(date)
    }

    // Date Utilities
    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(cal.time)
    }

    // Core Init Log: Checks date transitions, updates streak, handles daily missions reset
    suspend fun checkDailyTransition(): Boolean {
        val today = getTodayDateString()
        var stats = userStatsDao.getUserStats()
        var levelUpOccurred = false

        if (stats == null) {
            // First time seeding
            stats = UserStats(
                level = 1,
                experience = 0,
                streak = 1,
                lastActiveDate = today,
                todayStudyMinutes = 0,
                totalStudyMinutes = 0,
                unlockedAvatars = "warrior,mage",
                unlockedSkins = "default"
            )
            userStatsDao.insertOrUpdate(stats)
            generateDailyMissions(today)
            // Perform automatic check-in completion
            completeCheckInMission(today)
        } else {
            val lastActive = stats.lastActiveDate
            if (lastActive != today) {
                // Determine streak reward
                val yesterday = getYesterdayDateString()
                val newStreak = if (lastActive == yesterday) {
                    stats.streak + 1
                } else {
                    1 // Streak broken
                }

                // Add 15 EXP reward for daily check-in log
                val result = addExperienceInternal(stats, 15)
                levelUpOccurred = result.second

                stats = result.first.copy(
                    streak = newStreak,
                    lastActiveDate = today,
                    todayStudyMinutes = 0 // Reset daily limit
                )
                userStatsDao.insertOrUpdate(stats)

                // Clean old daily missions, generate new ones
                dailyMissionDao.deleteOldMissions(today)
                generateDailyMissions(today)
                completeCheckInMission(today)
            } else {
                // If missions empty for today, generate them anyway
                val todayMissions = dailyMissionDao.getMissionsForDate(today)
                if (todayMissions.isEmpty()) {
                    generateDailyMissions(today)
                    completeCheckInMission(today)
                }
            }
        }
        return levelUpOccurred
    }

    private suspend fun generateDailyMissions(date: String) {
        val missions = listOf(
            DailyMission(
                title = "Daily Quest Entry",
                description = "Open LevelUp Study and log ready for the trials.",
                targetCount = 1,
                currentCount = 0,
                isCompleted = false,
                expReward = 15,
                missionType = "CHECK_IN",
                dateString = date
            ),
            DailyMission(
                title = "Campaign: Novice Focus",
                description = "Harvest knowledge by studying for 20 minutes.",
                targetCount = 20,
                currentCount = 0,
                isCompleted = false,
                expReward = 30,
                missionType = "STUDY_MINUTES",
                dateString = date
            ),
            DailyMission(
                title = "Pomodoro Master",
                description = "Charge your focused mana with 2 Pomodoro intervals (50m total).",
                targetCount = 2,
                currentCount = 0,
                isCompleted = false,
                expReward = 50,
                missionType = "POMODORO",
                dateString = date
            ),
            DailyMission(
                title = "Guild Planner",
                description = "Draft 2 study tasks in your Quest Log.",
                targetCount = 2,
                currentCount = 0,
                isCompleted = false,
                expReward = 20,
                missionType = "ADD_TASK",
                dateString = date
            ),
            DailyMission(
                title = "Boss Slayer",
                description = "Vanquish and complete 1 study task.",
                targetCount = 1,
                currentCount = 0,
                isCompleted = false,
                expReward = 25,
                missionType = "COMPLETE_TASK",
                dateString = date
            )
        )
        dailyMissionDao.insertMissions(missions)
    }

    private suspend fun completeCheckInMission(date: String) {
        val missions = dailyMissionDao.getMissionsForDate(date)
        val checkInMission = missions.find { it.missionType == "CHECK_IN" && !it.isCompleted }
        if (checkInMission != null) {
            updateMissionProgress(checkInMission, 1)
        }
    }

    // Shared progress updater for missions
    suspend fun incrementMissionProgress(type: String, increment: Int): Boolean {
        val today = getTodayDateString()
        val missions = dailyMissionDao.getMissionsForDate(today)
        val targetMissions = missions.filter { it.missionType == type && !it.isCompleted }
        var expGained = 0
        var levelledUp = false

        for (mission in targetMissions) {
            val newCount = mission.currentCount + increment
            val completed = newCount >= mission.targetCount
            val updated = mission.copy(
                currentCount = newCount.coerceAtMost(mission.targetCount),
                isCompleted = completed
            )
            dailyMissionDao.updateMission(updated)

            if (completed) {
                expGained += mission.expReward
                // Increment stats of completed missions
                val stats = userStatsDao.getUserStats()
                if (stats != null) {
                    val updatedStats = stats.copy(totalMissionsCompleted = stats.totalMissionsCompleted + 1)
                    userStatsDao.insertOrUpdate(updatedStats)
                }
            }
        }

        if (expGained > 0) {
            levelledUp = addExperience(expGained)
        }
        return levelledUp
    }

    private suspend fun updateMissionProgress(mission: DailyMission, absoluteCount: Int) {
        val completed = absoluteCount >= mission.targetCount
        val updated = mission.copy(
            currentCount = absoluteCount.coerceAtMost(mission.targetCount),
            isCompleted = completed
        )
        dailyMissionDao.updateMission(updated)

        if (completed) {
            addExperience(mission.expReward)
            val stats = userStatsDao.getUserStats()
            if (stats != null) {
                val updatedStats = stats.copy(totalMissionsCompleted = stats.totalMissionsCompleted + 1)
                userStatsDao.insertOrUpdate(updatedStats)
            }
        }
    }

    // Experience modifier
    suspend fun addExperience(amount: Int): Boolean {
        val stats = userStatsDao.getUserStats() ?: return false
        val (updatedStats, levelUpOccurred) = addExperienceInternal(stats, amount)
        userStatsDao.insertOrUpdate(updatedStats)
        return levelUpOccurred
    }

    private fun addExperienceInternal(currentStats: UserStats, addedExp: Int): Pair<UserStats, Boolean> {
        var currentLevel = currentStats.level
        var currentExp = currentStats.experience + addedExp
        var didLevelUp = false

        // Temporary stats container
        var tempStats = currentStats.copy()

        while (true) {
            val nextExpThreshold = 100 + (currentLevel - 1) * 50
            if (currentExp >= nextExpThreshold) {
                currentExp -= nextExpThreshold
                currentLevel += 1
                didLevelUp = true
                
                // Unlock system rewards based on leveled threshold
                val currentUnlockedAvatars = tempStats.unlockedAvatars.split(",").toMutableSet()
                val currentUnlockedSkins = tempStats.unlockedSkins.split(",").toMutableSet()

                when (currentLevel) {
                    2 -> currentUnlockedSkins.add("neon")
                    3 -> currentUnlockedAvatars.add("rogue")
                    4 -> currentUnlockedSkins.add("void")
                    5 -> currentUnlockedAvatars.add("paladin")
                    6 -> currentUnlockedSkins.add("plasma")
                    7 -> currentUnlockedAvatars.add("ranger")
                    8 -> currentUnlockedSkins.add("gold")
                    10 -> currentUnlockedAvatars.add("necro")
                }

                tempStats = tempStats.copy(
                    unlockedAvatars = currentUnlockedAvatars.joinToString(","),
                    unlockedSkins = currentUnlockedSkins.joinToString(",")
                )
            } else {
                break
            }
        }

        val finalStats = tempStats.copy(
            level = currentLevel,
            experience = currentExp
        )
        return Pair(finalStats, didLevelUp)
    }

    // Avatar/Skin triggers
    suspend fun equipAvatar(avatar: String): Boolean {
        val stats = userStatsDao.getUserStats() ?: return false
        if (stats.unlockedAvatars.split(",").contains(avatar)) {
            userStatsDao.insertOrUpdate(stats.copy(equippedAvatar = avatar))
            return true
        }
        return false
    }

    suspend fun equipSkin(skin: String): Boolean {
        val stats = userStatsDao.getUserStats() ?: return false
        if (stats.unlockedSkins.split(",").contains(skin)) {
            userStatsDao.insertOrUpdate(stats.copy(equippedSkin = skin))
            return true
        }
        return false
    }

    // Complete Study Session logging
    suspend fun logStudySession(durationMinutes: Int, soundInput: String): Boolean {
        val studySession = StudySession(
            durationMinutes = durationMinutes,
            focusSound = soundInput
        )
        studySessionDao.insertSession(studySession)

        // Update stats time
        val stats = userStatsDao.getUserStats() ?: return false
        val todayMinLog = stats.todayStudyMinutes + durationMinutes
        val totalMinLog = stats.totalStudyMinutes + durationMinutes

        // 1 EXP awarded for every minute studied, plus 10 bonus exp for ending session!
        val expGained = durationMinutes + 10
        val (updatedStatsTemp, didLevelUp) = addExperienceInternal(stats, expGained)

        val finalStats = updatedStatsTemp.copy(
            todayStudyMinutes = todayMinLog,
            totalStudyMinutes = totalMinLog
        )
        userStatsDao.insertOrUpdate(finalStats)

        // Increment STUDY_MINUTES missions
        incrementMissionProgress("STUDY_MINUTES", durationMinutes)
        
        return didLevelUp
    }

    // Task and Notes DAO updates
    suspend fun insertTaskNote(taskNote: TaskNote) {
        taskNoteDao.insertTaskNote(taskNote)
        if (!taskNote.isNote) {
            incrementMissionProgress("ADD_TASK", 1)
        }
    }

    suspend fun updateTaskNote(taskNote: TaskNote) {
        val oldState = taskNoteDao.getTasksFlow().firstOrNull()?.find { it.id == taskNote.id }
        taskNoteDao.insertTaskNote(taskNote)
        
        // Complete missions check if transition from active to complete
        if (!taskNote.isNote && taskNote.isCompleted && (oldState == null || !oldState.isCompleted)) {
            incrementMissionProgress("COMPLETE_TASK", 1)
        }
    }

    suspend fun deleteTaskNote(taskNote: TaskNote) {
        taskNoteDao.deleteTaskNote(taskNote)
    }

    suspend fun completeMissionsForTest(): Boolean {
        // Complete POMODORO task directly (increment 1)
        return incrementMissionProgress("POMODORO", 1)
    }
}
