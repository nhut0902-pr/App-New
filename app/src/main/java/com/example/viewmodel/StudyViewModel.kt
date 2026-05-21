package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.StudyDatabase
import androidx.room.Room
import com.example.data.model.DailyMission
import com.example.data.model.StudySession
import com.example.data.model.TaskNote
import com.example.data.model.UserStats
import com.example.data.repository.StudyRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TimerState {
    IDLE, RUNNING, PAUSED
}

data class LevelUpEvent(
    val level: Int,
    val rewardType: String,
    val rewardName: String
)

class StudyViewModel(application: Application) : AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        StudyDatabase::class.java,
        "levelup_study_db"
    ).build()

    private val repository = StudyRepository(
        db.userStatsDao(),
        db.dailyMissionDao(),
        db.studySessionDao(),
        db.taskNoteDao()
    )

    // Flow integration for UI
    val userStatsState: StateFlow<UserStats?> = repository.userStatsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val tasksState: StateFlow<List<TaskNote>> = repository.tasksFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notesState: StateFlow<List<TaskNote>> = repository.notesFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val sessionsState: StateFlow<List<StudySession>> = repository.allSessionsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Dynamic daily missions flow
    val dailyMissionsState: StateFlow<List<DailyMission>> = userStatsState
        .flatMapLatest { stats ->
            if (stats != null) {
                repository.getMissionsForDateFlow(stats.lastActiveDate)
            } else {
                flowOf(emptyList())
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Pomodoro Timer States
    private val _timerTimeRemaining = MutableStateFlow(25 * 60) // 25 mins initial
    val timerTimeRemaining = _timerTimeRemaining.asStateFlow()

    private val _timerTotalTime = MutableStateFlow(25 * 60)
    val timerTotalTime = _timerTotalTime.asStateFlow()

    private val _timerState = MutableStateFlow(TimerState.IDLE)
    val timerState = _timerState.asStateFlow()

    // 25m/5m, 50m/10m, CUSTOM
    private val _timerPresetType = MutableStateFlow("25/5")
    val timerPresetType = _timerPresetType.asStateFlow()

    // Focus Sounds
    private val _selectedSound = MutableStateFlow("None")
    val selectedSound = _selectedSound.asStateFlow()

    private val _isSoundPlaying = MutableStateFlow(false)
    val isSoundPlaying = _isSoundPlaying.asStateFlow()

    // Level-up Celebration Overlay
    private val _levelUpEvent = MutableStateFlow<LevelUpEvent?>(null)
    val levelUpEvent = _levelUpEvent.asStateFlow()

    // Coroutine Job for ticking
    private var timerJob: Job? = null

    init {
        // Daily login/check-in validation
        viewModelScope.launch {
            val levelUp = repository.checkDailyTransition()
            if (levelUp) {
                triggerLevelUpNotification()
            }
        }
    }

    // Checking of levelup dialog
    fun dismissLevelUpDialog() {
        _levelUpEvent.value = null
    }

    private suspend fun triggerLevelUpNotification() {
        val stats = repository.userStatsFlow.firstOrNull() ?: db.userStatsDao().getUserStats()
        if (stats != null) {
            val (rewardType, rewardName) = when (stats.level) {
                2 -> Pair("Skin", "neon")
                3 -> Pair("Avatar", "rogue")
                4 -> Pair("Skin", "void")
                5 -> Pair("Avatar", "paladin")
                6 -> Pair("Skin", "plasma")
                7 -> Pair("Avatar", "ranger")
                8 -> Pair("Skin", "gold")
                10 -> Pair("Avatar", "necro")
                else -> Pair("Skin", "random chest bundle")
            }
            _levelUpEvent.value = LevelUpEvent(stats.level, rewardType, rewardName)
        }
    }

    // Pomodoro Configuration Changes
    fun selectTimerPreset(preset: String) {
        _timerPresetType.value = preset
        stopTimer()
        when (preset) {
            "25/5" -> {
                _timerTotalTime.value = 25 * 60
                _timerTimeRemaining.value = 25 * 60
            }
            "50/10" -> {
                _timerTotalTime.value = 50 * 60
                _timerTimeRemaining.value = 50 * 60
            }
            "custom" -> {
                _timerTotalTime.value = 15 * 60 // Default custom to 15m
                _timerTimeRemaining.value = 15 * 60
            }
        }
    }

    fun setCustomTimerMinutes(minutes: Int) {
        if (_timerPresetType.value == "custom") {
            stopTimer()
            val totalSeconds = minutes * 60
            _timerTotalTime.value = totalSeconds
            _timerTimeRemaining.value = totalSeconds
        }
    }

    // Timer control functions
    fun startTimer() {
        if (_timerState.value == TimerState.RUNNING) return

        _timerState.value = TimerState.RUNNING
        timerJob = viewModelScope.launch {
            while (_timerTimeRemaining.value > 0) {
                delay(1000)
                _timerTimeRemaining.value -= 1
            }
            onTimerFinished()
        }
    }

    fun pauseTimer() {
        _timerState.value = TimerState.PAUSED
        timerJob?.cancel()
    }

    fun resumeTimer() {
        startTimer()
    }

    fun stopTimer() {
        _timerState.value = TimerState.IDLE
        timerJob?.cancel()
        _timerTimeRemaining.value = _timerTotalTime.value
    }

    private suspend fun onTimerFinished() {
        _timerState.value = TimerState.IDLE
        val durationMinutes = _timerTotalTime.value / 60

        // Save study session & update user EXP
        val levelUpOccurred = repository.logStudySession(durationMinutes, _selectedSound.value)

        // Increment Pomodoro mission
        val pomomodeLup = repository.incrementMissionProgress("POMODORO", 1)

        if (levelUpOccurred || pomomodeLup) {
            triggerLevelUpNotification()
        }

        // Reset timer
        _timerTimeRemaining.value = _timerTotalTime.value
    }

    // Audio Focus sound controls
    fun selectSound(sound: String) {
        _selectedSound.value = sound
        if (sound == "None") {
            _isSoundPlaying.value = false
        } else {
            _isSoundPlaying.value = true
        }
    }

    fun toggleSoundPlaying() {
        if (_selectedSound.value != "None") {
            _isSoundPlaying.value = !_isSoundPlaying.value
        }
    }

    // Equip rewards
    fun equipAvatarClass(avatarClass: String) {
        viewModelScope.launch {
            repository.equipAvatar(avatarClass)
        }
    }

    fun equipSkinTheme(skinTheme: String) {
        viewModelScope.launch {
            repository.equipSkin(skinTheme)
        }
    }

    // Task list controls
    fun createQuest(title: String, isNote: Boolean = false) {
        viewModelScope.launch {
            val taskNote = TaskNote(
                title = title,
                isCompleted = false,
                isNote = isNote
            )
            repository.insertTaskNote(taskNote)
        }
    }

    fun toggleTaskCompleted(taskNote: TaskNote) {
        viewModelScope.launch {
            val updated = taskNote.copy(isCompleted = !taskNote.isCompleted)
            repository.updateTaskNote(updated)
        }
    }

    fun saveQuickNote(title: String, content: String) {
        viewModelScope.launch {
            val noteEntry = TaskNote(
                title = title,
                content = content,
                isNote = true
            )
            repository.insertTaskNote(noteEntry)
        }
    }

    fun updateQuickNote(note: TaskNote, newTitle: String, newContent: String) {
        viewModelScope.launch {
            val updated = note.copy(title = newTitle, content = newContent)
            repository.updateTaskNote(updated)
        }
    }

    fun deleteQuestNote(taskNote: TaskNote) {
        viewModelScope.launch {
            repository.deleteTaskNote(taskNote)
        }
    }

    // Force Level up logic for quick demo/gamify trials
    fun forceLevelUpForShow() {
        viewModelScope.launch {
            val stats = repository.userStatsFlow.firstOrNull() ?: db.userStatsDao().getUserStats()
            if (stats != null) {
                // Instantly gain needed exp to level up
                val currentNeeded = stats.maxExpForLevel() - stats.experience
                val levelUp = repository.addExperience(currentNeeded)
                if (levelUp) {
                    triggerLevelUpNotification()
                }
            }
        }
    }

    // Complete Pomodoro Campaign immediately for users wanting fast satisfaction
    fun instantFinishTimerForTrial() {
        viewModelScope.launch {
            val (levelUp) = Pair(onTimerFinished(), Unit)
        }
    }

    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
        db.close()
    }
}
