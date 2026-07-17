package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SentenceDao {
    @Query("SELECT * FROM sentences ORDER BY id ASC")
    fun getAllSentences(): Flow<List<SentenceEntity>>

    @Query("SELECT * FROM sentences WHERE lessonId = :lessonId ORDER BY id ASC")
    fun getSentencesByLesson(lessonId: Int): Flow<List<SentenceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSentences(sentences: List<SentenceEntity>)

    @Query("DELETE FROM sentences")
    suspend fun deleteAllSentences()

    @Query("SELECT * FROM user_progress")
    fun getAllUserProgress(): Flow<List<UserProgressEntity>>

    @Query("SELECT * FROM user_progress WHERE sentenceId = :sentenceId")
    suspend fun getProgressForSentence(sentenceId: Int): UserProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(progress: UserProgressEntity)

    @Query("SELECT * FROM app_settings WHERE `key` = :key")
    suspend fun getSetting(key: String): AppSettingEntity?

    @Query("SELECT * FROM app_settings")
    fun getAllSettings(): Flow<List<AppSettingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSetting(setting: AppSettingEntity)
}
