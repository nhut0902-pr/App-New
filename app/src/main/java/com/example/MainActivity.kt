package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.Column
import com.example.ui.components.LevelUpDialog
import com.example.ui.screens.AvatarScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PomodoroScreen
import com.example.ui.screens.StatsScreen
import com.example.ui.screens.TodoNoteScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.StudyViewModel
import com.example.ui.theme.DeepSlateBg
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

sealed class TabScreen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : TabScreen("dashboard", "Camp", Icons.Default.Dashboard)
    object Focus : TabScreen("focus", "Timer", Icons.Default.HourglassTop)
    object Quests : TabScreen("quests", "Quests", Icons.Default.Assignment)
    object Avatar : TabScreen("avatar", "Vault", Icons.Default.Shield)
    object Stats : TabScreen("stats", "Chronicle", Icons.Default.BarChart)
}

class MainActivity : ComponentActivity() {

    private val studyViewModel: StudyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                // UI state collects safely with lifecycle
                val userStats by studyViewModel.userStatsState.collectAsStateWithLifecycle()
                val missions by studyViewModel.dailyMissionsState.collectAsStateWithLifecycle()
                val tasks by studyViewModel.tasksState.collectAsStateWithLifecycle()
                val notes by studyViewModel.notesState.collectAsStateWithLifecycle()
                val sessions by studyViewModel.sessionsState.collectAsStateWithLifecycle()

                // Onboarding state triggers
                var showOnboarding by remember { mutableStateOf(false) }

                // Synchronize onboarding state with whether stats is empty
                LaunchedEffect(userStats) {
                    if (userStats == null) {
                        showOnboarding = true
                    } else {
                        showOnboarding = false
                    }
                }

                if (showOnboarding) {
                    OnboardingScreen(
                        onFinished = { starterClass ->
                            studyViewModel.equipAvatarClass(starterClass)
                            showOnboarding = false
                        }
                    )
                } else {
                    MainAppLayout(
                        viewModel = studyViewModel,
                        userStats = userStats,
                        missions = missions,
                        tasks = tasks,
                        notes = notes,
                        sessions = sessions
                    )
                }
            }
        }
    }
}

@Composable
fun MainAppLayout(
    viewModel: StudyViewModel,
    userStats: com.example.data.model.UserStats?,
    missions: List<com.example.data.model.DailyMission>,
    tasks: List<com.example.data.model.TaskNote>,
    notes: List<com.example.data.model.TaskNote>,
    sessions: List<com.example.data.model.StudySession>
) {
    var activeTab by remember { mutableStateOf<TabScreen>(TabScreen.Dashboard) }

    // Pomodoro flow state collectors
    val timeRemaining by viewModel.timerTimeRemaining.collectAsStateWithLifecycle()
    val totalSeconds by viewModel.timerTotalTime.collectAsStateWithLifecycle()
    val timerState by viewModel.timerState.collectAsStateWithLifecycle()
    val presetType by viewModel.timerPresetType.collectAsStateWithLifecycle()
    val selectedSound by viewModel.selectedSound.collectAsStateWithLifecycle()
    val isSoundPlaying by viewModel.isSoundPlaying.collectAsStateWithLifecycle()

    // Level-up overlays
    val levelUpEvent by viewModel.levelUpEvent.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepSlateBg),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            // High fidelity modern glowing gaming bottom navigation bar
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                containerColor = Color(0xFF130E26),
                tonalElevation = 8.dp
            ) {
                val screens = listOf(
                    TabScreen.Dashboard,
                    TabScreen.Focus,
                    TabScreen.Quests,
                    TabScreen.Avatar,
                    TabScreen.Stats
                )

                screens.forEach { screen ->
                    val isSelected = activeTab.route == screen.route
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { activeTab = screen },
                        modifier = Modifier.testTag("nav_btn_${screen.route}"),
                        icon = {
                            Icon(
                                imageVector = screen.icon,
                                contentDescription = screen.title,
                                modifier = Modifier.padding(bottom = 2.dp)
                            )
                        },
                        label = {
                            Text(
                                text = screen.title.uppercase(),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonCyan,
                            selectedTextColor = NeonCyan,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = Color(0xFF1E143E)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        // Active display screen body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepSlateBg)
                .statusBarsPadding()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (activeTab) {
                TabScreen.Dashboard -> {
                    DashboardScreen(
                        stats = userStats,
                        missions = missions,
                        onQuickStudyClicked = { activeTab = TabScreen.Focus },
                        onNavigateToMissions = { activeTab = TabScreen.Quests }
                    )
                }

                TabScreen.Focus -> {
                    PomodoroScreen(
                        timeRemainingSeconds = timeRemaining,
                        totalSeconds = totalSeconds,
                        timerState = timerState,
                        presetType = presetType,
                        selectedSound = selectedSound,
                        isSoundPlaying = isSoundPlaying,
                        onPresetSelected = { viewModel.selectTimerPreset(it) },
                        onSetCustomMinutes = { viewModel.setCustomTimerMinutes(it) },
                        onStartTimer = { viewModel.startTimer() },
                        onPauseTimer = { viewModel.pauseTimer() },
                        onResetTimer = { viewModel.stopTimer() },
                        onSelectSound = { viewModel.selectSound(it) },
                        onToggleSound = { viewModel.toggleSoundPlaying() },
                        onInstantFinish = { viewModel.instantFinishTimerForTrial() }
                    )
                }

                TabScreen.Quests -> {
                    // Combine missions + checklists in scroll list
                    Column(modifier = Modifier.fillMaxSize()) {
                        TodoNoteScreen(
                            tasks = tasks,
                            notes = notes,
                            onCreateQuest = { viewModel.createQuest(it) },
                            onToggleQuest = { viewModel.toggleTaskCompleted(it) },
                            onSaveNote = { title, content -> viewModel.saveQuickNote(title, content) },
                            onDeleteTaskNote = { viewModel.deleteQuestNote(it) }
                        )
                    }
                }

                TabScreen.Avatar -> {
                    AvatarScreen(
                        stats = userStats,
                        onEquipAvatar = { viewModel.equipAvatarClass(it) },
                        onEquipSkin = { viewModel.equipSkinTheme(it) }
                    )
                }

                TabScreen.Stats -> {
                    StatsScreen(
                        stats = userStats,
                        sessions = sessions
                    )
                }
            }

            // Beautiful Level Up Celebration Dialog triggered from Live State
            levelUpEvent?.let { event ->
                LevelUpDialog(
                    newLevel = event.level,
                    rewardType = event.rewardType,
                    rewardName = event.rewardName,
                    onDismiss = { viewModel.dismissLevelUpDialog() }
                )
            }
        }
    }
}
