package com.example.data.local

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update
import com.example.data.model.DailyMission
import com.example.data.model.StudySession
import com.example.data.model.TaskNote
import com.example.data.model.UserStats
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStatsDao {
    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    fun getUserStatsFlow(): Flow<UserStats?>

    @Query("SELECT * FROM user_stats WHERE id = 1 LIMIT 1")
    suspend fun getUserStats(): UserStats?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: UserStats)

    @Update
    suspend fun update(stats: UserStats)
}

@Dao
interface DailyMissionDao {
    @Query("SELECT * FROM daily_missions WHERE dateString = :date ORDER BY id ASC")
    fun getMissionsForDateFlow(date: String): Flow<List<DailyMission>>

    @Query("SELECT * FROM daily_missions WHERE dateString = :date ORDER BY id ASC")
    suspend fun getMissionsForDate(date: String): List<DailyMission>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMissions(missions: List<DailyMission>)

    @Update
    suspend fun updateMission(mission: DailyMission)

    @Query("DELETE FROM daily_missions WHERE dateString < :date")
    suspend fun deleteOldMissions(date: String)
}

@Dao
interface StudySessionDao {
    @Query("SELECT * FROM study_sessions ORDER BY timestamp DESC")
    fun getAllSessionsFlow(): Flow<List<StudySession>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: StudySession)

    @Query("SELECT SUM(durationMinutes) FROM study_sessions WHERE timestamp >= :sinceTimestamp")
    fun getTotalMinutesSinceFlow(sinceTimestamp: Long): Flow<Int?>
}

@Dao
interface TaskNoteDao {
    @Query("SELECT * FROM task_notes WHERE isNote = 0 ORDER BY isCompleted ASC, createdAt DESC")
    fun getTasksFlow(): Flow<List<TaskNote>>

    @Query("SELECT * FROM task_notes WHERE isNote = 1 ORDER BY createdAt DESC")
    fun getNotesFlow(): Flow<List<TaskNote>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskNote(taskNote: TaskNote)

    @Update
    suspend fun updateTaskNote(taskNote: TaskNote)

    @Delete
    suspend fun deleteTaskNote(taskNote: TaskNote)

    @Query("DELETE FROM task_notes WHERE id = :id")
    suspend fun deleteById(id: Int)
}

@Database(
    entities = [UserStats::class, DailyMission::class, StudySession::class, TaskNote::class],
    version = 1,
    exportSchema = false
)
abstract class StudyDatabase : RoomDatabase() {
    abstract fun userStatsDao(): UserStatsDao
    abstract fun dailyMissionDao(): DailyMissionDao
    abstract fun studySessionDao(): StudySessionDao
    abstract fun taskNoteDao(): TaskNoteDao
}
